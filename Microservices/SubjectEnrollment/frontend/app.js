// Subject Enrollment Microservice — API helpers
// Points directly to the SubjectEnrollment service on port 8082
const API_BASE = "http://localhost:8085/api";

/**
 * Generic fetch wrapper.
 * No auth token required — this microservice is open.
 */
async function api(path, method = "GET", body = null) {
  const headers = { "Content-Type": "application/json" };
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

/**
 * Fill a <select> element from an array.
 * Returns the number of items added.
 */
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

