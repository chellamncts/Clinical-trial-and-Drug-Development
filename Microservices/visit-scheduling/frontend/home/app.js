// Shared API helpers for the Visit Scheduling module (standalone build).
const API_BASE = "http://localhost:8086/api";

function logout() { localStorage.clear(); window.location.href = "../home/index.html"; }

// This standalone module has no auth backend, so role checks are a no-op.
function requireRole(_role) { /* standalone: no authentication required */ }

async function api(path, method = "GET", body = null) {
  const headers = { "Content-Type": "application/json" };
  const res = await fetch(API_BASE + path, {
    method,
    headers,
    body: body ? JSON.stringify(body) : null,
  });
  if (!res.ok) {
    let msg = "Request failed: " + res.status;
    try { const j = await res.json(); if (j && j.message) msg = j.message; } catch (e) { /* ignore */ }
    throw new Error(msg);
  }
  return res.status === 204 ? null : res.json();
}

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

