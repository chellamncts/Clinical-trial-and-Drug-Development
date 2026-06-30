// Shared API helpers for the Clinical Trial system
// Empty base = relative URL → all requests go to the same gateway (port 8080)
const API_BASE = "";

function saveAuth(token, role, username) {
  localStorage.setItem("token", token);
  localStorage.setItem("role", role);
  localStorage.setItem("username", username);
}
function getToken() { return localStorage.getItem("token"); }
function getRole() { return localStorage.getItem("role"); }
function getUsername() { return localStorage.getItem("username"); }
function logout() { localStorage.clear(); window.location.href = "/home/index.html"; }

function requireRole(role) {
  if (!getToken()) { window.location.href = "/home/login.html"; return; }
  // ADMIN always passes — can access every module
  if (getRole() === "ADMIN") return;
  if (role && getRole() !== role) {
    window.location.href = "/home/login.html";
  }
}

async function api(path, method = "GET", body = null) {
  const headers = { "Content-Type": "application/json" };
  const token = getToken();
  if (token) headers["Authorization"] = "Bearer " + token;
  const res = await fetch(API_BASE + path, {
    method,
    headers,
    body: body ? JSON.stringify(body) : null,
  });
  if (!res.ok) throw new Error("Request failed: " + res.status);
  return res.status === 204 ? null : res.json();
}

// Role → dashboard page mapping (matches Role enum in auth-service)
const DASHBOARDS = {
  ADMIN:                      "/TrialProtocol/admin.html",
  INVESTIGATOR:               "/subjectenrollment/dashboard.html",
  DATA_MANAGER:               "/VisitSchedule/dashboard.html",
  PHARMACOVIGILANCE_OFFICER:  "/AdverseEvent/dashboard.html",
  COORDINATOR:                "/LabSample/dashboard.html",
};

// Fill a <select> from a list. Returns count. Shows placeholder when empty.
function fillSelect(selectId, items, valueKey, labelFn, emptyText) {
  const sel = document.getElementById(selectId);
  if (!items || items.length === 0) {
    sel.innerHTML = `<option value="">${emptyText || "None available"}</option>`;
    sel.disabled = true;
    return 0;
  }
  sel.disabled = false;
  sel.innerHTML = items.map(i => `<option value="${i[valueKey]}">${labelFn(i)}</option>`).join("");
  return items.length;
}


