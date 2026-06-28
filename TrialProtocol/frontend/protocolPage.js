// Base URL of the Spring Boot REST API
const API_BASE = "http://localhost:8082/api/protocols";

const form = document.getElementById("protocolForm");
const tableBody = document.getElementById("protocolTableBody");
const searchInput = document.getElementById("searchInput");
const formMessage = document.getElementById("formMessage");

// Stat elements
const statTotal = document.getElementById("statTotal");
const statActive = document.getElementById("statActive");
const statDraft = document.getElementById("statDraft");

// Modal elements
const modalOverlay = document.getElementById("modalOverlay");
const openFormBtn = document.getElementById("openFormBtn");
const closeFormBtn = document.getElementById("closeFormBtn");
const cancelFormBtn = document.getElementById("cancelFormBtn");

let allProtocols = [];

// Load protocols when the page opens
document.addEventListener("DOMContentLoaded", loadProtocols);

/* ---------- Modal control (opens form on same page) ---------- */
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

// Close when clicking outside the modal box
modalOverlay.addEventListener("click", event => {
    if (event.target === modalOverlay) closeModal();
});

/* ---------- Sidebar navigation (Users not ready yet) ---------- */
document.querySelectorAll(".nav-item").forEach(item => {
    item.addEventListener("click", event => {
        const page = item.dataset.page;
        // Protocols (current) and Sites navigate normally; Users is not ready yet
        if (page === "users") {
            event.preventDefault();
            alert("This section will be available soon.");
        }
    });
});

/* ---------- Fetch all protocols (GET) ---------- */
function loadProtocols() {
    fetch(API_BASE)
        .then(response => {
            if (!response.ok) throw new Error("Failed to load protocols");
            return response.json();
        })
        .then(data => {
            allProtocols = data;
            renderTable(allProtocols);
            updateStats(allProtocols);
        })
        .catch(error => console.error(error));
}

/* ---------- Render protocol rows ---------- */
function actionButtons(p) {
    // Status is backend-managed; show only the actions allowed by the lifecycle.
    switch (p.protocolStatus) {
        case "DRAFT":
            return `<button class="btn primary small" data-action="APPROVED" data-id="${p.protocolId}">Approve</button>`;
        case "APPROVED":
        case "ACTIVE":
            return `<button class="btn ghost small" data-action="CLOSED" data-id="${p.protocolId}">Close</button>`;
        default: // CLOSED is terminal
            return `<span class="muted-text">&mdash;</span>`;
    }
}

function renderTable(protocols) {
    tableBody.innerHTML = "";

    if (protocols.length === 0) {
        tableBody.innerHTML =
            '<tr class="empty-row"><td colspan="7">No protocols found.</td></tr>';
        return;
    }

    protocols.forEach(p => {
        const row = document.createElement("tr");
        row.innerHTML = `
            <td>${p.protocolId}</td>
            <td>${p.trialTitle}</td>
            <td>${p.therapeuticArea}</td>
            <td>${p.phase}</td>
            <td><span class="pill ${p.protocolStatus}">${p.protocolStatus}</span></td>
            <td>${p.versionNumber}</td>
            <td>${actionButtons(p)}</td>
        `;
        tableBody.appendChild(row);
    });

    // Wire up the lifecycle action buttons
    document.querySelectorAll("[data-action]").forEach(btn => {
        btn.addEventListener("click", () => changeStatus(btn.dataset.id, btn.dataset.action));
    });
}

/* ---------- Change protocol status (PUT) ---------- */
function changeStatus(id, targetStatus) {
    fetch(`${API_BASE}/${id}/status?value=${targetStatus}`, { method: "PUT" })
        .then(async response => {
            const data = await response.json().catch(() => ({}));
            if (!response.ok) throw new Error(data.error || "Failed to update status");
            return data;
        })
        .then(() => loadProtocols())
        .catch(error => alert(error.message));
}

/* ---------- Update stat cards ---------- */
function updateStats(protocols) {
    statTotal.textContent = protocols.length;
    statActive.textContent = protocols.filter(p => p.protocolStatus === "ACTIVE").length;
    statDraft.textContent = protocols.filter(p => p.protocolStatus === "DRAFT").length;
}

/* ---------- Submit new protocol (POST) ---------- */
form.addEventListener("submit", event => {
    event.preventDefault();

    const protocol = {
        trialTitle: document.getElementById("trialTitle").value,
        therapeuticArea: document.getElementById("therapeuticArea").value,
        phase: document.getElementById("phase").value,
        startDate: document.getElementById("startDate").value,
        versionNumber: parseInt(document.getElementById("versionNumber").value, 10),
        inclusionCriteria: document.getElementById("inclusionCriteria").value,
        exclusionCriteria: document.getElementById("exclusionCriteria").value
    };

    fetch(API_BASE, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(protocol)
    })
        .then(async response => {
            const data = await response.json().catch(() => ({}));
            if (!response.ok) throw new Error(data.error || "Failed to save protocol");
            return data;
        })
        .then(() => {
            showMessage("Protocol saved successfully!", "success");
            loadProtocols();
            setTimeout(closeModal, 900);
        })
        .catch(error => {
            showMessage(error.message, "error");
        });
});

/* ---------- Search/filter ---------- */
searchInput.addEventListener("keyup", () => {
    const term = searchInput.value.toLowerCase();
    const filtered = allProtocols.filter(p =>
        p.trialTitle.toLowerCase().includes(term) ||
        p.therapeuticArea.toLowerCase().includes(term) ||
        p.phase.toLowerCase().includes(term)
    );
    renderTable(filtered);
});

/* ---------- Status message ---------- */
function showMessage(text, type) {
    formMessage.textContent = text;
    formMessage.className = "message " + type;
}


