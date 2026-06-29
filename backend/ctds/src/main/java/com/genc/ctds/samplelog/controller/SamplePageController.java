 package com.genc.ctds.samplelog.controller;

import com.genc.ctds.samplelog.model.InvestigationalProductInventory;
import com.genc.ctds.samplelog.model.SampleLog;
import com.genc.ctds.samplelog.repository.SampleLogRepository;
import com.genc.ctds.samplelog.service.SampleService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Serves the web UI as plain HTML pages directly from a @RestController
 * (no Thymeleaf). All pages reuse the shared stylesheet at /app.css.
 */
@RestController
public class SamplePageController {

    private final SampleLogRepository sampleLogRepository;
    private final SampleService sampleService;

    public SamplePageController(SampleLogRepository sampleLogRepository, SampleService sampleService) {
        this.sampleLogRepository = sampleLogRepository;
        this.sampleService = sampleService;
    }

    // ─────────────────────────────────────────────────────────────
    //  Root + Add Sample form
    // ─────────────────────────────────────────────────────────────
    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String home() {
        return userForm(null, null);
    }

    @GetMapping(value = "/UserForm", produces = MediaType.TEXT_HTML_VALUE)
    public String userForm(@RequestParam(value = "success", required = false) String success,
                           @RequestParam(value = "error", required = false) String error) {
        StringBuilder body = new StringBuilder();
        body.append(pageHeader("Collect New Sample", "Register a new lab specimen for a trial subject."));
        body.append(alerts(success, error));
        body.append("""
                <div class="card">
                    <div class="card-hdr">
                        <span class="card-hdr-title">Specimen Details</span>
                    </div>
                    <div class="card-body">
                        <form action="/saveUser" method="post">
                            <div class="form-grid">
                                <div class="field">
                                    <label for="subjectId">Subject ID</label>
                                    <input id="subjectId" type="number" name="subjectId" min="1" placeholder="Enter Subject ID" required>
                                </div>
                                <div class="field">
                                    <label for="collectionDate">Collection Date</label>
                                    <input id="collectionDate" type="date" name="collectionDate" required>
                                </div>
                                <div class="field">
                                    <label for="sampleType">Sample Type</label>
                                    <select id="sampleType" name="sampleType" required>
                                        <option value="">Select Sample Type</option>
                                        <option value="Blood">Blood</option>
                                        <option value="Urine">Urine</option>
                                        <option value="Saliva">Saliva</option>
                                        <option value="Serum">Serum</option>
                                        <option value="Plasma">Plasma</option>
                                        <option value="Tissue">Tissue</option>
                                    </select>
                                </div>
                                <div class="field">
                                    <label for="collectedBy">Collected By</label>
                                    <select id="collectedBy" name="collectedBy">
                                        <option value="">Select Lab Technician</option>
                                        <option value="Chellam">Chellam</option>
                                        <option value="Swathi">Swathi</option>
                                        <option value="Sivaranjini">Sivaranjini</option>
                                        <option value="Mohana">Mohana</option>
                                        <option value="Nithish">Nithish</option>
                                    </select>
                                </div>
                                <div class="field field-full">
                                    <label for="labResult">Lab Result</label>
                                    <textarea id="labResult" name="labResult" placeholder="Enter lab findings / result (optional at this stage)..."></textarea>
                                </div>
                            </div>
                            <div class="btn-bar">
                                <a class="btn btn-ghost" href="/sample/list">Cancel</a>
                                <button class="btn btn-primary" type="submit">Save Sample</button>
                            </div>
                        </form>
                    </div>
                </div>
                """);
        return layout("Add Sample", "/UserForm", body.toString());
    }

