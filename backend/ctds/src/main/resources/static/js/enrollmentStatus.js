window.onload = function () {
    loadSubjects();
};

function loadSubjects() {

    fetch("http://localhost:8081/enrollmentStatus")
        .then(response => response.json())
        .then(data => {

            console.log("DATA:", data);

            let table = document.getElementById("subjectTable");
            table.innerHTML = "";

            // ✅ Handle empty data
            if (!data || data.length === 0) {
                table.innerHTML =
                    "<tr>" +
                    "<td colspan='8' style='text-align:center;padding:30px;'>" +
                    "No subjects available" +
                    "</td>" +
                    "</tr>";
                return;
            }

            data.forEach(subject => {

                let status = subject.subjectStatus;

                // ✅ ✅ STATUS BADGE (THIS IS YOUR CHANGE)
                let statusBadge = "";

                if (status === "ENROLLED") {
                    statusBadge = "<span class='badge badge-completed'>ENROLLED</span>";
                } else if (status === "SCREENED") {
                    statusBadge = "<span class='badge badge-screened'>SCREENED</span>";
                } else if (status === "WITHDRAWN") {
                    statusBadge = "<span class='badge badge-pending'>WITHDRAWN</span>";
                }

else if (status === "COMPLETED") {
    statusBadge = "<span class='badge badge-completed'>COMPLETED</span>";
}


                // ✅ ACTIONS COLUMN
                let actions = "";

                // ✅ SCREENED
                if (status === "SCREENED") {

                    if (subject.consentProvided) {
                        actions += "<button " +
                            "onclick='enroll(" + subject.subjectId + ")' " +
                            "style='background:#1976D2;color:white;padding:6px 12px;border:none;border-radius:5px;'>" +
                            "Enroll</button>";
                    }

                    actions += " <button " +
                        "onclick='withdrawSubject(" + subject.subjectId + ")' " +
                        "style='background:#f44336;color:white;padding:6px 12px;border:none;border-radius:5px;margin-left:5px;'>" +
                        "Withdraw</button>";
                }

// ✅ ENROLLED
else if (status === "ENROLLED") {

    // ✅ COMPLETE BUTTON
    actions += "<button " +
        "onclick='completeSubject(" + subject.subjectId + ")' " +
        "style='background:#2e7d32;color:white;padding:6px 12px;border:none;border-radius:5px;'>" +
        "Complete</button>";

    // ✅ WITHDRAW BUTTON
    actions += " <button " +
        "onclick='withdrawSubject(" + subject.subjectId + ")' " +
        "style='background:#f44336;color:white;padding:6px 12px;border:none;border-radius:5px;margin-left:5px;'>" +
        "Withdraw</button>";
}


                // ✅ WITHDRAWN
                else if (status === "WITHDRAWN") {

                    actions += "<span style='color:red;font-weight:bold;'>✗ Withdrawn</span>";
                }

                // ✅ BUILD ROW
                let row =
                    "<tr>" +
                    "<td>" + subject.subjectId + "</td>" +
                    "<td>" + subject.protocolId + "</td>" +
                    "<td>" + subject.siteId + "</td>" +
                    "<td>" + subject.enrollmentDate + "</td>" +
                    "<td>" + subject.studyArm + "</td>" +

                    // ✅ ✅ USE BADGE HERE
                    "<td>" + statusBadge + "</td>" +

                    "<td>" + (subject.consentProvided ? "Yes" : "No") + "</td>" +
                    "<td style='display:flex;gap:8px;flex-wrap:wrap;'>" + actions + "</td>" +
                    "</tr>";

                table.innerHTML += row;
            });

        })
        .catch(error => {
            console.error("ERROR:", error);
        });
}

// ✅ ENROLL FUNCTION
function enroll(id) {
    fetch("http://localhost:8081/enrollSubject?subjectId=" + id, {
        method: "POST"
    })
    .then(() => loadSubjects());
}

// ✅ WITHDRAW FUNCTION
function withdrawSubject(id) {
    fetch("http://localhost:8081/withdrawSubject?subjectId=" + id, {
        method: "POST"
    })
    .then(() => loadSubjects());
}

// ✅ COMPLETE FUNCTION
function completeSubject(id) {
    fetch("http://localhost:8081/completeSubject?subjectId=" + id, {
        method: "POST"
    })
    .then(() => loadSubjects());
}

