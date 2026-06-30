// Show signed-in user
const whoEl = document.getElementById("who");
if (whoEl) whoEl.textContent = "Signed in as " + (localStorage.getItem("username") || "user");

// Adverse Event (Pharmacovigilance) module logic
requireRole("PHARMACOVIGILANCE_OFFICER");

// Sidebar section switching
document.querySelectorAll(".nav-item").forEach(b => b.onclick = () => {
  document.querySelectorAll(".nav-item").forEach(x => x.classList.remove("active"));
  document.querySelectorAll(".sec").forEach(s => s.classList.remove("active"));
  b.classList.add("active");
  const sec = document.getElementById(b.dataset.sec);
  if (sec) sec.classList.add("active");
  const t = document.getElementById("secTitle");
  if (t) t.textContent = b.textContent.trim();
  // Reload subjects when opening Report Event tab
  if (b.dataset.sec === "report") loadSubjects();
});

function pretty(s){ return s ? s.replace(/_/g, " ").replace(/\w\S*/g, w => w[0] + w.slice(1).toLowerCase()) : "-"; }
function badge(s){ const m = { REPORTED:"b-draft", UNDER_REVIEW:"b-approved", RESOLVED:"b-active", FATAL:"b-closed" }; return `<span class="badge ${m[s]||"b-draft"}">${pretty(s)}</span>`; }
function sevPill(s){ return `<span class="sev-pill ${s||""}"><i class="bi bi-exclamation-octagon"></i>${s||"-"}</span>`; }
function fmtDate(d){ return d ? new Date(d).toLocaleDateString("en-GB", { day:"2-digit", month:"short", year:"numeric" }) : "-"; }

let EVENTS = [];
let SUBJECTS = [];

function eventStepper(e){
  const steps = [
    { l:"Reported", i:"bi-exclamation-triangle" },
    { l:"Under Review", i:"bi-search" },
    { l:"Resolved", i:"bi-check2-circle" }
  ];
  const order = ["REPORTED", "UNDER_REVIEW", "RESOLVED", "FATAL"];
  const idx = order.indexOf(e.eventStatus);
  const fatal = e.eventStatus === "FATAL";
  const inner = steps.map((st, i) => {
    const cls = i < idx ? "done" : (i === idx && !fatal ? "active" : "");
    return `<div class="pstep ${cls}"><span class="pstep-dot"><i class="bi ${st.i}"></i></span><span class="pstep-l">${st.l}</span></div>${i < steps.length - 1 ? '<span class="pstep-line"></span>' : ""}`;
  }).join("");
  return `<div class="pstepper">${inner}</div>${fatal ? '<div class="fatal-note"><i class="bi bi-heart-pulse"></i> Fatal event recorded</div>' : ""}`;
}

function updateStats(){
  const total = EVENTS.length;
  const reported = EVENTS.filter(e => e.eventStatus === "REPORTED").length;
  const review = EVENTS.filter(e => e.eventStatus === "UNDER_REVIEW").length;
  const resolved = EVENTS.filter(e => e.eventStatus === "RESOLVED" || e.eventStatus === "FATAL").length;
  document.getElementById("cTotal").textContent = total;
  document.getElementById("cReported").textContent = reported;
  document.getElementById("cReview").textContent = review;
  document.getElementById("cResolved").textContent = resolved;
}

function renderTable(list){
  const box = document.getElementById("tbox");
  if (!box) return;
  if (!list || !list.length) {
    box.innerHTML = '<div class="evt-empty"><i class="bi bi-shield-exclamation"></i><p>No adverse events found.</p></div>';
    return;
  }
  box.innerHTML = `<table class="event-table">
    <thead><tr>
      <th>ID</th><th>Subject</th><th>Visit</th><th>Severity</th><th>Seriousness</th><th>SAE</th><th>Status</th><th>Description</th>
    </tr></thead>
    <tbody>
      ${list.map(e => `<tr>
        <td class="event-id">#${e.eventId}</td>
        <td>Subject #${e.subjectId}</td>
        <td>${e.visitId ? `<span class="badge b-draft">Visit #${e.visitId}</span>` : '<span style="color:var(--muted);font-size:.8rem">—</span>'}</td>
        <td>${sevPill(e.severity)}</td>
        <td>${e.seriousness || "-"}</td>
        <td>${e.safetyReportSubmitted ? '<span class="badge b-active">Submitted</span>' : '<span class="badge b-draft">Pending</span>'}</td>
        <td>${badge(e.eventStatus)}</td>
        <td class="event-desc" title="${(e.eventDescription || "").replace(/"/g, "&quot;")}">${e.eventDescription || "-"}</td>
      </tr>`).join("")}
    </tbody>
  </table>`;
}

