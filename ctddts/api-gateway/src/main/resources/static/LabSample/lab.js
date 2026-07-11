
requireRole("COORDINATOR");

// Show signed-in user
const whoEl = document.getElementById("who");
if (whoEl) whoEl.textContent = localStorage.getItem("username") || "user";

// Sidebar section switching
document.querySelectorAll(".nav-item").forEach(b => b.onclick = () => {
  document.querySelectorAll(".nav-item").forEach(x => x.classList.remove("active"));
  document.querySelectorAll(".sec").forEach(s => s.classList.remove("active"));
  b.classList.add("active");
  const sec = document.getElementById(b.dataset.sec);
  if (sec) sec.classList.add("active");
  const t = document.getElementById("secTitle");
  if (t) t.textContent = b.textContent.trim();
  // Reload data when switching to sections
  if (b.dataset.sec === "inventory") loadInventory();
  if (b.dataset.sec === "dispense")  { loadInventory(); loadInventoryForDispense(); loadSubjects(); }
  if (b.dataset.sec === "lab-results") loadSamples();
  if (b.dataset.sec === "collect") loadSubjects();
});

// ── Utilities ─────────────────────────────────
function fmtDate(d) {
  if (!d) return "—";
  try { return new Date(d).toLocaleDateString("en-GB", { day: "2-digit", month: "short", year: "numeric" }); }
  catch { return d; }
}

function statusBadge(s) {
  const cls = {
    COLLECTED:  "badge-collected",
    IN_TRANSIT: "badge-in_transit",
    ANALYZED:   "badge-analyzed",
    DESTROYED:  "badge-destroyed"
  };
  const labels = {
    COLLECTED:  "Collected",
    IN_TRANSIT: "In Transit",
    ANALYZED:   "Analyzed",
    DESTROYED:  "Destroyed"
  };
  return `<span class="status-pill ${cls[s] || ""}">${labels[s] || s}</span>`;
}

function coldBadge(s) {
  const cls   = { OK: "badge-ok", EXCURSION: "badge-excursion", UNKNOWN: "badge-unknown" };
  return `<span class="status-pill ${cls[s] || "badge-unknown"}">${s || "UNKNOWN"}</span>`;
}

function showMsg(elId, type, text) {
  const el = document.getElementById(elId);
  if (!el) return;
  el.innerHTML = `<div class="msg ${type}"><i class="bi bi-${type === "ok" ? "check-circle" : "exclamation-triangle"}"></i> ${text}</div>`;
}

// ── App State ──────────────────────────────────
let SAMPLES   = [];
let INVENTORY = [];
let ALL_SUBJECTS = [];

// ── DASHBOARD section ──────────────────────────
function updateDashStats() {
  document.getElementById("dTotal").textContent     = SAMPLES.length;
  document.getElementById("dCollected").textContent = SAMPLES.filter(s => s.sampleStatus === "COLLECTED").length;
  document.getElementById("dTransit").textContent   = SAMPLES.filter(s => s.sampleStatus === "IN_TRANSIT").length;
  document.getElementById("dAnalyzed").textContent  = SAMPLES.filter(s => s.sampleStatus === "ANALYZED").length;
}

function renderDashTable() {
  const box = document.getElementById("dashBox");
  if (!box) return;
  const recent = [...SAMPLES].reverse().slice(0, 10);
  if (!recent.length) {
    box.innerHTML = '<div class="lab-empty"><i class="bi bi-clipboard2-x"></i><p>No samples yet. Collect the first sample.</p></div>';
    return;
  }
  box.innerHTML = `<table class="sample-table">
    <thead><tr>
      <th>ID</th><th>Subject</th><th>Type</th><th>Collection Date</th><th>Status</th><th>Lab Result</th>
    </tr></thead>
    <tbody>${recent.map(s => `<tr>
      <td>#${s.sampleId}</td>
      <td>Subject #${s.subjectId}</td>
      <td>${s.sampleType || "—"}</td>
      <td>${fmtDate(s.collectionDate)}</td>
      <td>${statusBadge(s.sampleStatus)}</td>
      <td style="max-width:180px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap" title="${(s.labResult||"").replace(/"/g,"&quot;")}">${s.labResult || '<span style="color:var(--muted)">Pending</span>'}</td>
    </tr>`).join("")}
    </tbody>
  </table>`;
}

