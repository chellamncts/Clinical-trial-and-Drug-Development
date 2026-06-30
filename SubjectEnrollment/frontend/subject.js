// Subject Enrollment Module — standalone microservice logic
// No authentication required; talks directly to http://localhost:8082/api

/* ── Section navigation ─────────────────────────────────────────── */
const SEC_TITLES = { dashboard: "Dashboard", enroll: "Screen & Enroll", subjects: "All Subjects" };

document.querySelectorAll(".nav-item").forEach(b => b.addEventListener("click", () => {
  document.querySelectorAll(".nav-item").forEach(x => x.classList.remove("active"));
  document.querySelectorAll(".sec").forEach(x => x.classList.remove("active"));
  b.classList.add("active");
  document.getElementById(b.dataset.sec).classList.add("active");
  document.getElementById("secTitle").textContent = SEC_TITLES[b.dataset.sec];
}));

/* ── Utilities ───────────────────────────────────────────────────── */
function pretty(s) {
  return s ? s.replace(/_/g, " ").replace(/\w\S*/g, w => w[0] + w.slice(1).toLowerCase()) : "—";
}

function badge(s) {
  const m = { SCREENED: "b-draft", ENROLLED: "b-active", COMPLETED: "b-approved", WITHDRAWN: "b-closed" };
  return `<span class="badge ${m[s] || "b-draft"}">${pretty(s)}</span>`;
}

function toggleForm(id) { document.getElementById(id).classList.toggle("hidden"); }

function today() { return new Date().toISOString().split("T")[0]; }

/* ── State ───────────────────────────────────────────────────────── */
let SUBJECTS = [];

/* ── Screen a new subject ────────────────────────────────────────── */
async function screenSubject() {
  const protocolId = document.getElementById("protocolId").value.trim();
  const siteId     = document.getElementById("siteId").value.trim();
  const studyArm   = document.getElementById("studyArm").value.trim();
  const smsg       = document.getElementById("smsg");

  if (!protocolId) { smsg.innerHTML = '<div class="msg err">Protocol ID is required.</div>'; return; }

  try {
    await api("/subjects/screen", "POST", {
      protocolId: parseInt(protocolId),
      siteId:     siteId ? parseInt(siteId) : null,
      studyArm:   studyArm || null,
      screeningDate: today()
    });
    smsg.innerHTML = '<div class="msg ok">Subject screened successfully.</div>';
    await load();
    setTimeout(() => { toggleForm("screenForm"); smsg.innerHTML = ""; }, 900);
  } catch (e) {
    smsg.innerHTML = `<div class="msg err">${e.message}</div>`;
  }
}

/* ── Open consent modal: show subject details ────────────────────── */
async function openConsent() {
  const subjectId = document.getElementById("actId").value;
  if (!subjectId) return;
  toggleForm("consentForm");
  document.getElementById("consentAccept").checked = false;
  document.getElementById("bConsent").disabled = true;
  document.getElementById("cmsg").innerHTML = "";

  const s = SUBJECTS.find(x => x.subjectId === +subjectId);
  const body = document.getElementById("consentBody");
  if (s) {
    body.innerHTML = `
      <div class="consent-block">
        <h3><i class="bi bi-person-vcard"></i> Subject Details</h3>
        <p class="consent-title">Subject #${s.subjectId}</p>
        <p class="consent-sub">Protocol ID: ${s.protocolId} &nbsp;·&nbsp; Site ID: ${s.siteId || "—"} &nbsp;·&nbsp; Study Arm: ${pretty(s.studyArm)}</p>
      </div>
      <div class="consent-grid">
        <div class="consent-crit ok">
          <h4><i class="bi bi-calendar-check"></i> Screened On</h4>
          <p>${s.screeningDate || "—"}</p>
        </div>
        <div class="consent-crit no">
          <h4><i class="bi bi-info-circle"></i> Current Status</h4>
          <p>${pretty(s.subjectStatus)}</p>
        </div>
      </div>
      <div style="display:grid;grid-template-columns:1fr 1fr;gap:12px;margin-bottom:12px;">
        <div>
          <label for="cvVersion">Consent Version *</label>
          <input type="text" id="cvVersion" placeholder="e.g. v1.0" value="v1.0" />
        </div>
        <div>
          <label for="cvDate">Consent Date *</label>
          <input type="date" id="cvDate" value="${today()}" />
        </div>
        <div style="grid-column:1/-1">
          <label for="cvBy">Consented By</label>
          <input type="text" id="cvBy" placeholder="Name of person capturing consent" />
        </div>
      </div>`;
  } else {
    body.innerHTML = '<div class="msg err">Subject not found.</div>';
  }
}