function filterTable(){
  const q = (document.getElementById("evtSearch")?.value || "").toLowerCase();
  const list = q ? EVENTS.filter(e =>
    String(e.eventId).includes(q) ||
    String(e.subjectId).includes(q) ||
    (e.eventStatus || "").toLowerCase().includes(q) ||
    (e.severity || "").toLowerCase().includes(q) ||
    (e.eventDescription || "").toLowerCase().includes(q)
  ) : EVENTS;
  renderTable(list);
}

function applyWorkflowButtons(e){
  const bClassify = document.getElementById("bClassify");
  const bSubmit   = document.getElementById("bSubmit");
  const bResolve  = document.getElementById("bResolve");
  if (!bClassify || !bSubmit || !bResolve) return;
  if (!e) {
    bClassify.disabled = true;
    bSubmit.disabled   = true;
    bResolve.disabled  = true;
    return;
  }
  bClassify.disabled = e.eventStatus === "RESOLVED" || e.eventStatus === "FATAL";
  bSubmit.disabled   = !(e.eventStatus === "UNDER_REVIEW" && e.seriousness === "SERIOUS" && !e.safetyReportSubmitted);
  bResolve.disabled  = e.eventStatus !== "UNDER_REVIEW";
}

function showEvent(){
  const e = EVENTS.find(x => x.eventId === +eid.value);
  const box = document.getElementById("eventDetail");
  if (!box) return;
  if (!e) {
    box.innerHTML = '<div class="det-empty"><i class="bi bi-shield-plus"></i><p>Select an event to review details.</p></div>';
    applyWorkflowButtons(null);
    return;
  }

  box.innerHTML = `
    <div class="detail-hero">
      <div class="detail-avatar red"><i class="bi bi-exclamation-triangle-fill"></i></div>
      <div class="detail-title-wrap">
        <div class="detail-title">Event #${e.eventId}</div>
        <div class="detail-sub">Subject #${e.subjectId} · Onset: ${fmtDate(e.eventOnsetDate)}</div>
      </div>
      ${badge(e.eventStatus)}
    </div>
    ${eventStepper(e)}
    <div class="info-grid">
      <div class="ig"><span class="ig-l"><i class="bi bi-exclamation-octagon"></i> Severity</span><span class="ig-v">${sevPill(e.severity)}</span></div>
      <div class="ig"><span class="ig-l"><i class="bi bi-clipboard2-pulse"></i> Seriousness</span><span class="ig-v ${e.seriousness === "SERIOUS" ? "err" : "mut"}">${e.seriousness || "Pending"}</span></div>
      <div class="ig"><span class="ig-l"><i class="bi bi-send-check"></i> SAE Report</span><span class="ig-v ${e.safetyReportSubmitted ? "ok" : "mut"}">${e.safetyReportSubmitted ? '<i class="bi bi-check-circle-fill"></i> Submitted' : 'Not submitted'}</span></div>
      <div class="ig"><span class="ig-l"><i class="bi bi-code-square"></i> MedDRA</span><span class="ig-v mut">${e.meddraCode || "-"}</span></div>
      <div class="ig"><span class="ig-l"><i class="bi bi-calendar-check"></i> Linked Visit</span><span class="ig-v mut">${e.visitId ? "Visit #"+e.visitId : "—"}</span></div>
    </div>
    <p class="detail-sub" style="margin-top:10px;font-style:italic">"${e.eventDescription || ""}"</p>`;

  applyWorkflowButtons(e);
}

async function onSubjectChange(){
  const sid = document.getElementById("subjectId")?.value;
  const vSel = document.getElementById("linkedVisit");
  if (!vSel) return;   // linkedVisit may not exist in older cached page
  if (!sid) {
    vSel.innerHTML = '<option value="">— Select a subject first —</option>';
    return;
  }
  vSel.innerHTML = '<option value="">Loading visits…</option>';
  try {
    const visits = await api(`/api/visits/subject/${sid}`);
    if (!visits || !visits.length) {
      vSel.innerHTML = '<option value="">— No visits yet for this subject —</option>';
    } else {
      vSel.innerHTML = '<option value="">— None (not visit-linked) —</option>' +
        visits.map(v => `<option value="${v.visitId}">#${v.visitId} · ${v.visitName||"Visit"} · ${v.visitDate||"no date"} (${v.crfStatus})</option>`).join("");
    }
  } catch(e) {
    vSel.innerHTML = '<option value="">Could not load visits</option>';
  }
}