// ── COLLECT SAMPLE section ─────────────────────
async function collectSample() {
  const msg = document.getElementById("collectMsg");
  if (msg) msg.innerHTML = "";

  const subjectId      = document.getElementById("cSubjectId")?.value;
  const sampleType     = (document.getElementById("cSampleType")?.value || "").trim();
  const collectionDate = document.getElementById("cCollectionDate")?.value;
  const labResult      = (document.getElementById("cLabResult")?.value || "").trim();

  if (!subjectId || isBlank(sampleType)) {
    showMsg("collectMsg", "err", "Subject ID and Sample Type are required.");
    return;
  }

  try {
    const s = await api("/api/samples", "POST", {
      subjectId: +subjectId,
      sampleType,
      collectionDate: collectionDate || null,
      labResult: labResult || null
    });
    showMsg("collectMsg", "ok", `Sample #${s.sampleId} collected for Subject #${s.subjectId}.`);
    // Reset form
    ["cSubjectId","cSampleType","cCollectionDate","cLabResult"].forEach(id => {
      const el = document.getElementById(id);
      if (el) el.value = el.tagName === "SELECT" ? "" : "";
    });
    await loadSamples();
  } catch(e) {
    showMsg("collectMsg", "err", e.message);
  }
}

// ── LAB RESULTS section ────────────────────────
function renderLabResultsTable(list) {
  const box = document.getElementById("labBox");
  if (!box) return;
  if (!list || !list.length) {
    box.innerHTML = '<div class="lab-empty"><i class="bi bi-clipboard2-x"></i><p>No samples found.</p></div>';
    return;
  }
  box.innerHTML = `<table class="sample-table">
    <thead><tr>
      <th>ID</th><th>Subject</th><th>Type</th><th>Date</th><th>Status</th><th>Lab Result</th><th>Actions</th>
    </tr></thead>
    <tbody>${list.map(s => `<tr>
      <td>#${s.sampleId}</td>
      <td>Subject #${s.subjectId}</td>
      <td>${s.sampleType || "—"}</td>
      <td>${fmtDate(s.collectionDate)}</td>
      <td>${statusBadge(s.sampleStatus)}</td>
      <td style="max-width:160px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap" title="${(s.labResult||"").replace(/"/g,"&quot;")}">${s.labResult || '<em style="color:var(--muted)">Not recorded</em>'}</td>
      <td>
        ${s.sampleStatus === "COLLECTED"
          ? `<button class="act-btn" onclick="markTransit(${s.sampleId})"><i class="bi bi-truck"></i> In Transit</button>`
          : ""}
        ${s.sampleStatus === "IN_TRANSIT"
          ? `<button class="act-btn" onclick="openResultModal(${s.sampleId})"><i class="bi bi-clipboard2-check"></i> Record Result</button>`
          : ""}
        ${s.sampleStatus === "ANALYZED"
          ? `<button class="act-btn" onclick="destroySample(${s.sampleId})" style="color:#ef4444"><i class="bi bi-trash3"></i> Destroy</button>`
          : ""}
        ${s.sampleStatus === "DESTROYED" ? '<span style="color:var(--muted);font-size:.78rem">Archived</span>' : ""}
      </td>
    </tr>`).join("")}
    </tbody>
  </table>`;
}

function filterLabResults() {
  const q = (document.getElementById("labSearch")?.value || "").toLowerCase();
  const list = q ? SAMPLES.filter(s =>
    String(s.sampleId).includes(q) ||
    String(s.subjectId).includes(q) ||
    (s.sampleType || "").toLowerCase().includes(q) ||
    (s.sampleStatus || "").toLowerCase().includes(q) ||
    (s.labResult || "").toLowerCase().includes(q)
  ) : SAMPLES;
  renderLabResultsTable(list);
}

async function markTransit(id) {
  try {
    await api(`/api/samples/${id}/transit`, "PUT");
    showMsg("labMsg", "ok", `Sample #${id} marked as In Transit.`);
    await loadSamples();
  } catch(e) {
    showMsg("labMsg", "err", e.message);
  }
}