    @PostMapping(value = "/saveUser", produces = MediaType.TEXT_HTML_VALUE)
    public String saveUser(@RequestParam int subjectId,
                           @RequestParam(required = false) String collectionDate,
                           @RequestParam String sampleType,
                           @RequestParam(required = false) String labResult) {
        try {
            SampleLog sampleLog = new SampleLog();
            sampleLog.setSubjectId(subjectId);
            sampleLog.setSampleType(sampleType);
            if (collectionDate != null && !collectionDate.isBlank()) {
                sampleLog.setCollectionDate(LocalDate.parse(collectionDate));
            }
            if (labResult != null && !labResult.isBlank()) {
                sampleLog.setLabResult(labResult);
            }
            sampleLog.setSampleStatus(SampleLog.SampleStatus.COLLECTED);
            SampleLog saved = sampleService.collectSample(sampleLog);
            return listSamples("Sample #" + saved.getSampleId() + " collected for Subject "
                    + saved.getSubjectId() + ".", null);
        } catch (Exception e) {
            return userForm(null, e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  All Samples list
    // ─────────────────────────────────────────────────────────────
    @GetMapping(value = "/sample/list", produces = MediaType.TEXT_HTML_VALUE)
    public String listSamples(@RequestParam(value = "success", required = false) String success,
                              @RequestParam(value = "error", required = false) String error) {
        List<SampleLog> samples = sampleLogRepository.findAll();
        StringBuilder body = new StringBuilder();
        body.append(pageHeader("All Samples", "Complete list of lab specimens registered in the system."));
        body.append("""
                <div class="stats-row">
                    <div class="stat-tile">
                        <div class="st-num">%d</div>
                        <div class="st-label">Total Samples</div>
                    </div>
                </div>
                """.formatted(samples.size()));
        body.append("<div class=\"card\"><div class=\"card-hdr\">")
            .append("<span class=\"card-hdr-title\">Sample Records</span>")
            .append("<a class=\"btn btn-primary\" href=\"/UserForm\">Add Sample</a></div>")
            .append("<div class=\"card-body\">");
        body.append(alerts(success, error));
        if (samples.isEmpty()) {
            body.append("<div class=\"empty\">No samples found. <a href=\"/UserForm\">Add your first sample</a></div>");
        } else {
            body.append("""
                    <div class="table-wrap">
                        <table>
                            <thead>
                            <tr>
                                <th>ID</th><th>Subject</th><th>Type</th><th>Collection Date</th>
                                <th>Lab Result</th><th>Status</th><th>Action</th>
                            </tr>
                            </thead>
                            <tbody>
                    """);
            for (SampleLog s : samples) {
                body.append("<tr>")
                    .append("<td>").append(s.getSampleId()).append("</td>")
                    .append("<td>").append(s.getSubjectId()).append("</td>")
                    .append("<td>").append(esc(s.getSampleType())).append("</td>")
                    .append("<td>").append(s.getCollectionDate() != null ? s.getCollectionDate() : "--").append("</td>")
                    .append("<td>").append(s.getLabResult() != null ? esc(s.getLabResult()) : "--").append("</td>")
                    .append("<td>").append(statusBadge(s.getSampleStatus())).append("</td>")
                    .append("<td><a href=\"/sample/lab-result/").append(s.getSampleId()).append("\" ")
                    .append("style=\"font-size:12px; font-weight:600; color:#0f5132; text-decoration:none; padding:4px 10px; border:1px solid #cfe3d7; border-radius:6px; background:#f0fdf4; white-space:nowrap;\">Lab Result</a></td>")
                    .append("</tr>");
            }
            body.append("</tbody></table></div>");
            body.append("""
                    <div class="btn-bar">
                        <a class="btn btn-ghost" href="/sample/search-form">Search</a>
                        <a class="btn btn-ghost" href="/sample/inventory">Inventory</a>
                        <a class="btn btn-ghost" href="/sample/ip/dispense-form">Dispense IP</a>
                        <a class="btn btn-primary" href="/UserForm">Add Sample</a>
                    </div>
                    """);
        }
        body.append("</div></div>");
        return layout("All Samples", "/sample/list", body.toString());
    }

    // ─────────────────────────────────────────────────────────────
    //  Search
    // ─────────────────────────────────────────────────────────────
    @GetMapping(value = "/sample/search-form", produces = MediaType.TEXT_HTML_VALUE)
    public String searchForm() {
        return renderSearch(null, null, null, null);
    }

    @GetMapping(value = "/sample/search", produces = MediaType.TEXT_HTML_VALUE)
    public String searchBySubject(@RequestParam(value = "subjectId", required = false) Integer subjectId) {
        if (subjectId == null || subjectId <= 0) {
            return renderSearch(null, "Please enter a valid Subject ID.", null, null);
        }
        List<SampleLog> samples = sampleLogRepository.findBySubjectId(subjectId);
        if (samples.isEmpty()) {
            return renderSearch(subjectId, "No samples found for Subject ID: " + subjectId, null, null);
        }
        return renderSearch(subjectId, null,
                "Found " + samples.size() + " sample(s) for Subject " + subjectId, samples);
    }

    private String renderSearch(Integer subjectId, String error, String success, List<SampleLog> results) {
        StringBuilder body = new StringBuilder();
        body.append(pageHeader("Search Samples", "Find all specimens collected for a specific trial subject."));
        body.append("<div class=\"card\"><div class=\"card-hdr\"><span class=\"card-hdr-title\">Search by Subject ID</span></div><div class=\"card-body\">");
        body.append(alerts(success, error));
        body.append("""
                <form action="/sample/search" method="get">
                    <div class="form-grid">
                        <div class="field">
                            <label for="subjectId">Subject ID</label>
                            <input id="subjectId" type="number" name="subjectId" min="1" placeholder="Enter Subject ID" value="%s">
                        </div>
                    </div>
                    <div class="btn-bar">
                        <a class="btn btn-ghost" href="/UserForm">New Sample</a>
                        <button class="btn btn-primary" type="submit">Search</button>
                    </div>
                </form>
                """.formatted(subjectId != null ? subjectId.toString() : ""));
        body.append("</div></div>");

        if (results != null && !results.isEmpty()) {
            body.append("<div class=\"card\"><div class=\"card-hdr\">")
                .append("<span class=\"card-hdr-title\">Results for Subject ").append(subjectId).append("</span>")
                .append("<span style=\"font-size:13px; color:var(--muted);\">").append(results.size()).append(" record(s) found</span>")
                .append("</div><div class=\"card-body\"><div class=\"table-wrap\"><table><thead><tr>")
                .append("<th>ID</th><th>Subject</th><th>Type</th><th>Collection Date</th><th>Lab Result</th><th>Status</th>")
                .append("</tr></thead><tbody>");
            for (SampleLog s : results) {
                body.append("<tr>")
                    .append("<td>").append(s.getSampleId()).append("</td>")
                    .append("<td>").append(s.getSubjectId()).append("</td>")
                    .append("<td>").append(esc(s.getSampleType())).append("</td>")
                    .append("<td>").append(s.getCollectionDate() != null ? s.getCollectionDate() : "--").append("</td>")
                    .append("<td>").append(s.getLabResult() != null ? esc(s.getLabResult()) : "--").append("</td>")
                    .append("<td>").append(statusBadge(s.getSampleStatus())).append("</td>")
                    .append("</tr>");
            }
            body.append("</tbody></table></div>");
            body.append("""
                    <div class="btn-bar">
                        <a class="btn btn-ghost" href="/sample/list">View All</a>
                        <a class="btn btn-primary" href="/UserForm">Add Sample</a>
                    </div>
                    """);
            body.append("</div></div>");
        }
        return layout("Search", "/sample/search-form", body.toString());
    }

    // ─────────────────────────────────────────────────────────────
    //  Inventory
    // ─────────────────────────────────────────────────────────────
    @GetMapping(value = "/sample/inventory", produces = MediaType.TEXT_HTML_VALUE)
    public String inventory(@RequestParam(value = "success", required = false) String success,
                            @RequestParam(value = "error", required = false) String error) {
        List<InvestigationalProductInventory> inventory = sampleService.getInventoryStatus();
        StringBuilder body = new StringBuilder();
        body.append(pageHeader("Inventory Status",
                "Current investigational product stock, batch details and cold-chain conditions."));
        body.append(alerts(success, error));
        body.append("<div class=\"stats-row\">");
        for (InvestigationalProductInventory item : inventory) {
            body.append("<div class=\"stat-tile\"><div class=\"st-num\">").append(item.getQuantityAvailable())
                .append("</div><div class=\"st-label\">").append(esc(item.getProductName())).append(" Available</div></div>");
        }
        body.append("</div>");
        body.append("<div class=\"card\"><div class=\"card-hdr\">")
            .append("<span class=\"card-hdr-title\">IP Stock Levels</span>")
            .append("<a class=\"btn btn-primary\" href=\"/sample/ip/dispense-form\">Dispense IP</a></div><div class=\"card-body\">");
        if (inventory.isEmpty()) {
            body.append("<div class=\"empty\">No inventory records found.</div>");
        } else {
            body.append("<div class=\"table-wrap\"><table><thead><tr>")
                .append("<th>ID</th><th>Product</th><th>Batch Number</th><th>Received</th><th>Dispensed</th><th>Available</th><th>Temp (C)</th><th>Cold Chain</th>")
                .append("</tr></thead><tbody>");
            body.append(inventoryRows(inventory));
            body.append("</tbody></table></div>");
        }
        body.append("</div></div>");
        return layout("Inventory", "/sample/inventory", body.toString());
    }

    // ─────────────────────────────────────────────────────────────
    //  Dispense IP
    // ─────────────────────────────────────────────────────────────
    @GetMapping(value = "/sample/ip/dispense-form", produces = MediaType.TEXT_HTML_VALUE)
    public String dispenseForm(@RequestParam(value = "success", required = false) String success,
                               @RequestParam(value = "error", required = false) String error) {
        List<InvestigationalProductInventory> inventory = sampleService.getInventoryStatus();
        StringBuilder body = new StringBuilder();
        body.append(pageHeader("Dispense Investigational Product",
                "Record IP dispensing to a trial subject and update accountability log."));
        body.append(alerts(success, error));

        StringBuilder options = new StringBuilder();
        for (InvestigationalProductInventory item : inventory) {
            options.append("<option value=\"").append(item.getInventoryId()).append("\">")
                   .append(esc(item.getProductName())).append(" - Batch: ").append(esc(item.getBatchNumber()))
                   .append(" (Available: ").append(item.getQuantityAvailable()).append(")</option>");
        }

        body.append("""
                <div class="card">
                    <div class="card-hdr"><span class="card-hdr-title">Dispense Record</span></div>
                    <div class="card-body">
                        <form action="/sample/ip/dispense-action" method="post">
                            <div class="form-grid">
                                <div class="field">
                                    <label for="inventoryId">Select Drug (Inventory)</label>
                                    <select id="inventoryId" name="inventoryId" required>
                                        <option value="">Select Drug</option>
                                        %s
                                    </select>
                                </div>
                                <div class="field">
                                    <label for="subjectId">Subject ID</label>
                                    <input id="subjectId" name="subjectId" type="number" min="1" placeholder="Enter Subject ID" required>
                                </div>
                                <div class="field">
                                    <label for="quantity">Quantity</label>
                                    <input id="quantity" name="quantity" type="number" min="1" value="1" required>
                                </div>
                                <div class="field">
                                    <label for="dispensedBy">Dispensed By *</label>
                                    <select id="dispensedBy" name="dispensedBy" required>
                                        <option value="">Select Pharmacist</option>
                                        <option value="Chellam">Chellam</option>
                                        <option value="Swathi">Swathi</option>
                                        <option value="Sivaranjini">Sivaranjini</option>
                                        <option value="Mohana">Mohana</option>
                                        <option value="Nithish">Nithish</option>
                                    </select>
                                </div>
                                <div class="field field-full">
                                    <label for="dispensingLocation">Dispensing Location *</label>
                                    <input id="dispensingLocation" name="dispensingLocation" type="text" placeholder="e.g. Pharmacy Ward 3, Clinic A" required>
                                </div>
                            </div>
                            <div class="btn-bar">
                                <a class="btn btn-ghost" href="/sample/inventory">Cancel</a>
                                <button class="btn btn-primary" type="submit">Dispense</button>
                            </div>
                        </form>
                    </div>
                </div>
                """.formatted(options.toString()));

        body.append("<div class=\"card\"><div class=\"card-hdr\"><span class=\"card-hdr-title\">Current Stock Reference</span></div><div class=\"card-body\">")
            .append("<div class=\"table-wrap\"><table><thead><tr>")
            .append("<th>ID</th><th>Product</th><th>Batch</th><th>Received</th><th>Dispensed</th><th>Available</th><th>Cold Chain</th>")
            .append("</tr></thead><tbody>")
            .append(inventoryRowsShort(inventory))
            .append("</tbody></table></div></div></div>");

        return layout("Dispense IP", "/sample/ip/dispense-form", body.toString());
    }

    @PostMapping(value = "/sample/ip/dispense-action", produces = MediaType.TEXT_HTML_VALUE)
    public String dispenseIp(@RequestParam int inventoryId,
                             @RequestParam int subjectId,
                             @RequestParam int quantity,
                             @RequestParam String dispensedBy,
                             @RequestParam String dispensingLocation) {
        try {
            InvestigationalProductInventory.DispenseLog log =
                    sampleService.dispenseInvestigationalProduct(inventoryId, subjectId, quantity, dispensedBy, dispensingLocation);
            return inventory("Dispense #" + log.getDispenseId() + ": " + log.getQuantityDispensed()
                    + " unit(s) given to Subject " + log.getSubjectId() + " by " + log.getDispensedBy()
                    + " at " + log.getDispensingLocation() + ".", null);
        } catch (Exception e) {
            return dispenseForm(null, e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  Lab Result / Chain of custody
    // ─────────────────────────────────────────────────────────────
    @GetMapping(value = "/sample/lab-result/{sampleId}", produces = MediaType.TEXT_HTML_VALUE)
    public String labResultForm(@PathVariable int sampleId) {
        SampleLog sample;
        try {
            sample = sampleService.getSample(sampleId);
        } catch (Exception e) {
            return listSamples(null, "Sample #" + sampleId + " not found.");
        }
        return renderLabResult(sample, null, null);
    }

    @PostMapping(value = "/sample/lab-result-action", produces = MediaType.TEXT_HTML_VALUE)
    public String recordLabResult(@RequestParam int sampleId, @RequestParam String labResult) {
        try {
            SampleLog updated = sampleService.recordLabResult(sampleId, labResult);
            return listSamples("Lab result recorded for Sample #" + sampleId + ". Status: "
                    + updated.getSampleStatus().getDisplayName(), null);
        } catch (Exception e) {
            try {
                return renderLabResult(sampleService.getSample(sampleId), e.getMessage(), null);
            } catch (Exception ex) {
                return listSamples(null, e.getMessage());
            }
        }
    }

    @PostMapping(value = "/sample/update-status", produces = MediaType.TEXT_HTML_VALUE)
    public String updateStatus(@RequestParam int sampleId, @RequestParam String status) {
        try {
            SampleLog.SampleStatus target = SampleLog.SampleStatus.valueOf(status);
            SampleLog updated = sampleService.updateSampleStatus(sampleId, target);
            return renderLabResult(updated,
                    null, "Sample #" + sampleId + " moved to " + updated.getSampleStatus().getDisplayName());
        } catch (Exception e) {
            try {
                return renderLabResult(sampleService.getSample(sampleId), e.getMessage(), null);
            } catch (Exception ex) {
                return listSamples(null, e.getMessage());
            }
        }
    }

    private String renderLabResult(SampleLog sample, String error, String success) {
        StringBuilder body = new StringBuilder();
        body.append(pageHeader("Record Lab Result",
                "Enter the lab analysis result for this sample. Sample must be IN_TRANSIT to record a result."));
        body.append(alerts(success, error));

        // Sample info card
        body.append("<div class=\"card\"><div class=\"card-hdr\">")
            .append("<span class=\"card-hdr-title\">Sample Information</span>")
            .append(statusBadge(sample.getSampleStatus()))
            .append("</div><div class=\"card-body\"><div class=\"form-grid\">")
            .append(readonlyField("Sample ID", String.valueOf(sample.getSampleId())))
            .append(readonlyField("Subject ID", String.valueOf(sample.getSubjectId())))
            .append(readonlyField("Sample Type", sample.getSampleType()))
            .append(readonlyField("Collection Date", sample.getCollectionDate() != null ? sample.getCollectionDate().toString() : "--"))
            .append(readonlyField("Current Status", sample.getSampleStatus().getDisplayName()))
            .append("</div></div></div>");

        String statusName = sample.getSampleStatus().name();

        if ("COLLECTED".equals(statusName)) {
            body.append("""
                    <div class="card">
                        <div class="card-hdr"><span class="card-hdr-title">Chain of Custody - Move to In Transit</span></div>
                        <div class="card-body">
                            <p style="color:#5f6f67; margin-bottom:16px; font-size:13px;">
                                This sample is still <strong>COLLECTED</strong>. To record a lab result, first move it to <strong>IN_TRANSIT</strong>.
                            </p>
                            <form action="/sample/update-status" method="post">
                                <input type="hidden" name="sampleId" value="%d">
                                <input type="hidden" name="status" value="IN_TRANSIT">
                                <div class="btn-bar" style="border:none; padding:0; margin:0; justify-content:flex-start;">
                                    <button class="btn btn-primary" type="submit">Mark as In Transit</button>
                                </div>
                            </form>
                        </div>
                    </div>
                    """.formatted(sample.getSampleId()));
        } else if ("IN_TRANSIT".equals(statusName)) {
            body.append("""
                    <div class="card">
                        <div class="card-hdr"><span class="card-hdr-title">Lab Analysis Result</span></div>
                        <div class="card-body">
                            <form action="/sample/lab-result-action" method="post">
                                <input type="hidden" name="sampleId" value="%d">
                                <div class="form-grid">
                                    <div class="field field-full">
                                        <label for="labResult">Lab Result *</label>
                                        <textarea id="labResult" name="labResult" required
                                                  placeholder="e.g. Hemoglobin: 13.5 g/dL - Normal range. No abnormalities detected."></textarea>
                                    </div>
                                </div>
                                <div class="btn-bar">
                                    <a class="btn btn-ghost" href="/sample/list">Cancel</a>
                                    <button class="btn btn-primary" type="submit">Save Lab Result</button>
                                </div>
                            </form>
                        </div>
                    </div>
                    """.formatted(sample.getSampleId()));
        } else if ("ANALYZED".equals(statusName) || "DESTROYED".equals(statusName)) {
            body.append("<div class=\"card\"><div class=\"card-hdr\"><span class=\"card-hdr-title\">Lab Result Already Recorded</span></div><div class=\"card-body\"><div class=\"form-grid\">")
                .append("<div class=\"field field-full\"><label>Recorded Result</label><textarea readonly>")
                .append(sample.getLabResult() != null ? esc(sample.getLabResult()) : "")
                .append("</textarea></div>")
                .append("</div>");
            if ("ANALYZED".equals(statusName)) {
                body.append("""
                        <div style="margin-top:16px; padding-top:16px; border-top:1px solid #e2e8f0;">
                            <p style="color:#5f6f67; font-size:13px; margin-bottom:12px;">Mark this sample as <strong>DESTROYED</strong> after retention period.</p>
                            <form action="/sample/update-status" method="post">
                                <input type="hidden" name="sampleId" value="%d">
                                <input type="hidden" name="status" value="DESTROYED">
                                <button class="btn" style="background:#fef2f2; color:#dc2626; border:1.5px solid #fca5a5;" type="submit">Mark as Destroyed</button>
                            </form>
                        </div>
                        """.formatted(sample.getSampleId()));
            }
            body.append("</div></div>");
        }
        return layout("Record Lab Result", "/sample/list", body.toString());
    }

    // ─────────────────────────────────────────────────────────────
    //  Shared layout + helpers
    // ─────────────────────────────────────────────────────────────
    private String layout(String title, String activeNav, String content) {
        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>%s &mdash; Lab Sample Tracking</title>
                    <link rel="stylesheet" href="/app.css">
                </head>
                <body>
                <header class="topbar">
                    <div class="topbar-brand">Clinical Trial &amp; Drug Development Tracking System</div>
                    <nav class="topbar-nav">
                        %s
                    </nav>
                </header>
                <div class="app-shell">
                    <aside class="sidebar"></aside>
                    <main class="main">
                        %s
                    </main>
                </div>
                <footer class="footer">&copy; 2026 Clinical Trial &amp; Drug Development Tracking System</footer>
                </body>
                </html>
                """.formatted(esc(title), navLinks(activeNav), content);
    }

    private String navLinks(String active) {
        String[][] items = {
                {"/UserForm", "Add Sample"},
                {"/sample/list", "All Samples"},
                {"/sample/search-form", "Search"},
                {"/sample/inventory", "Inventory"},
                {"/sample/ip/dispense-form", "Dispense IP"},
        };
        StringBuilder sb = new StringBuilder();
        for (String[] item : items) {
            String cls = item[0].equals(active) ? " class=\"active\"" : "";
            sb.append("<a href=\"").append(item[0]).append("\"").append(cls).append(">")
              .append(item[1]).append("</a>");
        }
        return sb.toString();
    }

    private String pageHeader(String title, String subtitle) {
        return "<div class=\"pg-hdr\"><h1>" + esc(title) + "</h1><p>" + esc(subtitle) + "</p></div>";
    }

    private String alerts(String success, String error) {
        StringBuilder sb = new StringBuilder();
        if (success != null && !success.isBlank()) {
            sb.append("<div class=\"alert msg-success\">").append(esc(success)).append("</div>");
        }
        if (error != null && !error.isBlank()) {
            sb.append("<div class=\"alert msg-error\">").append(esc(error)).append("</div>");
        }
        return sb.toString();
    }

    private String readonlyField(String label, String value) {
        return "<div class=\"field\"><label>" + esc(label) + "</label>"
                + "<input type=\"text\" value=\"" + esc(value) + "\" readonly></div>";
    }

    private String statusBadge(SampleLog.SampleStatus status) {
        if (status == null) {
            return "--";
        }
        return "<span class=\"badge badge-" + status.name().toLowerCase() + "\">"
                + esc(status.getDisplayName()) + "</span>";
    }

    private String inventoryRows(List<InvestigationalProductInventory> inventory) {
        StringBuilder sb = new StringBuilder();
        for (InvestigationalProductInventory item : inventory) {
            sb.append("<tr>")
              .append("<td>").append(item.getInventoryId()).append("</td>")
              .append("<td><strong>").append(esc(item.getProductName())).append("</strong></td>")
              .append("<td>").append(esc(item.getBatchNumber())).append("</td>")
              .append("<td style=\"text-align:center\">").append(item.getQuantityReceived()).append("</td>")
              .append("<td style=\"text-align:center\">").append(item.getQuantityDispensed()).append("</td>")
              .append("<td style=\"text-align:center\"><strong style=\"color:#0f5132\">").append(item.getQuantityAvailable()).append("</strong></td>")
              .append("<td>").append(item.getStorageTemperatureC()).append("C</td>")
              .append("<td>").append(coldChainBadge(item.getColdChainStatus())).append("</td>")
              .append("</tr>");
        }
        return sb.toString();
    }

    private String inventoryRowsShort(List<InvestigationalProductInventory> inventory) {
        StringBuilder sb = new StringBuilder();
        for (InvestigationalProductInventory item : inventory) {
            sb.append("<tr>")
              .append("<td>").append(item.getInventoryId()).append("</td>")
              .append("<td><strong>").append(esc(item.getProductName())).append("</strong></td>")
              .append("<td>").append(esc(item.getBatchNumber())).append("</td>")
              .append("<td style=\"text-align:center\">").append(item.getQuantityReceived()).append("</td>")
              .append("<td style=\"text-align:center\">").append(item.getQuantityDispensed()).append("</td>")
              .append("<td style=\"text-align:center\"><strong style=\"color:#0f5132\">").append(item.getQuantityAvailable()).append("</strong></td>")
              .append("<td>").append(coldChainBadge(item.getColdChainStatus())).append("</td>")
              .append("</tr>");
        }
        return sb.toString();
    }

    private String coldChainBadge(InvestigationalProductInventory.ColdChainStatus status) {
        String cls = status == InvestigationalProductInventory.ColdChainStatus.OK ? "badge-collected" : "badge-destroyed";
        return "<span class=\"badge " + cls + "\">" + status + "</span>";
    }

    /** Minimal HTML escaping to avoid breaking markup / injection. */
    private String esc(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}








