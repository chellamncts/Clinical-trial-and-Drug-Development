// Home / Login page logic
async function doLogin() {
  const msg = document.getElementById("msg");
  const btn = document.getElementById("loginBtn");
  const username = document.getElementById("username")?.value ?? "";
  const password = document.getElementById("password")?.value ?? "";
  if (username.length === 0 || password.length === 0) {
    msg.innerHTML = '<div class="msg err">Username and password are required</div>';
    return;
  }
  if (btn) btn.classList.add("is-loading");
  try {
    const res = await fetch("/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username, password })
    });
    if (!res.ok) {
      if (btn) btn.classList.remove("is-loading");
      const status = res.status;
      if (status === 401) {
        msg.innerHTML = '<div class="msg err">Invalid username or password</div>';
      } else {
        msg.innerHTML = `<div class="msg err">Login failed (server error ${status}) — check auth-service console</div>`;
      }
      return;
    }
    const data = await res.json();
    saveAuth(data.token, data.role, data.username);
    window.location.href = DASHBOARDS[data.role] || "/home/index.html";
  } catch (e) {
    if (btn) btn.classList.remove("is-loading");
    msg.innerHTML = '<div class="msg err">Cannot reach server — is the gateway running on port 8083?</div>';
  }
}

// Password visibility toggle (login page)
function togglePw(btn) {
  const input = document.getElementById("password");
  if (!input) return;
  const show = input.type === "password";
  input.type = show ? "text" : "password";
  btn.innerHTML = show ? '<i class="bi bi-eye-slash"></i>' : '<i class="bi bi-eye"></i>';
  btn.setAttribute("aria-label", show ? "Hide password" : "Show password");
}

// Submit on Enter key from either field
document.addEventListener("keydown", (e) => {
  if (e.key === "Enter" && document.getElementById("password")) doLogin();
});




