<script>
document.getElementById("enrollBtn").addEventListener("click", function () {

    let subject Id = document.querySelector("input[placeholder='Enter Subject ID']").value;
    let protocolId = document.querySelector("input[placeholder='Enter Protocol ID']").value;
    let siteId = document.querySelector("input[placeholder='Enter Site ID']").value;
    let date = document.querySelector("input[type='date']").value;

    let selects = document.querySelectorAll(".form-select");
    let studyArm = selects[0].value;
    let status = selects[1].value;

    let table = document.querySelector("table");

    let newRow = table.insertRow(-1);

    newRow.insertCell(0).innerHTML = subjectId;
    newRow.insertCell(1).innerHTML = protocolId;
    newRow.insertCell(2).innerHTML = siteId;
    newRow.insertCell(3).innerHTML = date;
    newRow.insertCell(4).innerHTML = studyArm;
    newRow.insertCell(5).innerHTML = status;
};
</script>