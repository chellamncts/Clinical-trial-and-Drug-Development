
requireRole("DATA_MANAGER");

const whoEl = document.getElementById("who");
if (whoEl) whoEl.textContent = localStorage.getItem("username") || "user";

document.querySelectorAll(".nav-item").forEach(b => b.onclick = () => {
  document.querySelectorAll(".nav-item").forEach(x => x.classList.remove("active"));
  document.querySelectorAll(".sec").forEach(s => s.classList.remove("active"));
  b.classList.add("active");
  const sec = document.getElementById(b.dataset.sec);
  if (sec) sec.classList.add("active");
  const t = document.getElementById("secTitle");
  if (t) t.textContent = b.textContent.trim();
  if (b.dataset.sec === "schedule") loadSubjects();
});

function pretty(s){ return s ? s.replace(/_/g," ").replace(/\w\S*/g, w => w[0]+w.slice(1).toLowerCase()) : "—"; }

function badge(s){
  const map = { PENDING:"b-pending", COMPLETED:"b-completed", LOCKED:"b-locked" };
  return `<span class="badge ${map[s]||'b-pending'}">${pretty(s)}</span>`;
}

function fmtDate(d){ return d ? new Date(d).toLocaleDateString("en-GB",{day:"2-digit",month:"short",year:"numeric"}) : "—"; }

function crfStepper(v){
  const steps = [
    { l:"Pending",   i:"bi-hourglass" },
    { l:"Completed", i:"bi-clipboard-check" },
    { l:"Locked",    i:"bi-lock-fill" }
  ];
  const order = ["PENDING","COMPLETED","LOCKED"];
  const idx   = order.indexOf(v.crfStatus);
  return `<div class="pstepper">`+steps.map((st,i) => {
    const cls = i < idx ? "done" : (i === idx ? "active" : "");
    return `<div class="pstep ${cls}">
      <span class="pstep-dot"><i class="bi ${st.i}"></i></span>
      <span class="pstep-l">${st.l}</span>
    </div>${i < steps.length-1 ? '<span class="pstep-line"></span>' : ""}`;
  }).join("")+`</div>`;
}

let VISITS = [];
let ALL_SUBJECTS = [];

function updateStats(){
  document.getElementById("cTotal").textContent     = VISITS.length;
  document.getElementById("cPending").textContent   = VISITS.filter(v=>v.crfStatus==="PENDING").length;
  document.getElementById("cCompleted").textContent = VISITS.filter(v=>v.crfStatus==="COMPLETED").length;
  document.getElementById("cLocked").textContent    = VISITS.filter(v=>v.crfStatus==="LOCKED").length;
}

function renderTable(data){
  const box = document.getElementById("tbox");
  if(!data || !data.length){
    box.innerHTML = `<div class="visit-empty"><i class="bi bi-calendar-x"></i><p>No visits scheduled yet.</p></div>`;
    return;
  }
  box.innerHTML = `
    <table class="visit-table">
      <thead><tr>
        <th><i class="bi bi-hash"></i>ID</th>
        <th><i class="bi bi-person"></i>Subject</th>
        <th><i class="bi bi-tag"></i>Visit Name</th>
        <th><i class="bi bi-calendar-event"></i>Date</th>
        <th><i class="bi bi-arrows-expand"></i>Window</th>
        <th><i class="bi bi-question-circle"></i>Queries</th>
        <th><i class="bi bi-activity"></i>Status</th>
      </tr></thead>
      <tbody>
        ${data.map(v=>`
          <tr>
            <td class="visit-id-cell">${v.visitId}</td>
            <td>Subject ${v.subjectId}</td>
            <td class="visit-name-cell">${v.visitName||"—"}</td>
            <td class="visit-date-cell">${fmtDate(v.visitDate)}</td>
            <td>${v.visitWindow ? `<span class="window-chip"><i class="bi bi-arrows-expand"></i>${v.visitWindow}</span>` : `<span style="color:var(--muted);font-size:.8rem">—</span>`}</td>
            <td class="qc-cell ${(v.queryCount||0)>0?'has-queries':''}">${v.queryCount||0}</td>
            <td>${badge(v.crfStatus)}</td>
          </tr>`).join("")}
      </tbody>
    </table>`;
}

