// REST API base URLs (Spring Boot on port 8082)
const SITES_API = "http://localhost:8082/api/sites";
// Only APPROVED/ACTIVE protocols are eligible for site work (backend-enforced)
const ELIGIBLE_PROTOCOLS_API = "http://localhost:8082/api/protocols/site-eligible";

const form = document.getElementById("siteForm");
const tableBody = document.getElementById("siteTableBody");
const searchInput = document.getElementById("searchInput");
const formMessage = document.getElementById("formMessage");
const protocolSelect = document.getElementById("protocolId");

// Stat elements
const statTotal = document.getElementById("statTotal");
const statActive = document.getElementById("statActive");
const statReady = document.getElementById("statReady");

// Modal elements
const modalOverlay = document.getElementById("modalOverlay");
const openFormBtn = document.getElementById("openFormBtn");
const closeFormBtn = document.getElementById("closeFormBtn");
const cancelFormBtn = document.getElementById("cancelFormBtn");

let allSites = [];

// Load data when page opens
document.addEventListener("DOMContentLoaded", () => {
    loadSites();
    loadProtocols();
});

/* ---------- Modal control ---------- */
function openModal() {
    modalOverlay.classList.add("open");
}

function closeModal() {
    modalOverlay.classList.remove("open");
    form.reset();
    formMessage.textContent = "";
    formMessage.className = "message";
}

openFormBtn.addEventListener("click", openModal);
closeFormBtn.addEventListener("click", closeModal);
cancelFormBtn.addEventListener("click", closeModal);

modalOverlay.addEventListener("click", event => {
    if (event.target === modalOverlay) closeModal();
});

/* ---------- Sidebar navigation ---------- */
document.querySelectorAll(".nav-item").forEach(item => {
    item.addEventListener("click", event => {
        const page = item.dataset.page;
        // Protocol Management links to its own page; Users is not ready yet
        if (page === "users") {
            event.preventDefault();
            alert("This section will be available soon.");
        }
    });
});

/* ---------- Load protocols for the dropdown (only APPROVED/ACTIVE) ---------- */
function loadProtocols() {
    fetch(ELIGIBLE_PROTOCOLS_API)
        .then(response => {
            if (!response.ok) throw new Error("Failed to load protocols");
            return response.json();
        })
        .then(protocols => {
            protocolSelect.innerHTML = "";
            if (protocols.length === 0) {
                protocolSelect.innerHTML =
                    '<option value="">No approved protocols available</option>';
                return;
            }
            protocolSelect.innerHTML = '<option value="">Select protocol</option>';
            protocols.forEach(p => {
                const option = document.createElement("option");
                option.value = p.protocolId;
                option.textContent = `${p.trialTitle} (v${p.versionNumber})`;
                protocolSelect.appendChild(option);
            });
        })
        .catch(error => console.error(error));
}

/* ---------- Load all sites (GET) ---------- */
function loadSites() {
    fetch(SITES_API)
        .then(response => {
            if (!response.ok) throw new Error("Failed to load sites");
            return response.json();
        })
        .then(data => {
            allSites = data;
            renderTable(allSites);
            updateStats(allSites);
        })
        .catch(error => console.error(error));
}

/* ---------- Render site rows ---------- */
function isReady(site) {
    return site.ethicsApproved && site.staffTrained && site.pharmacyReady;
}

function renderTable(sites) {
    tableBody.innerHTML = "";

    if (sites.length === 0) {
        tableBody.innerHTML =
            '<tr class="empty-row"><td colspan="6">No sites registered yet.</td></tr>';
        return;
    }

    sites.forEach(site => {
        const protocolTitle = site.trialProtocol ? site.trialProtocol.trialTitle : "-";
        const ready = isReady(site);
        const isActive = site.siteStatus === "ACTIVE";

        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${site.siteCode}</td>
            <td>${site.siteName}<span class="sub">${site.location}</span></td>
            <td>${protocolTitle}</td>
            <td><span class="pill ${ready ? "ready" : "pending"}">${ready ? "Ready" : "Pending"}</span></td>
            <td><span class="pill ${site.siteStatus}">${site.siteStatus}</span></td>
            <td>
                <button class="btn primary small activate-btn" data-id="${site.siteId}" ${isActive ? "disabled" : ""}>
                    ${isActive ? "Active" : "Activate"}
                </button>
            </td>
        `;
        tableBody.appendChild(row);
    });

    // Wire up activate buttons
    document.querySelectorAll(".activate-btn").forEach(btn => {
        btn.addEventListener("click", () => activateSite(btn.dataset.id));
    });
}

/* ---------- Update stat cards ---------- */
function updateStats(sites) {
    statTotal.textContent = sites.length;
    statActive.textContent = sites.filter(s => s.siteStatus === "ACTIVE").length;
    statReady.textContent = sites.filter(s => isReady(s)).length;
}

/* ---------- Register new site (POST) ---------- */
form.addEventListener("submit", event => {
    event.preventDefault();

    const site = {
        protocolId: parseInt(protocolSelect.value, 10),
        siteCode: document.getElementById("siteCode").value,
        siteName: document.getElementById("siteName").value,
        location: document.getElementById("location").value,
        principalInvestigatorName: document.getElementById("principalInvestigatorName").value,
        ethicsApproved: document.getElementById("ethicsApproved").checked,
        staffTrained: document.getElementById("staffTrained").checked,
        pharmacyReady: document.getElementById("pharmacyReady").checked
    };

    fetch(SITES_API, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(site)
    })
        .then(async response => {
            const data = await response.json().catch(() => ({}));
            if (!response.ok) throw new Error(data.error || "Failed to register site");
            return data;
        })
        .then(() => {
            showMessage("Site registered successfully!", "success");
            loadSites();
            setTimeout(closeModal, 900);
        })
        .catch(error => showMessage(error.message, "error"));
});

/* ---------- Activate a site (POST) ---------- */
function activateSite(siteId) {
    fetch(`${SITES_API}/${siteId}/activate`, { method: "POST" })
        .then(async response => {
            const data = await response.json().catch(() => ({}));
            if (!response.ok) throw new Error(data.error || "Failed to activate site");
            return data;
        })
        .then(() => loadSites())
        .catch(error => alert(error.message));
}

/* ---------- Search/filter ---------- */
searchInput.addEventListener("keyup", () => {
    const term = searchInput.value.toLowerCase();
    const filtered = allSites.filter(s =>
        s.siteCode.toLowerCase().includes(term) ||
        s.siteName.toLowerCase().includes(term) ||
        s.location.toLowerCase().includes(term)
    );
    renderTable(filtered);
});

/* ---------- Status message ---------- */
function showMessage(text, type) {
    formMessage.textContent = text;
    formMessage.className = "message " + type;
}

