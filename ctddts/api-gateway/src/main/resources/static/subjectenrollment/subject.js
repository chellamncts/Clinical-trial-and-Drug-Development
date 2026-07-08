// Subject Enrollment Module — logic (routes through API Gateway)

// ── Auth guard: only INVESTIGATOR (and ADMIN) may enter ──────────────
requireRole("INVESTIGATOR", "COORDINATOR");

// ── Show signed-in user ──────────────────────────────────────────────
const whoEl = document.getElementById("who");
if (whoEl) whoEl.textContent = localStorage.getItem("username") || "user";

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

function toggleForm(id) {
  if (id === "screenForm") {
    const el = document.getElementById("screenForm");
    if (el.classList.contains("hidden")) openScreenForm();
    else closeScreenForm();
  } else {
    document.getElementById(id).classList.toggle("hidden");
  }
}

function today() { return new Date().toISOString().split("T")[0]; }

/* ── State ───────────────────────────────────────────────────────── */
let SUBJECTS  = [];
let PROTOCOLS = [];   // cache of ACTIVE protocols
let ALL_SITES = [];   // cache of all sites

/* ═══════════════════════════════════════════════════════════════════
   SCREEN FORM — cascading Protocol → Site dropdowns
   ═══════════════════════════════════════════════════════════════════ */

/** Open the Screen Subject modal and pre-load protocol list */
async function openScreenForm() {
  document.getElementById("screenForm").classList.remove("hidden");
  document.getElementById("smsg").innerHTML = "";
  document.getElementById("studyArm").value = "";
  document.getElementById("bScreenSubject").disabled = true;

  const pSel = document.getElementById("protocolId");
  const sSel = document.getElementById("siteId");
  pSel.innerHTML = '<option value="">Loading protocols…</option>';
  pSel.disabled = true;
  sSel.innerHTML = '<option value="">— Select a protocol first —</option>';
  sSel.disabled = true;

  try {
    const protocols = await api("/api/protocols");
    PROTOCOLS = protocols.filter(p => p.protocolStatus === "ACTIVE");

    if (!PROTOCOLS.length) {
      pSel.innerHTML = '<option value="">No ACTIVE protocols — activate one first</option>';
      document.getElementById("smsg").innerHTML =
        '<div class="msg err">No ACTIVE protocols found. Please activate a protocol in the Admin console before screening subjects.</div>';
      return;
    }

    pSel.innerHTML = '<option value="">— Select a protocol —</option>' +
      PROTOCOLS.map(p =>
        `<option value="${p.protocolId}">#${p.protocolId} · ${p.trialTitle} (${p.phase ? "Phase " + p.phase.replace("PHASE_","") : ""})</option>`
      ).join("");
    pSel.disabled = false;

    // Pre-load all sites so the site dropdown can be filtered client-side
    const sites = await api("/api/protocols/sites");
    ALL_SITES = sites;

  } catch (e) {
    pSel.innerHTML = '<option value="">Error loading protocols</option>';
    document.getElementById("smsg").innerHTML =
      `<div class="msg err">Could not load protocols: ${e.message}</div>`;
  }
}

/** When a protocol is selected, filter the site dropdown */
function onProtocolChange() {
  const protocolId = parseInt(document.getElementById("protocolId").value);
  const sSel       = document.getElementById("siteId");
  const btn        = document.getElementById("bScreenSubject");

  // Reset site and button
  sSel.innerHTML = "";
  sSel.disabled  = true;
  btn.disabled   = true;

  if (!protocolId) {
    sSel.innerHTML = '<option value="">— Select a protocol first —</option>';
    return;
  }

  // Filter ACTIVE sites belonging to this protocol
  const activeSites = ALL_SITES.filter(s => s.protocolId === protocolId && s.siteStatus === "ACTIVE");

  if (!activeSites.length) {
    sSel.innerHTML = '<option value="">No ACTIVE sites for this protocol</option>';
    document.getElementById("smsg").innerHTML =
      '<div class="msg err">This protocol has no ACTIVE sites. Please activate a site in the Admin console first.</div>';
    return;
  }

  document.getElementById("smsg").innerHTML = "";
  sSel.innerHTML = '<option value="">— Select a site —</option>' +
    activeSites.map(s =>
      `<option value="${s.siteId}">#${s.siteId} · ${s.siteName || "Site " + s.siteId} (${s.location || ""})</option>`
    ).join("");
  sSel.disabled = false;

  // Enable Screen button only when site is also chosen
  sSel.onchange = () => { btn.disabled = !sSel.value; };
}

/** Close + reset the Screen Subject form */
function closeScreenForm() {
  document.getElementById("screenForm").classList.add("hidden");
  document.getElementById("smsg").innerHTML = "";
  document.getElementById("protocolId").innerHTML = '<option value="">—</option>';
  document.getElementById("siteId").innerHTML     = '<option value="">—</option>';
  document.getElementById("siteId").disabled      = true;
  document.getElementById("studyArm").value       = "";
  document.getElementById("bScreenSubject").disabled = true;
}

