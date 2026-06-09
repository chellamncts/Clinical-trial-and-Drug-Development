let inputs = document.querySelectorAll("input, select");

inputs.forEach(function(input) {
    let originalPlaceholder = input.placeholder;

    input.addEventListener("focus", function() {
        if (input.type !== "checkbox") {
            input.placeholder = "Please fill the field";
        }
    });

    input.addEventListener("blur", function() {
        if (input.type !== "checkbox") {
            input.placeholder = originalPlaceholder;
        }
    });
});
document.getElementById("enrollBtn").addEventListener("click", function () {
    let subjectId = document.querySelector("input[placeholder='Enter Subject ID']").value.trim();
    let protocolId = document.querySelector("input[placeholder='Enter Protocol ID']").value.trim();
    let siteId = document.querySelector("input[placeholder='Enter Site ID']").value.trim();
    let date = document.querySelector("input[type='date']").value;
    let selects = document.querySelectorAll(".form-select");
    let studyArm = selects[0].value;
    let status = selects[1].value;
    let consent = document.getElementById("consentCheck").checked;
    if (
        subjectId === "" ||
        protocolId === "" ||
        siteId === "" ||
        date === "" ||
        studyArm === "Select Arm" ||
        status === "" ||
        consent === false
    ) {
        alert("Please fill all fields and provide consent!");
        return;
    }
    let table = document.querySelector("table");
    let newRow = table.insertRow(-1);
    newRow.insertCell(0).innerText = subjectId;
    newRow.insertCell(1).innerText = protocolId;
    newRow.insertCell(2).innerText = siteId;
    newRow.insertCell(3).innerText = date;
    newRow.insertCell(4).innerText = studyArm;
    newRow.insertCell(5).innerText = status;
    document.querySelector("form").reset();
    inputs.forEach(function(input) {
        if (input.type !== "checkbox") {
            input.placeholder = "";
        }
    });
    alert("Subject enrolled successfully!");
});