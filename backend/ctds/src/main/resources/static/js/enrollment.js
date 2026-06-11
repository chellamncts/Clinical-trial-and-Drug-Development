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
document.querySelector("form").addEventListener("submit", function(e) {

    let subjectId = document.querySelector("input[name='subjectId']").value.trim();
    let protocolId = document.querySelector("input[name='protocolId']").value.trim();
    let siteId = document.querySelector("input[name='siteId']").value.trim();
    let date = document.querySelector("input[type='date']").value;

    let selects = document.querySelectorAll("select");
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
        alert(" Please fill all fields and provide consent!");
        e.preventDefault();
        return;
    }
});