/* ── Capture consent (enroll subject) ────────────────────────────── */
async function consent() {
  const subjectId = document.getElementById("actId").value;
  const accepted  = document.getElementById("consentAccept").checked;
  const cmsg      = document.getElementById("cmsg");
  if (!accepted) { cmsg.innerHTML = '<div class="msg err">Please confirm the subject accepts consent.</div>'; return; }

  const form = {
    consentVersion: (document.getElementById("cvVersion")?.value || "v1.0").trim(),
    consentDate:    document.getElementById("cvDate")?.value || today(),
    consentedBy:    (document.getElementById("cvBy")?.value || "").trim(),
    notes:          "Consent captured via dashboard"
  };

  try {
    await api("/subjects/" + subjectId + "/consent", "PUT", form);
    // After consent, enroll the subject
    await api("/subjects/" + subjectId + "/enroll", "PUT");
    cmsg.innerHTML = '<div class="msg ok">Consent captured — subject enrolled.</div>';
    await load();
    setTimeout(() => { toggleForm("consentForm"); cmsg.innerHTML = ""; }, 900);
  } catch (e) {
    cmsg.innerHTML = `<div class="msg err">${e.message}</div>`;
  }
}

/* ── Withdraw / Complete ─────────────────────────────────────────── */
async function withdraw() {
  const subjectId = document.getElementById("actId").value;
  const reason    = prompt("Enter withdrawal reason:") || "No reason provided";
  const msg       = document.getElementById("msg");
  try {
    await api("/subjects/" + subjectId + "/withdraw", "PUT", { reason });
    msg.innerHTML = '<div class="msg ok">Subject withdrawn.</div>';
    await load();
  } catch (e) {
    msg.innerHTML = `<div class="msg err">${e.message}</div>`;
  }
}

async function complete() {
  const subjectId = document.getElementById("actId").value;
  const msg       = document.getElementById("msg");
  try {
    await api("/subjects/" + subjectId + "/complete", "PUT");
    msg.innerHTML = '<div class="msg ok">Subject marked as completed.</div>';
    await load();
  } catch (e) {
    msg.innerHTML = `<div class="msg err">${e.message}</div>`;
  }
}

/* ── Subject status stepper ──────────────────────────────────────── */
function subjStepper(s) {
  const steps = [
    { l: "Screened",  i: "bi-clipboard-check" },
    { l: "Enrolled",  i: "bi-shuffle" },
    { l: "Completed", i: "bi-check2-all" }
  ];
  const idx      = ({ SCREENED: 0, ENROLLED: 1, COMPLETED: 2, WITHDRAWN: 1 })[s.subjectStatus] ?? 0;
  const withdrawn = s.subjectStatus === "WITHDRAWN";
  const inner = steps.map((st, i) => {
    const cls = i < idx ? "done" : (i === idx && !withdrawn ? "active" : "");
    return `<div class="pstep ${cls}"><span class="pstep-dot"><i class="bi ${st.i}"></i></span><span class="pstep-l">${st.l}</span></div>`;
  }).join('<span class="pstep-line"></span>');
  return `<div class="pstepper">${inner}</div>` +
    (withdrawn ? '<div class="withdrawn-note"><i class="bi bi-person-dash"></i> Subject withdrawn</div>' : "");
}

