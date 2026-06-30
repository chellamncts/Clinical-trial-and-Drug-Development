package com.genc.Lab_Sample_and_IP_Tracking.samplelog.controller;

import com.genc.Lab_Sample_and_IP_Tracking.samplelog.model.InvestigationalProductInventory;
import com.genc.Lab_Sample_and_IP_Tracking.samplelog.model.SampleLog;
import com.genc.Lab_Sample_and_IP_Tracking.samplelog.repository.SampleLogRepository;
import com.genc.Lab_Sample_and_IP_Tracking.samplelog.service.SampleService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class SamplePageController {

    private static final String ACTIVE = "active";

    private final SampleLogRepository sampleLogRepository;
    private final SampleService sampleService;

    public SamplePageController(SampleLogRepository sampleLogRepository, SampleService sampleService) {
        this.sampleLogRepository = sampleLogRepository;
        this.sampleService = sampleService;
    }

    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String home() {
        return renderUserForm(null, null);
    }

    @GetMapping(value = "/UserForm", produces = MediaType.TEXT_HTML_VALUE)
    public String userForm(@RequestParam(value = "success", required = false) String success,
                           @RequestParam(value = "error", required = false) String error) {
        return renderUserForm(success, error);
    }

    @GetMapping(value = "/saveUser", produces = MediaType.TEXT_HTML_VALUE)
    public String saveUserForm() {
        // Redirect GET requests to the form
        return renderUserForm(null, null);
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
            return renderSampleList("Sample #" + saved.getSampleId() + " collected for Subject "
                    + saved.getSubjectId() + ".", null);
        } catch (Exception e) {
            return renderUserForm(null, e.getMessage());
        }
    }

    @GetMapping(value = "/sample/list", produces = MediaType.TEXT_HTML_VALUE)
    public String listSamples(@RequestParam(value = "success", required = false) String success,
                              @RequestParam(value = "error", required = false) String error) {
        return renderSampleList(success, error);
    }

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

    @GetMapping(value = "/sample/inventory", produces = MediaType.TEXT_HTML_VALUE)
    public String inventory(@RequestParam(value = "success", required = false) String success,
                            @RequestParam(value = "error", required = false) String error) {
        return renderInventory(success, error);
    }

    @GetMapping(value = "/sample/ip/dispense-form", produces = MediaType.TEXT_HTML_VALUE)
    public String dispenseForm(@RequestParam(value = "success", required = false) String success,
                               @RequestParam(value = "error", required = false) String error) {
        return renderDispenseForm(success, error);
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
            return renderInventory("Dispense #" + log.getDispenseId() + ": " + log.getQuantityDispensed()
                    + " unit(s) given to Subject " + log.getSubjectId() + " by " + log.getDispensedBy()
                    + " at " + log.getDispensingLocation() + ".", null);
        } catch (Exception e) {
            return renderDispenseForm(null, e.getMessage());
        }
    }

    @GetMapping(value = "/sample/ip/dispense-action", produces = MediaType.TEXT_HTML_VALUE)
    public String dispenseActionForm() {
        // Redirect GET requests to the dispense form
        return renderDispenseForm(null, null);
    }

    @GetMapping(value = "/sample/lab-result/{sampleId}", produces = MediaType.TEXT_HTML_VALUE)
    public String labResultForm(@PathVariable int sampleId) {
        try {
            SampleLog sample = sampleService.getSample(sampleId);
            return renderLabResult(sample, null, null);
        } catch (Exception e) {
            return renderSampleList(null, "Sample #" + sampleId + " not found.");
        }
    }

    @PostMapping(value = "/sample/lab-result-action", produces = MediaType.TEXT_HTML_VALUE)
    public String recordLabResult(@RequestParam int sampleId, @RequestParam String labResult) {
        try {
            SampleLog updated = sampleService.recordLabResult(sampleId, labResult);
            return renderSampleList("Lab result recorded for Sample #" + sampleId + ". Status: "
                    + updated.getSampleStatus().getDisplayName(), null);
        } catch (Exception e) {
            try {
                return renderLabResult(sampleService.getSample(sampleId), e.getMessage(), null);
            } catch (Exception ex) {
                return renderSampleList(null, e.getMessage());
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
                return renderSampleList(null, e.getMessage());
            }
        }
    }

    @PostMapping(value = "/sample/destroy/{sampleId}", produces = MediaType.TEXT_HTML_VALUE)
    public String destroySample(@PathVariable int sampleId) {
        try {
            SampleLog updated = sampleService.updateSampleStatus(sampleId, SampleLog.SampleStatus.DESTROYED);
            return renderLabResult(updated, null,
                    "Sample #" + sampleId + " moved to " + updated.getSampleStatus().getDisplayName());
        } catch (Exception e) {
            try {
                return renderLabResult(sampleService.getSample(sampleId), e.getMessage(), null);
            } catch (Exception ex) {
                return renderSampleList(null, e.getMessage());
            }
        }
    }

    private String renderUserForm(String success, String error) {
        Map<String, String> vars = baseVars("/UserForm", success, error);
        return render("pages/user-form.html", vars);
    }

    private String renderSampleList(String success, String error) {
        List<SampleLog> samples = sampleLogRepository.findAll();
        Map<String, String> vars = baseVars("/sample/list", success, error);
        vars.put("totalSamples", String.valueOf(samples.size()));
        vars.put("emptyMessage", samples.isEmpty() ? "No samples found. Add your first sample" : "");
        vars.put("sampleRows", buildSampleRows(samples));
        return render("pages/sample-list.html", vars);
    }

    private String renderSearch(Integer subjectId, String error, String success, List<SampleLog> results) {
        Map<String, String> vars = baseVars("/sample/search-form", success, error);
        vars.put("subjectId", subjectId == null ? "" : String.valueOf(subjectId));
        vars.put("resultCount", results == null ? "0" : String.valueOf(results.size()));
        vars.put("searchRows", buildSearchRows(results));
        vars.put("resultsHeading", subjectId == null ? "" : "Results for Subject " + subjectId);
        return render("pages/search.html", vars);
    }

    private String renderInventory(String success, String error) {
        List<InvestigationalProductInventory> inventory = sampleService.getInventoryStatus();
        Map<String, String> vars = baseVars("/sample/inventory", success, error);
        vars.put("inventoryStats", buildInventoryStats(inventory));
        vars.put("inventoryRows", buildInventoryRows(inventory));
        return render("pages/inventory.html", vars);
    }

    private String renderDispenseForm(String success, String error) {
        List<InvestigationalProductInventory> inventory = sampleService.getInventoryStatus();
        Map<String, String> vars = baseVars("/sample/ip/dispense-form", success, error);
        vars.put("dispenseOptions", buildDispenseOptions(inventory));
        vars.put("inventoryRows", buildInventoryRowsShort(inventory));
        return render("pages/dispense-form.html", vars);
    }

    private String renderLabResult(SampleLog sample, String error, String success) {
        Map<String, String> vars = baseVars("/sample/list", success, error);
        vars.put("sampleId", String.valueOf(sample.getSampleId()));
        vars.put("subjectId", String.valueOf(sample.getSubjectId()));
        vars.put("sampleType", esc(sample.getSampleType()));
        vars.put("collectionDate", sample.getCollectionDate() == null ? "--" : sample.getCollectionDate().toString());
        vars.put("statusDisplay", esc(sample.getSampleStatus().getDisplayName()));
        vars.put("existingLabResult", esc(sample.getLabResult()));
        vars.put("collectedSection", sample.getSampleStatus() == SampleLog.SampleStatus.COLLECTED
                ? render("snippets/lab-collected-section.html", vars) : "");
        vars.put("inTransitSection", sample.getSampleStatus() == SampleLog.SampleStatus.IN_TRANSIT
                ? render("snippets/lab-in-transit-section.html", vars) : "");
        vars.put("destroyAction", sample.getSampleStatus() == SampleLog.SampleStatus.ANALYZED
                ? render("snippets/lab-destroy-action.html", vars) : "");
        vars.put("finalTitle", sample.getSampleStatus() == SampleLog.SampleStatus.DESTROYED
                ? "Sample Destroyed" : "Lab Result Recorded");
        vars.put("finalSection", (sample.getSampleStatus() == SampleLog.SampleStatus.ANALYZED
                || sample.getSampleStatus() == SampleLog.SampleStatus.DESTROYED)
                ? render("snippets/lab-final-section.html", vars) : "");
        return render("pages/lab-result.html", vars);
    }

    private String buildSampleRows(List<SampleLog> samples) {
        String rowTemplate = load("snippets/sample-row.html");
        StringBuilder rows = new StringBuilder();
        for (SampleLog s : samples) {
            Map<String, String> row = new HashMap<>();
            row.put("sampleId", String.valueOf(s.getSampleId()));
            row.put("subjectId", String.valueOf(s.getSubjectId()));
            row.put("sampleType", esc(s.getSampleType()));
            row.put("collectionDate", s.getCollectionDate() == null ? "--" : s.getCollectionDate().toString());
            row.put("labResult", s.getLabResult() == null ? "--" : esc(s.getLabResult()));
            row.put("statusClass", "badge-" + s.getSampleStatus().name().toLowerCase());
            row.put("statusDisplay", esc(s.getSampleStatus().getDisplayName()));
            row.put("labResultLink", "/sample/lab-result/" + s.getSampleId());
            rows.append(apply(rowTemplate, row));
        }
        return rows.toString();
    }

    private String buildSearchRows(List<SampleLog> results) {
        if (results == null || results.isEmpty()) {
            return "";
        }
        String rowTemplate = load("snippets/search-row.html");
        StringBuilder rows = new StringBuilder();
        for (SampleLog s : results) {
            Map<String, String> row = new HashMap<>();
            row.put("sampleId", String.valueOf(s.getSampleId()));
            row.put("subjectId", String.valueOf(s.getSubjectId()));
            row.put("sampleType", esc(s.getSampleType()));
            row.put("collectionDate", s.getCollectionDate() == null ? "--" : s.getCollectionDate().toString());
            row.put("labResult", s.getLabResult() == null ? "--" : esc(s.getLabResult()));
            row.put("statusClass", "badge-" + s.getSampleStatus().name().toLowerCase());
            row.put("statusDisplay", esc(s.getSampleStatus().getDisplayName()));
            rows.append(apply(rowTemplate, row));
        }
        return rows.toString();
    }

    private String buildInventoryStats(List<InvestigationalProductInventory> inventory) {
        String tileTemplate = load("snippets/inventory-stat-tile.html");
        StringBuilder stats = new StringBuilder();
        for (InvestigationalProductInventory item : inventory) {
            Map<String, String> vars = new HashMap<>();
            vars.put("quantityAvailable", String.valueOf(item.getQuantityAvailable()));
            vars.put("productName", esc(item.getProductName()));
            stats.append(apply(tileTemplate, vars));
        }
        return stats.toString();
    }

    private String buildInventoryRows(List<InvestigationalProductInventory> inventory) {
        String rowTemplate = load("snippets/inventory-row.html");
        StringBuilder rows = new StringBuilder();
        for (InvestigationalProductInventory item : inventory) {
            Map<String, String> vars = new HashMap<>();
            vars.put("inventoryId", String.valueOf(item.getInventoryId()));
            vars.put("productName", esc(item.getProductName()));
            vars.put("batchNumber", esc(item.getBatchNumber()));
            vars.put("quantityReceived", String.valueOf(item.getQuantityReceived()));
            vars.put("quantityDispensed", String.valueOf(item.getQuantityDispensed()));
            vars.put("quantityAvailable", String.valueOf(item.getQuantityAvailable()));
            vars.put("temperatureC", String.valueOf(item.getStorageTemperatureC()));
            vars.put("coldClass", item.getColdChainStatus() == InvestigationalProductInventory.ColdChainStatus.OK
                    ? "badge-collected" : "badge-destroyed");
            vars.put("coldStatus", item.getColdChainStatus().name());
            rows.append(apply(rowTemplate, vars));
        }
        return rows.toString();
    }

    private String buildInventoryRowsShort(List<InvestigationalProductInventory> inventory) {
        String rowTemplate = load("snippets/inventory-row-short.html");
        StringBuilder rows = new StringBuilder();
        for (InvestigationalProductInventory item : inventory) {
            Map<String, String> vars = new HashMap<>();
            vars.put("inventoryId", String.valueOf(item.getInventoryId()));
            vars.put("productName", esc(item.getProductName()));
            vars.put("batchNumber", esc(item.getBatchNumber()));
            vars.put("quantityReceived", String.valueOf(item.getQuantityReceived()));
            vars.put("quantityDispensed", String.valueOf(item.getQuantityDispensed()));
            vars.put("quantityAvailable", String.valueOf(item.getQuantityAvailable()));
            vars.put("coldClass", item.getColdChainStatus() == InvestigationalProductInventory.ColdChainStatus.OK
                    ? "badge-collected" : "badge-destroyed");
            vars.put("coldStatus", item.getColdChainStatus().name());
            rows.append(apply(rowTemplate, vars));
        }
        return rows.toString();
    }

    private String buildDispenseOptions(List<InvestigationalProductInventory> inventory) {
        String optionTemplate = load("snippets/dispense-option.html");
        StringBuilder options = new StringBuilder();
        for (InvestigationalProductInventory item : inventory) {
            Map<String, String> vars = new HashMap<>();
            vars.put("inventoryId", String.valueOf(item.getInventoryId()));
            vars.put("productName", esc(item.getProductName()));
            vars.put("batchNumber", esc(item.getBatchNumber()));
            vars.put("quantityAvailable", String.valueOf(item.getQuantityAvailable()));
            options.append(apply(optionTemplate, vars));
        }
        return options.toString();
    }

    private Map<String, String> baseVars(String activeNav, String success, String error) {
        Map<String, String> vars = new HashMap<>();
        vars.put("navUserFormClass", "/UserForm".equals(activeNav) ? ACTIVE : "");
        vars.put("navListClass", "/sample/list".equals(activeNav) ? ACTIVE : "");
        vars.put("navSearchClass", "/sample/search-form".equals(activeNav) ? ACTIVE : "");
        vars.put("navInventoryClass", "/sample/inventory".equals(activeNav) ? ACTIVE : "");
        vars.put("navDispenseClass", "/sample/ip/dispense-form".equals(activeNav) ? ACTIVE : "");
        vars.put("successAlert", success == null || success.isBlank() ? "" : apply(load("snippets/alert-success.html"),
                Map.of("message", esc(success))));
        vars.put("errorAlert", error == null || error.isBlank() ? "" : apply(load("snippets/alert-error.html"),
                Map.of("message", esc(error))));
        return vars;
    }

    private String render(String templatePath, Map<String, String> vars) {
        return apply(load(templatePath), vars);
    }

    private String apply(String template, Map<String, String> vars) {
        String rendered = template;
        for (Map.Entry<String, String> entry : vars.entrySet()) {
            rendered = rendered.replace("{{" + entry.getKey() + "}}", entry.getValue() == null ? "" : entry.getValue());
        }
        return rendered.replaceAll("\\{\\{[a-zA-Z0-9_]+}}", "");
    }

    private String load(String path) {
        String fullPath = "ui/" + path;
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(fullPath)) {
            if (in == null) {
                throw new IllegalStateException("Template not found: " + fullPath);
            }
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read template: " + fullPath, e);
        }
    }

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