function filterTable(){
  const q = (document.getElementById("visitSearch")?.value || "").toLowerCase();
  const filtered = q
    ? VISITS.filter(v => String(v.subjectId).toLowerCase().includes(q))
    : VISITS;
  renderTable(filtered);
}

async function showVisit(){
  const v   = VISITS.find(x => x.visitId === +vid.value);
  const box = document.getElementById("visitDetail");
  if(!box) return;
  if(!v){
    box.innerHTML = `<div class="det-empty"><i class="bi bi-calendar3"></i>Select a visit to view CRF status.</div>`;
    document.querySelectorAll(".crf-btns .btn").forEach(b => b.disabled = true);
    document.getElementById("qc").disabled = true;
    return;
  }
  const locked = v.crfStatus === "LOCKED";

  let aeHtml = "";
  try {
    const aes = await api(`/api/events/visit/${v.visitId}`);
    if (aes && aes.length) {
      const sevClass = { SEVERE:"b-closed", MODERATE:"b-pending", MILD:"b-draft" };
      aeHtml = `<div class="admin-tip" style="margin-top:12px;border-left:3px solid #ef4444;padding-left:10px;">
        <strong><i class="bi bi-shield-exclamation" style="color:#ef4444"></i> ${aes.length} Adverse Event(s) on this visit:</strong>
        <ul style="margin:6px 0 0 16px;padding:0">
          ${aes.map(ae=>`<li style="margin:3px 0"><span class="badge ${sevClass[ae.severity]||'b-draft'}">${ae.severity}</span>
            ${ae.eventDescription ? ae.eventDescription.substring(0,60)+"…" : ""}
            <span class="badge b-pending" style="font-size:.7rem">${ae.eventStatus}</span>
          </li>`).join("")}
        </ul>
      </div>`;
    }
  } catch(e){ }

  box.innerHTML = `
    <div class="detail-hero">
      <div class="detail-avatar purple"><i class="bi bi-calendar-check"></i></div>
      <div class="detail-title-wrap">
        <div class="detail-title">${v.visitName||"Visit "+v.visitId}</div>
        <div class="detail-sub">Subject ${v.subjectId} · ${fmtDate(v.visitDate)}</div>
      </div>
      ${badge(v.crfStatus)}
    </div>
    ${crfStepper(v)}
    <div class="info-grid">
      <div class="ig"><span class="ig-l"><i class="bi bi-question-circle"></i> Queries</span><span class="ig-v ${(v.queryCount||0)>0?'err':''}">${v.queryCount||0}</span></div>
      <div class="ig"><span class="ig-l"><i class="bi bi-arrows-expand"></i> Visit Window</span><span class="ig-v mut">${v.visitWindow||"—"}</span></div>
    </div>
    ${aeHtml}
    ${locked ? `<div class="admin-tip" style="margin-top:12px;color:#3730a3;"><i class="bi bi-lock-fill" style="color:#6366f1"></i> This CRF is locked and cannot be modified.</div>` : ""}
    <div style="margin-top:12px">
      <button class="btn secondary" style="font-size:.82rem;padding:6px 14px;background:#fef2f2;color:#ef4444;border-color:#fca5a5"
        onclick="openAeModal(${v.visitId}, ${v.subjectId})">
        <i class="bi bi-shield-exclamation"></i> Report Adverse Event for this Visit
      </button>
    </div>`;

  document.querySelectorAll(".crf-btns .btn").forEach(b => b.disabled = locked);
  document.getElementById("qc").disabled = locked;
}