/* ── Screen a new subject ────────────────────────────────────────── */
async function screenSubject() {
  const protocolId = document.getElementById("protocolId").value;
  const siteId     = document.getElementById("siteId").value;
  const studyArm   = document.getElementById("studyArm").value.trim();
  const smsg       = document.getElementById("smsg");

  if (!protocolId) { smsg.innerHTML = '<div class="msg err">Please select a protocol.</div>'; return; }
  if (!siteId)     { smsg.innerHTML = '<div class="msg err">Please select a site.</div>'; return; }

  try {
    await api("/api/subjects/screen", "POST", {
      protocolId:    parseInt(protocolId),
      siteId:        parseInt(siteId),
      studyArm:      studyArm || null,
      screeningDate: today()
    });
    smsg.innerHTML = '<div class="msg ok">Subject screened successfully.</div>';
    await load();
    setTimeout(() => closeScreenForm(), 900);
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

async function consent() {
  const subjectId = document.getElementById("actId").value;
  const accepted  = document.getElementById("consentAccept").checked;
  const cmsg      = document.getElementById("cmsg");
  if (!accepted) { cmsg.innerHTML = '<div class="msg err">Please confirm the subject accepts consent.</div>'; return; }

  const form = {
    consentDate:    document.getElementById("cvDate")?.value || today(),
    consentedBy:    (document.getElementById("cvBy")?.value || "").trim(),
    notes:          "Consent captured via dashboard"
  };

  try {
    await api("/api/subjects/" + subjectId + "/consent", "PUT", form);
    await api("/api/subjects/" + subjectId + "/enroll", "PUT");
    cmsg.innerHTML = '<div class="msg ok">Consent captured — subject enrolled.</div>';
    await load();
    setTimeout(() => { toggleForm("consentForm"); cmsg.innerHTML = ""; }, 900);
  } catch (e) {
    cmsg.innerHTML = `<div class="msg err">${e.message}</div>`;
  }
}
async function withdraw() {
  const subjectId = document.getElementById("actId").value;
  const reason    = prompt("Enter withdrawal reason:");
  const msg       = document.getElementById("msg");

  if (!reason) {
    msg.innerHTML = '<div class="msg err">Withdrawal cancelled — no reason provided.</div>';
    return;
  }

  try {
    await api("/api/subjects/" + subjectId + "/withdraw", "PUT", { reason });
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
    await api("/api/subjects/" + subjectId + "/complete", "PUT");
    msg.innerHTML = '<div class="msg ok">Subject marked as completed.</div>';
    await load();
  } catch (e) {
    msg.innerHTML = `<div class="msg err">${e.message}</div>`;
  }
}

function subjStepper(s) {
  const steps = [
    { l: "Screened",  i: "bi-clipboard-check" },
    { l: "Enrolled",  i: "bi-shuffle" },
    { l: "Completed", i: "bi-check2-all" }
  ];
  const idx       = ({ SCREENED: 0, ENROLLED: 1, COMPLETED: 2, WITHDRAWN: 1 })[s.subjectStatus] ?? 0;
  const withdrawn = s.subjectStatus === "WITHDRAWN";
  const inner = steps.map((st, i) => {
    const cls = i < idx ? "done" : (i === idx && !withdrawn ? "active" : "");
    return `<div class="pstep ${cls}"><span class="pstep-dot"><i class="bi ${st.i}"></i></span><span class="pstep-l">${st.l}</span></div>`;
  }).join('<span class="pstep-line"></span>');
  return `<div class="pstepper">${inner}</div>` +
    (withdrawn ? '<div class="withdrawn-note"><i class="bi bi-person-dash"></i> Subject withdrawn</div>' : "");
}

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
        <div class="subj-name">Subject ${s.subjectId}</div>
        <div class="subj-sub">Protocol ${s.protocolId} &nbsp;·&nbsp; Site #${s.siteId || "—"}</div>
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

function fillSubjectSelect() {
  const sel = document.getElementById("actId");
  if (!SUBJECTS.length) {
    sel.disabled = true;
    sel.innerHTML = '<option value="">No subjects yet</option>';
    return;
  }
  sel.disabled = false;
  sel.innerHTML = '<option value="">Select a subject…</option>' +
    SUBJECTS.map(s => `<option value="${s.subjectId}">${s.subjectId} · Protocol ${s.protocolId} · ${pretty(s.subjectStatus)}</option>`).join("");
  sel.value = "";
}

async function load() {
  try {
    const d = await api("/api/subjects");
    SUBJECTS = d;
    document.getElementById("tbox").innerHTML      = subjTable(d);
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

load();
