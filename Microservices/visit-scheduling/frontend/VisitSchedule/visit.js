// Visit Scheduling & CRF (Data Manager) module logic
requireRole("DATA_MANAGER");

// Sidebar section switching (same pattern used by other dashboard modules)
document.querySelectorAll(".nav-item").forEach(b => b.onclick = () => {
  document.querySelectorAll(".nav-item").forEach(x => x.classList.remove("active"));
  document.querySelectorAll(".sec").forEach(s => s.classList.remove("active"));
  b.classList.add("active");
  const sec = document.getElementById(b.dataset.sec);
  if (sec) sec.classList.add("active");
  const t = document.getElementById("secTitle");
  if (t) t.textContent = b.textContent.trim();
});

/* ── helpers ── */
function pretty(s){ return s ? s.replace(/_/g," ").replace(/\w\S*/g, w => w[0]+w.slice(1).toLowerCase()) : "—"; }

function badge(s){
  const map = { PENDING:"b-pending", COMPLETED:"b-completed", LOCKED:"b-locked" };
  return `<span class="badge ${map[s]||'b-pending'}">${pretty(s)}</span>`;
}

function fmtDate(d){ return d ? new Date(d).toLocaleDateString("en-GB",{day:"2-digit",month:"short",year:"numeric"}) : "—"; }

/* ── stepper ── */
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

/* ── global data ── */
let VISITS = [];

/* STATS */
function updateStats(){
  document.getElementById("cTotal").textContent     = VISITS.length;
  document.getElementById("cPending").textContent   = VISITS.filter(v=>v.crfStatus==="PENDING").length;
  document.getElementById("cCompleted").textContent = VISITS.filter(v=>v.crfStatus==="COMPLETED").length;
  document.getElementById("cLocked").textContent    = VISITS.filter(v=>v.crfStatus==="LOCKED").length;
}

/* VISITS TABLE (Dashboard) */
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
            <td class="visit-id-cell">#${v.visitId}</td>
            <td>Subject #${v.subjectId}</td>
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
  const q = (document.getElementById("visitSearch")?.value||"").toLowerCase();
  const filtered = q ? VISITS.filter(v =>
    (v.visitName||"").toLowerCase().includes(q) ||
    String(v.subjectId).includes(q) ||
    (v.crfStatus||"").toLowerCase().includes(q) ||
    (v.visitWindow||"").toLowerCase().includes(q)
  ) : VISITS;
  renderTable(filtered);
}

/* CRF ACTIONS — visit detail hero */
function showVisit(){
  const v   = VISITS.find(x => x.visitId === +vid.value);
  const box = document.getElementById("visitDetail");
  if(!box) return;
  if(!v){
    box.innerHTML = `<div class="det-empty"><i class="bi bi-calendar3"></i><p>Select a visit to view CRF status.</p></div>`;
    return;
  }
  const locked = v.crfStatus === "LOCKED";
  box.innerHTML = `
    <div class="detail-hero">
      <div class="detail-avatar purple"><i class="bi bi-calendar-check"></i></div>
      <div class="detail-title-wrap">
        <div class="detail-title">${v.visitName||"Visit #"+v.visitId}</div>
        <div class="detail-sub">Subject #${v.subjectId} · ${fmtDate(v.visitDate)}</div>
      </div>
      ${badge(v.crfStatus)}
    </div>
    ${crfStepper(v)}
    <div class="info-grid">
      <div class="ig"><span class="ig-l"><i class="bi bi-question-circle"></i> Queries</span><span class="ig-v ${(v.queryCount||0)>0?'err':''}">${v.queryCount||0}</span></div>
      <div class="ig"><span class="ig-l"><i class="bi bi-arrows-expand"></i> Visit Window</span><span class="ig-v mut">${v.visitWindow||"—"}</span></div>
    </div>
    ${locked ? `<div class="admin-tip" style="margin-top:12px;color:#3730a3;"><i class="bi bi-lock-fill" style="color:#6366f1"></i> This CRF is locked and cannot be modified.</div>` : ""}`;

  // disable buttons if locked
  document.querySelectorAll(".crf-btns .btn").forEach(b => b.disabled = locked);
  if(locked) document.getElementById("qc").disabled = true;
  else document.getElementById("qc").disabled = false;
}

/* SAVE — Schedule visit */
async function save(){
  const box = document.getElementById("smsg");
  box.innerHTML = "";
  if(!subjectId.value){
    box.innerHTML = `<div class="msg err"><i class="bi bi-exclamation-triangle"></i> Subject is mandatory.</div>`;
    return;
  }
  try {
    await api("/visits","POST",{
      subjectId  : +subjectId.value,
      visitName  : visitName.value,
      visitDate  : visitDate.value,
      visitWindow: visitWindow.value === "" ? null : visitWindow.value
    });
    box.innerHTML = `<div class="msg ok"><i class="bi bi-check-circle"></i> Visit scheduled successfully!</div>`;
    visitName.value = ""; visitDate.value = ""; visitWindow.value = "";
    load();
  } catch(e){
    box.innerHTML = `<div class="msg err"><i class="bi bi-exclamation-triangle"></i> ${e.message}</div>`;
  }
}

/* Record CRF */
async function recordCrf(){
  const box = document.getElementById("crfmsg");
  box.innerHTML = "";
  try {
    await api("/visits/"+vid.value+"/crf?queryCount="+qc.value,"PUT");
    box.innerHTML = `<div class="msg ok"><i class="bi bi-check-circle"></i> CRF data recorded successfully.</div>`;
    load();
  } catch(e){
    box.innerHTML = `<div class="msg err"><i class="bi bi-exclamation-triangle"></i> ${e.message}</div>`;
  }
}

/* Lock CRF */
async function lockCrf(){
  const box = document.getElementById("crfmsg");
  box.innerHTML = "";
  try {
    await api("/visits/"+vid.value+"/lock","PUT");
    box.innerHTML = `<div class="msg ok"><i class="bi bi-lock-fill"></i> CRF locked successfully.</div>`;
    load();
  } catch(e){
    box.innerHTML = `<div class="msg err"><i class="bi bi-exclamation-triangle"></i> ${e.message}</div>`;
  }
}

/* VISIT HISTORY */
async function loadHistory(){
  const sid = document.getElementById("histSubject")?.value;
  const box = document.getElementById("hbox");
  if(!sid){ box.innerHTML = ""; return; }
  try {
    const data = await api("/visits/subject/"+sid);
    if(!data || !data.length){
      box.innerHTML = `<div class="visit-empty"><i class="bi bi-calendar-x"></i><p>No visits found for this subject.</p></div>`;
      return;
    }
    const iconMap = { PENDING:"bi-hourglass", COMPLETED:"bi-clipboard-check", LOCKED:"bi-lock-fill" };
    box.innerHTML = `<ul class="hist-timeline">`+data.map(v=>`
      <li class="hist-item">
        <div class="hist-dot ${v.crfStatus}"><i class="bi ${iconMap[v.crfStatus]||'bi-calendar'}"></i></div>
        <div class="hist-body">
          <div class="hist-name">${v.visitName||"Visit #"+v.visitId}</div>
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

/* LOAD ALL DATA */
async function load(){
  const d = await api("/visits");
  VISITS = d;
  updateStats();
  renderTable(VISITS);
  fillSelect("vid", d, "visitId",
    x => `${x.visitName||"Visit"} #${x.visitId} · ${x.crfStatus}`, "No visits yet");
  showVisit();
}

load();