async function save(){
  const box = document.getElementById("smsg");
  box.innerHTML = "";

  const subjectIdVal = document.getElementById("subjectId").value;
  const visitNameVal = (visitName.value || "").trim();
  const visitDateVal = visitDate.value;

  if(!subjectIdVal){
    box.innerHTML = `<div class="msg err"><i class="bi bi-exclamation-triangle"></i> Please select an enrolled subject.</div>`;
    return;
  }

  if(isBlank(visitNameVal)){
    box.innerHTML = `<div class="msg err"><i class="bi bi-exclamation-triangle"></i> Visit Name is required and cannot be spaces only.</div>`;
    return;
  }

  if(!visitDateVal){
    box.innerHTML = `<div class="msg err"><i class="bi bi-exclamation-triangle"></i> Visit Date is required.</div>`;
    return;
  }

const selectedDate = new Date(visitDateVal);

selectedDate.setHours(0,0,0,0);

const today = new Date();
today.setHours(0,0,0,0);

const yesterday = new Date(today);
yesterday.setDate(today.getDate() - 1);

if(selectedDate < yesterday){
  box.innerHTML = `<div class="msg err"><i class="bi bi-exclamation-triangle"></i> Visit Date cannot be Past.</div>`;
  return;
}

  try {
    await api("/api/visits","POST",{
      subjectId  : +subjectIdVal,
      visitName  : visitNameVal,
      visitDate  : visitDateVal,
      visitWindow: (visitWindow.value||"").trim() === "" ? null : visitWindow.value.trim()
    });
    box.innerHTML = `<div class="msg ok"><i class="bi bi-check-circle"></i> Visit scheduled successfully!</div>`;
    visitName.value = "";
    visitDate.value = "";
    visitWindow.value = "";
    document.getElementById("subjectId").value = "";
    await load();
  } catch(e){
    box.innerHTML = `<div class="msg err"><i class="bi bi-exclamation-triangle"></i> ${e.message}</div>`;
  }
}

async function recordCrf(){
  const box = document.getElementById("crfmsg");
  box.innerHTML = "";
  try {
    await api("/api/visits/"+vid.value+"/crf?queryCount="+qc.value,"PUT");
    box.innerHTML = `<div class="msg ok"><i class="bi bi-check-circle"></i> CRF data recorded successfully.</div>`;
    await load();
  } catch(e){
    box.innerHTML = `<div class="msg err"><i class="bi bi-exclamation-triangle"></i> ${e.message}</div>`;
  }
}

async function lockCrf(){
  const box = document.getElementById("crfmsg");
  box.innerHTML = "";
  try {
    await api("/api/visits/"+vid.value+"/lock","PUT");
    box.innerHTML = `<div class="msg ok"><i class="bi bi-lock-fill"></i> CRF locked successfully.</div>`;
    await load();
  } catch(e){
    box.innerHTML = `<div class="msg err"><i class="bi bi-exclamation-triangle"></i> ${e.message}</div>`;
  }
}

async function loadHistory(){
  const sid = document.getElementById("histSubject")?.value;
  const box = document.getElementById("hbox");
  if(!sid){ box.innerHTML = ""; return; }
  try {
    const data = await api("/api/visits/subject/"+sid);
    if(!data || !data.length){
      box.innerHTML = `<div class="visit-empty"><i class="bi bi-calendar-x"></i><p>No visits found for this subject.</p></div>`;
      return;
    }
    const iconMap = { PENDING:"bi-hourglass", COMPLETED:"bi-clipboard-check", LOCKED:"bi-lock-fill" };
    box.innerHTML = `<ul class="hist-timeline">`+data.map(v=>`
      <li class="hist-item">
        <div class="hist-dot ${v.crfStatus}"><i class="bi ${iconMap[v.crfStatus]||'bi-calendar'}"></i></div>
        <div class="hist-body">
          <div class="hist-name">${v.visitName||"Visit "+v.visitId}</div>
          <div class="hist-meta">
            <span><i class="bi bi-calendar-event"></i>${fmtDate(v.visitDate)}</span>
            <span><i class="bi bi-arrows-expand"></i>${v.visitWindow||"—"}</span>
            <span><i class="bi bi-question-circle"></i>${v.queryCount||0} queries</span>
          </div>
          <div style="margin-top:8px">${badge(v.crfStatus)}</div>
        </div>
      </li>`).join("")+`</ul>`;
  } catch(e){
    box.innerHTML = `<div class="msg err">${e.message}</div>`;
  }
}

