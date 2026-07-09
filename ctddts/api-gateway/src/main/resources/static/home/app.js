
const API_BASE = "";

function trimVal(id) { const el=document.getElementById(id); return el?(el.value||"").trim():""; }
function isBlank(v) { return !v || v.trim().length === 0; }
function isCtddtsEmail(v) { return !!v && /^[^\s@]+@ctddts\.com$/i.test(v.trim()); }

function saveAuth(token, role, username) {
  localStorage.setItem("token", token);
  localStorage.setItem("role", role);
  localStorage.setItem("username", username);
}
function getToken() { return localStorage.getItem("token"); }
function getRole() { return localStorage.getItem("role"); }
function getUsername() { return localStorage.getItem("username"); }
function logout() { localStorage.clear(); window.location.href = "/home/login.html"; }

function requireRole(role) {
  if (!getToken()) { window.location.href = "/home/login.html"; return; }
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
  if (!res.ok) {
    let msg = "";
    try {
      const errBody = await res.json();
      msg = errBody.message || errBody.error || "";
    } catch (_) {}
    if (!msg) {
      const fallback = {
        400: "Invalid input — please check your data.",
        401: "Unauthorised — please log in again.",
        403: "You don't have permission to perform this action.",
        404: "The requested resource was not found.",
        409: "Conflict — this record already exists.",
        500: "Server error — please try again later.",
        503: "Service unavailable — check that all services are running."
      };
      msg = fallback[res.status] || ("Unexpected error (status " + res.status + ").");
    }
    throw new Error(msg);
  }
  return res.status === 204 ? null : res.json();
}

const DASHBOARDS = {
  ADMIN:                      "/TrialProtocol/admin.html",
  INVESTIGATOR:               "/subjectenrollment/dashboard.html",
  DATA_MANAGER:               "/VisitSchedule/dashboard.html",
  PHARMACOVIGILANCE_OFFICER:  "/AdverseEvent/dashboard.html",
  COORDINATOR:                "/LabSample/dashboard.html",
};

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