async function save(){
  const box = document.getElementById("rmsg");
  if (!box) return;
  box.innerHTML = "";

  const sid = document.getElementById("subjectId")?.value;
  const sev = document.getElementById("severity")?.value;
  const oed = document.getElementById("eventOnsetDate")?.value;
  const edv = document.getElementById("eventDescription")?.value;

  if(!sid || !sev || !oed || !oed.length || !edv || !edv.length){
    box.innerHTML = '<div class="msg err">Subject, severity, onset date and description are mandatory.</div>';
    return;
  }

  const visitIdVal = document.getElementById("linkedVisit")?.value || null;

  try {
    await api("/api/events", "POST", {
      subjectId: +sid,
      eventDescription: edv,
      eventOnsetDate: oed,
      severity: sev,
      visitId: visitIdVal ? +visitIdVal : null
    });
    box.innerHTML = '<div class="msg ok"><i class="bi bi-check-circle"></i> Event reported successfully.</div>';
    if (document.getElementById("eventDescription")) document.getElementById("eventDescription").value = "";
    if (document.getElementById("eventOnsetDate"))   document.getElementById("eventOnsetDate").value = "";
    if (document.getElementById("subjectId"))        document.getElementById("subjectId").value = "";
    if (document.getElementById("linkedVisit"))      document.getElementById("linkedVisit").innerHTML = '<option value="">— Select a subject first —</option>';
    if (document.getElementById("severity"))         document.getElementById("severity").value = "MILD";
    await load();
  } catch (e) {
    box.innerHTML = `<div class="msg err"><i class="bi bi-exclamation-triangle"></i> ${e.message}</div>`;
  }
}

async function classify(){
  const box = document.getElementById("wmsg");
  box.innerHTML = "";
  try {
    await api(`/api/events/${eid.value}/classify`, "PUT");
    box.innerHTML = '<div class="msg ok">Seriousness classified and event moved to Under Review.</div>';
    await load();
  } catch (e) {
    box.innerHTML = `<div class="msg err">${e.message}</div>`;
  }
}

async function submitReport(){
  const box = document.getElementById("wmsg");
  box.innerHTML = "";
  try {
    await api(`/api/events/${eid.value}/submit`, "PUT");
    box.innerHTML = '<div class="msg ok">Safety report submitted.</div>';
    await load();
  } catch (e) {
    box.innerHTML = `<div class="msg err">${e.message}</div>`;
  }
}

async function resolve(){
  const box = document.getElementById("wmsg");
  box.innerHTML = "";
  try {
    await api(`/api/events/${eid.value}/resolve`, "PUT");
    box.innerHTML = '<div class="msg ok">Event resolved.</div>';
    await load();
  } catch (e) {
    box.innerHTML = `<div class="msg err">${e.message}</div>`;
  }
}

async function loadHistory(){
  const sid = document.getElementById("histSubject")?.value;
  const box = document.getElementById("hbox");
  if (!sid) { box.innerHTML = ""; return; }

  try {
    const list = await api(`/api/events/subject/${sid}`);
    if (!list.length) {
      box.innerHTML = '<div class="evt-empty"><i class="bi bi-journal-x"></i><p>No events found for this subject.</p></div>';
      return;
    }
    box.innerHTML = `<div class="history-list">${list.map(e => `
      <div class="history-item">
        <div class="history-top">
          <div>
            <div class="history-title">Event #${e.eventId} · ${sevPill(e.severity)}</div>
            <div class="history-sub">Onset ${fmtDate(e.eventOnsetDate)} · MedDRA ${e.meddraCode || "-"}</div>
          </div>
          <div>${badge(e.eventStatus)}</div>
        </div>
        <div class="history-sub">${e.eventDescription || "-"}</div>
        ${e.eventStatus === "FATAL" ? '<div class="fatal-note"><i class="bi bi-heart-pulse"></i> Fatal event</div>' : ""}
      </div>`).join("")}</div>`;
  } catch (e) {
    box.innerHTML = `<div class="msg err">${e.message}</div>`;
  }
}

async function loadSubjects(){
  try {
    SUBJECTS = await api("/api/subjects");
    const n = fillSelect("subjectId", SUBJECTS, "subjectId",
      x => `Subject #${x.subjectId} · ${x.subjectStatus||"N/A"}`, "No subjects yet");
    const repBtn = document.getElementById("repBtn");
    if (repBtn) repBtn.disabled = n === 0;
    fillSelect("histSubject", SUBJECTS, "subjectId",
      x => `Subject #${x.subjectId} · ${x.subjectStatus||"N/A"}`, "No subjects");

    // Auto-populate linked visit for the first subject
    if (n > 0) await onSubjectChange();
  } catch(e) {
    console.error("Could not load subjects:", e.message);
  }
}

async function load(){
  try {
    EVENTS = await api("/api/events");
    updateStats();
    renderTable(EVENTS);
    fillSelect("eid", EVENTS, "eventId", x => `Event #${x.eventId} · Subject #${x.subjectId} · ${pretty(x.eventStatus)}`, "No events yet");
    showEvent();
  } catch(e) {
    console.error("Could not load events:", e.message);
  }
}

loadSubjects();
load();
