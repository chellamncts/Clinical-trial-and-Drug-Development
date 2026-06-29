document.getElementById("subjectForm").addEventListener("submit", function (e) {
    e.preventDefault();

    const subject = {
        protocolId: document.getElementById("protocolId").value,
        siteId: document.getElementById("siteId").value,
        enrollmentDate: document.getElementById("enrollmentDate").value,
        studyArm: document.getElementById("studyArm").value,
        consentProvided: document.getElementById("consent").checked
    };

    fetch("http://localhost:8086/screenSubject", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(subject)
    })
    .then(response => response.text())
    .then(data => {
        document.getElementById("message").innerText = data;
        document.getElementById("subjectForm").reset();
    })
    .catch(error => console.error("Error:", error));
});