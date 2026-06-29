// Base URL of the Spring Boot REST API
const AUTH_API = "http://localhost:8082/api/auth";

const form = document.getElementById("loginForm");
const usernameInput = document.getElementById("username");
const passwordInput = document.getElementById("password");
const loginBtn = document.getElementById("loginBtn");
const alertBox = document.getElementById("alertBox");

// Where to send each role after a successful login.
// Only the Protocol/Site standalone pages exist today, so unmapped roles also land there.
const ROLE_LANDING = {
    ADMIN: "protocolPage.html",
    CLINICAL_RESEARCH_COORDINATOR: "protocolPage.html",
    PRINCIPAL_INVESTIGATOR: "protocolPage.html",
    DATA_MANAGER: "protocolPage.html",
    PHARMACOVIGILANCE_OFFICER: "protocolPage.html"
};

/* ---------- Helpers ---------- */
function showAlert(message, type) {
    alertBox.textContent = message;
    alertBox.className = `alert show ${type}`;
}

function clearAlert() {
    alertBox.textContent = "";
    alertBox.className = "alert";
}

/* ---------- Submit credentials to the backend ---------- */
form.addEventListener("submit", async event => {
    event.preventDefault();
    clearAlert();

    const credentials = {
        username: usernameInput.value.trim(),
        password: passwordInput.value
    };

    if (!credentials.username || !credentials.password) {
        showAlert("Please enter both username and password.", "error");
        return;
    }

    loginBtn.disabled = true;
    loginBtn.textContent = "Signing in...";

    try {
        const response = await fetch(`${AUTH_API}/login`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(credentials)
        });

        const data = await response.json().catch(() => ({}));

        if (!response.ok) {
            throw new Error(data.error || "Invalid username or password");
        }

        // Persist the logged-in user for the dashboard pages
        sessionStorage.setItem("ctds.username", data.username);
        sessionStorage.setItem("ctds.role", data.role);

        showAlert(`Welcome, ${data.username}! Redirecting...`, "success");

        const target = ROLE_LANDING[data.role] || "protocolPage.html";
        setTimeout(() => { window.location.href = target; }, 700);
    } catch (error) {
        const message = error.message.includes("Failed to fetch")
            ? "Cannot reach the server. Make sure the backend is running on port 8082."
            : error.message;
        showAlert(message, "error");
        loginBtn.disabled = false;
        loginBtn.textContent = "LOGIN";
    }
});