async function loadSubjects(){
  try {
    ALL_SUBJECTS = await api("/api/subjects");
    const enrolled = ALL_SUBJECTS.filter(s => s.subjectStatus === "ENROLLED");

    const subSel = document.getElementById("subjectId");
    if(enrolled.length){
      subSel.disabled = false;
      subSel.innerHTML = '<option value="">— Select an enrolled subject —</option>' +
        enrolled.map(s => `<option value="${s.subjectId}">${s.subjectId} · Protocol ${s.protocolId} · Site ${s.siteId||"—"}</option>`).join("");
    } else {
      subSel.innerHTML = '<option value="">No ENROLLED subjects yet</option>';
      subSel.disabled = true;
    }

    const histSel = document.getElementById("histSubject");
    if(histSel && histSel.tagName === "SELECT"){
      histSel.innerHTML = '<option value="">— Select a subject —</option>' +
        ALL_SUBJECTS.map(s => `<option value="${s.subjectId}">${s.subjectId} · ${s.subjectStatus}</option>`).join("");
    }
  } catch(e){
    console.error("Could not load subjects:", e.message);
    const subSel = document.getElementById("subjectId");
    if(subSel) subSel.innerHTML = `<option value="">Error loading subjects</option>`;
  }
}

async function load(){
  try {
    const d = await api("/api/visits");
    VISITS = d;
    updateStats();
    renderTable(VISITS);
    fillSelect("vid", d, "visitId",
      x => `${x.visitName||"Visit"} ${x.visitId} · Subject ${x.subjectId} · ${x.crfStatus}`, "No visits yet");
    await showVisit();
  } catch(e){
    console.error("Could not load visits:", e.message);
  }
}

loadSubjects();
load();

function openAeModal(visitId, subjectId) {
  document.getElementById("aeModal").classList.remove("hidden");
  document.getElementById("aeSubjectId").value  = "Subject " + subjectId;
  document.getElementById("aeVisitId").value    = "Visit " + visitId;
  document.getElementById("aeOnsetDate").value  = new Date().toISOString().split("T")[0];
  document.getElementById("aeSeverity").value   = "MILD";
  document.getElementById("aeDescription").value = "";
  document.getElementById("aemsg").innerHTML    = "";
  document.getElementById("aeModal").dataset.visitId   = visitId;
  document.getElementById("aeModal").dataset.subjectId = subjectId;
}

function closeAeModal() {
  document.getElementById("aeModal").classList.add("hidden");
  document.getElementById("aemsg").innerHTML = "";
}

async function submitAe() {
  const box       = document.getElementById("aemsg");
  const modal     = document.getElementById("aeModal");
  const visitId   = +modal.dataset.visitId;
  const subjectId = +modal.dataset.subjectId;
  const severity  = document.getElementById("aeSeverity").value;
  const onsetDate = document.getElementById("aeOnsetDate").value;
  const desc      = (document.getElementById("aeDescription").value || "").trim();

  if (isBlank(desc) || isBlank(onsetDate)) {
    box.innerHTML = '<div class="msg err"><i class="bi bi-exclamation-triangle"></i> Onset date and description are required.</div>';
    return;
  }

  try {
    await api("/api/events", "POST", {
      subjectId:        subjectId,
      visitId:          visitId,
      severity:         severity,
      eventOnsetDate:   onsetDate,
      eventDescription: desc
    });
    box.innerHTML = '<div class="msg ok"><i class="bi bi-check-circle"></i> Adverse Event reported successfully! It will appear in the Pharmacovigilance module.</div>';
    setTimeout(() => closeAeModal(), 1800);
  } catch(e) {
    box.innerHTML = `<div class="msg err"><i class="bi bi-exclamation-triangle"></i> ${e.message}</div>`;
  }
}

