// Subject Enrollment — API helpers (routes through API Gateway)
const API_BASE = "";

// ── Auth helpers (shared with gateway) ──────────────────────────────
function saveAuth(token, role, username) {
  localStorage.setItem("token", token);
  localStorage.setItem("role", role);
  localStorage.setItem("username", username);
}
function getToken()    { return localStorage.getItem("token"); }
function getRole()     { return localStorage.getItem("role"); }
function getUsername() { return localStorage.getItem("username"); }
function logout()      { localStorage.clear(); window.location.href = "/home/login.html"; }

function requireRole(...allowed) {
  if (!getToken()) { window.location.href = "/home/login.html"; return; }
  const role = getRole();
  if (role === "ADMIN") return; // ADMIN can access everything
  if (allowed.length && !allowed.includes(role)) {
    window.location.href = "/home/login.html";
  }
}

// ── Authenticated fetch wrapper ──────────────────────────────────────
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
    let msg = "Request failed: " + res.status;
    try { const e = await res.json(); msg = e.message || msg; } catch (_) {}
    throw new Error(msg);
  }
  return res.status === 204 ? null : res.json();
}

// ── Select helper ────────────────────────────────────────────────────
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