async function destroySample(id) {
  if (!confirm(`Destroy Sample #${id}? This action cannot be undone.`)) return;
  try {
    await api(`/api/samples/${id}/destroy`, "PUT");
    showMsg("labMsg", "ok", `Sample #${id} destroyed.`);
    await loadSamples();
  } catch(e) {
    showMsg("labMsg", "err", e.message);
  }
}

// ── Lab Result Modal ───────────────────────────
let modalSampleId = null;

function openResultModal(id) {
  modalSampleId = id;
  const overlay = document.getElementById("resultModal");
  if (overlay) {
    document.getElementById("modalSampleId").textContent = id;
    document.getElementById("modalLabResult").value = "";
    document.getElementById("modalMsg").innerHTML = "";
    overlay.classList.add("open");
  }
}

function closeResultModal() {
  const overlay = document.getElementById("resultModal");
  if (overlay) overlay.classList.remove("open");
  modalSampleId = null;
}

async function saveLabResult() {
  const labResult = (document.getElementById("modalLabResult")?.value || "").trim();
  if (isBlank(labResult)) {
    showMsg("modalMsg", "err", "Lab result text is required.");
    return;
  }
  try {
    await api(`/api/samples/${modalSampleId}/result`, "PUT", { labResult });
    showMsg("modalMsg", "ok", "Lab result recorded.");
    setTimeout(() => {
      closeResultModal();
      showMsg("labMsg", "ok", `Lab result recorded for Sample #${modalSampleId}.`);
    }, 800);
    await loadSamples();
  } catch(e) {
    showMsg("modalMsg", "err", e.message);
  }
}

// ── INVENTORY section ──────────────────────────
function buildInventoryTableHtml() {
  if (!INVENTORY.length) {
    return '<div class="lab-empty"><i class="bi bi-box-seam"></i><p>No inventory items found.</p></div>';
  }
  return `<table class="sample-table">
    <thead><tr>
      <th>ID</th><th>Product</th><th>Batch</th><th>Received</th><th>Dispensed</th><th>Available</th><th>Temp (°C)</th><th>Cold Chain</th>
    </tr></thead>
    <tbody>${INVENTORY.map(i => `<tr>
      <td>#${i.inventoryId}</td>
      <td><strong>${i.productName}</strong></td>
      <td>${i.batchNumber || "—"}</td>
      <td style="text-align:center">${i.quantityReceived}</td>
      <td style="text-align:center">${i.quantityDispensed}</td>
      <td style="text-align:center"><strong>${i.quantityAvailable}</strong></td>
      <td>${i.storageTemperatureC != null ? i.storageTemperatureC + "°C" : "—"}</td>
      <td>${coldBadge(i.coldChainStatus)}</td>
    </tr>`).join("")}
    </tbody>
  </table>`;
}

function renderInventoryTable() {
  const box = document.getElementById("invBox");
  if (box) box.innerHTML = buildInventoryTableHtml();

  // Also update reference table in dispense section
  const box2 = document.getElementById("invBox2");
  if (box2) box2.innerHTML = buildInventoryTableHtml();

  const totalAvail = INVENTORY.reduce((sum, i) => sum + (i.quantityAvailable || 0), 0);
  const statsEl = document.getElementById("invStats");
  if (statsEl) {
    statsEl.innerHTML = INVENTORY.map(i => `
      <div class="inv-stat">
        <div class="val">${i.quantityAvailable}</div>
        <div class="lbl">${i.productName} (${i.batchNumber})</div>
      </div>
    `).join("") + `<div class="inv-stat" style="border-color:#6366f1">
      <div class="val" style="color:#6366f1">${totalAvail}</div>
      <div class="lbl">Total Available Units</div>
    </div>`;
  }
}

// ── DISPENSE section ───────────────────────────
function loadInventoryForDispense() {
  const sel = document.getElementById("dInventoryId");
  if (!sel) return;
  if (!INVENTORY.length) {
    sel.innerHTML = '<option value="">No inventory available</option>';
    sel.disabled = true;
    return;
  }
  sel.disabled = false;
  sel.innerHTML = '<option value="">Select Drug / Inventory Item</option>' +
    INVENTORY.map(i =>
      `<option value="${i.inventoryId}">${i.productName} – ${i.batchNumber} (${i.quantityAvailable} available)</option>`
    ).join("");
}