/* ── Subject detail card ─────────────────────────────────────────── */
function showSubject() {
  const subjectId = document.getElementById("actId").value;
  const s         = SUBJECTS.find(x => x.subjectId === +subjectId);
  const box       = document.getElementById("subjDetail");
  const set       = (id, on) => { const b = document.getElementById(id); if (b) b.disabled = !on; };

  if (!s) {
    box.innerHTML = '<div class="subj-empty"><i class="bi bi-person-bounding-box"></i><p>Select a subject to view details and take action.</p></div>';
    set("bConsentOpen", false); set("bComplete", false); set("bWithdraw", false);
    return;
  }

  box.innerHTML = `
    <div class="subj-hero">
      <div class="subj-avatar"><i class="bi bi-person-fill"></i></div>
      <div class="subj-hero-text">
        <div class="subj-name">Subject #${s.subjectId}</div>
        <div class="subj-sub">Protocol #${s.protocolId} &nbsp;·&nbsp; Site #${s.siteId || "—"}</div>
      </div>
      ${badge(s.subjectStatus)}
    </div>
    ${subjStepper(s)}
    <div class="info-grid">
      <div class="ig"><span class="ig-l"><i class="bi bi-diagram-2"></i> Study Arm</span><span class="ig-v">${pretty(s.studyArm)}</span></div>
      <div class="ig"><span class="ig-l"><i class="bi bi-file-earmark-medical"></i> Consent</span><span class="ig-v">${s.consentVersion ? `<i class="bi bi-check-circle-fill" style="color:var(--ok)"></i> ${s.consentVersion}` : '<span class="ig-v mut">Pending</span>'}</span></div>
      <div class="ig"><span class="ig-l"><i class="bi bi-calendar-check"></i> Enrolled</span><span class="ig-v">${s.enrollmentDate || "—"}</span></div>
      <div class="ig"><span class="ig-l"><i class="bi bi-calendar"></i> Screened</span><span class="ig-v">${s.screeningDate || "—"}</span></div>
    </div>`;

  set("bConsentOpen", s.subjectStatus === "SCREENED");
  set("bComplete",    s.subjectStatus === "ENROLLED");
  set("bWithdraw",    s.subjectStatus !== "WITHDRAWN" && s.subjectStatus !== "COMPLETED");
}

/* ── Subjects table ──────────────────────────────────────────────── */
function subjTable(d) {
  if (!d.length) return '<div class="empty">No subjects yet — screen one first.</div>';
  return `<table>
    <thead><tr><th>ID</th><th>Protocol</th><th>Site</th><th>Study Arm</th><th>Screened</th><th>Enrolled</th><th>Status</th></tr></thead>
    <tbody>${d.map(s => `
      <tr>
        <td><b>#${s.subjectId}</b></td>
        <td>${s.protocolId}</td>
        <td>${s.siteId || "—"}</td>
        <td>${pretty(s.studyArm)}</td>
        <td>${s.screeningDate || "—"}</td>
        <td>${s.enrollmentDate || "—"}</td>
        <td>${badge(s.subjectStatus)}</td>
      </tr>`).join("")}
    </tbody>
  </table>`;
}

/* ── Subject select dropdown ─────────────────────────────────────── */
function fillSubjectSelect() {
  const sel = document.getElementById("actId");
  if (!SUBJECTS.length) {
    sel.disabled = true;
    sel.innerHTML = '<option value="">No subjects yet</option>';
    return;
  }
  sel.disabled = false;
  sel.innerHTML = '<option value="">Select a subject…</option>' +
    SUBJECTS.map(s => `<option value="${s.subjectId}">#${s.subjectId} · Protocol ${s.protocolId} · ${pretty(s.subjectStatus)}</option>`).join("");
  sel.value = "";
}

/* ── Load all subjects from the microservice ─────────────────────── */
async function load() {
  try {
    const d = await api("/subjects");
    SUBJECTS = d;
    document.getElementById("tbox").innerHTML     = subjTable(d);
    document.getElementById("dSubjects").innerHTML = subjTable(d);
    document.getElementById("cTotal").textContent     = d.length;
    document.getElementById("cScreened").textContent  = d.filter(s => s.subjectStatus === "SCREENED").length;
    document.getElementById("cEnrolled").textContent  = d.filter(s => s.subjectStatus === "ENROLLED").length;
    document.getElementById("cCompleted").textContent = d.filter(s => s.subjectStatus === "COMPLETED").length;
    fillSubjectSelect();
    showSubject();
  } catch (e) {
    console.error("Failed to load subjects:", e.message);
  }
}

/* ── Bootstrap ───────────────────────────────────────────────────── */
load();