async function dispenseIP() {
  const msg = document.getElementById("dispenseMsg");
  if (msg) msg.innerHTML = "";

  const inventoryId       = document.getElementById("dInventoryId")?.value;
  const subjectId         = document.getElementById("dSubjectId")?.value;
  const quantity          = document.getElementById("dQuantity")?.value;
  const dispensedBy        = (document.getElementById("dDispensedBy")?.value || "").trim();
  const dispensingLocation = (document.getElementById("dLocation")?.value || "").trim();

  if (!inventoryId || !subjectId || !quantity || isBlank(dispensedBy) || isBlank(dispensingLocation)) {
    showMsg("dispenseMsg", "err", "All fields are required.");
    return;
  }

  try {
    const log = await api(`/api/inventory/${inventoryId}/dispense`, "POST", {
      subjectId: +subjectId,
      quantity:  +quantity,
      dispensedBy,
      dispensingLocation
    });
    showMsg("dispenseMsg", "ok",
      `Dispensed ${log.quantityDispensed} unit(s) of ${getProductName(inventoryId)} to Subject #${log.subjectId} by ${log.dispensedBy}.`
    );
    // Reset
    ["dInventoryId","dSubjectId","dQuantity","dDispensedBy","dLocation"].forEach(id => {
      const el = document.getElementById(id);
      if (el) el.value = "";
    });
    await loadInventory();
    loadInventoryForDispense();
  } catch(e) {
    showMsg("dispenseMsg", "err", e.message);
  }
}

function getProductName(inventoryId) {
  const item = INVENTORY.find(i => String(i.inventoryId) === String(inventoryId));
  return item ? item.productName : `Item #${inventoryId}`;
}

// ── Data Loaders ───────────────────────────────
async function loadSubjects() {
  try {
    ALL_SUBJECTS = await api("/api/subjects");
    const enrolled = ALL_SUBJECTS.filter(s => s.subjectStatus === "ENROLLED");

    // Collect Sample subject dropdown
    const cSel = document.getElementById("cSubjectId");
    if (cSel) {
      if (!enrolled.length) {
        cSel.innerHTML = '<option value="">No ENROLLED subjects yet</option>';
      } else {
        cSel.innerHTML = '<option value="">Select Subject</option>' +
          enrolled.map(s => `<option value="${s.subjectId}">#${s.subjectId} — ${s.firstName || ""} ${s.lastName || ""}</option>`).join("");
      }
    }

    const dSel = document.getElementById("dSubjectId");
    if (dSel) {
      if (!enrolled.length) {
        dSel.innerHTML = '<option value="">No ENROLLED subjects yet</option>';
      } else {
        dSel.innerHTML = '<option value="">Select Subject</option>' +
          enrolled.map(s => `<option value="${s.subjectId}">#${s.subjectId} — ${s.firstName || ""} ${s.lastName || ""}</option>`).join("");
      }
    }
  } catch (e) {
    console.error("Could not load subjects:", e.message);
    const msg = `<option value="">Error loading subjects</option>`;
    const cSel = document.getElementById("cSubjectId");
    const dSel = document.getElementById("dSubjectId");
    if (cSel) cSel.innerHTML = msg;
    if (dSel) dSel.innerHTML = msg;
  }
}

async function loadSamples() {
  try {
    SAMPLES = await api("/api/samples");
    updateDashStats();
    renderDashTable();
    renderLabResultsTable(SAMPLES);
  } catch(e) {
    console.error("Could not load samples:", e.message);
  }
}

async function loadInventory() {
  try {
    INVENTORY = await api("/api/inventory");
    renderInventoryTable();
  } catch(e) {
    console.error("Could not load inventory:", e.message);
  }
}

const burger = document.getElementById("burger");
const sidebar = document.querySelector(".sidebar");
if (burger && sidebar) {
  burger.addEventListener("click", () => sidebar.classList.toggle("open"));
}
loadSamples();
loadInventory();
loadSubjects();