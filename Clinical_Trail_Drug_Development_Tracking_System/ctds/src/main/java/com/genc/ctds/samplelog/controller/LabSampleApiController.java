package com.genc.ctds.samplelog.controller;

import com.genc.ctds.samplelog.dto.CollectSampleRequest;
import com.genc.ctds.samplelog.dto.DispenseRequest;
import com.genc.ctds.samplelog.dto.LabResultRequest;
import com.genc.ctds.samplelog.model.InvestigationalProductInventory;
import com.genc.ctds.samplelog.model.SampleLog;
import com.genc.ctds.samplelog.repository.SampleLogRepository;
import com.genc.ctds.samplelog.service.SampleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST API for Lab Sample & IP Tracking (LabSampleAndIPTracking service).
 * All endpoints are prefixed with /api/samples or /api/inventory.
 */
@RestController
@RequestMapping("/api")
public class LabSampleApiController {

    private final SampleLogRepository sampleLogRepository;
    private final SampleService sampleService;

    public LabSampleApiController(SampleLogRepository sampleLogRepository,
                                   SampleService sampleService) {
        this.sampleLogRepository = sampleLogRepository;
        this.sampleService = sampleService;
    }

    // ──────────────────────────────────────────────
    //  SAMPLES
    // ──────────────────────────────────────────────

    /** List all lab samples. */
    @GetMapping("/samples")
    public List<SampleLog> getAllSamples() {
        return sampleLogRepository.findAll();
    }

    /** Collect (register) a new lab sample. */
    @PostMapping("/samples")
    public SampleLog collectSample(@RequestBody CollectSampleRequest req) {
        SampleLog sample = new SampleLog();
        sample.setSubjectId(req.getSubjectId());
        sample.setSampleType(req.getSampleType());
        if (req.getCollectionDate() != null && !req.getCollectionDate().isBlank()) {
            sample.setCollectionDate(LocalDate.parse(req.getCollectionDate()));
        }
        if (req.getLabResult() != null && !req.getLabResult().isBlank()) {
            sample.setLabResult(req.getLabResult());
        }
        return sampleService.collectSample(sample);
    }

    /** Get all samples for a specific subject. */
    @GetMapping("/samples/subject/{subjectId}")
    public List<SampleLog> getSamplesBySubject(@PathVariable int subjectId) {
        return sampleService.getSamplesBySubject(subjectId);
    }

    /** Get a single sample by ID. */
    @GetMapping("/samples/{id}")
    public SampleLog getSample(@PathVariable int id) {
        return sampleService.getSample(id);
    }

    /** Move sample to IN_TRANSIT status. */
    @PutMapping("/samples/{id}/transit")
    public SampleLog markInTransit(@PathVariable int id) {
        return sampleService.updateSampleStatus(id, SampleLog.SampleStatus.IN_TRANSIT);
    }

    /** Record lab result (moves sample to ANALYZED status). */
    @PutMapping("/samples/{id}/result")
    public SampleLog recordLabResult(@PathVariable int id,
                                      @RequestBody LabResultRequest req) {
        return sampleService.recordLabResult(id, req.getLabResult());
    }

    /** Destroy a sample (moves to DESTROYED status). */
    @PutMapping("/samples/{id}/destroy")
    public SampleLog destroySample(@PathVariable int id) {
        return sampleService.updateSampleStatus(id, SampleLog.SampleStatus.DESTROYED);
    }

    // ──────────────────────────────────────────────
    //  INVENTORY
    // ──────────────────────────────────────────────

    /** List all IP inventory items. */
    @GetMapping("/inventory")
    public List<InvestigationalProductInventory> getInventory() {
        return sampleService.getInventoryStatus();
    }

    /** Dispense investigational product to a subject. */
    @PostMapping("/inventory/{id}/dispense")
    public ResponseEntity<InvestigationalProductInventory.DispenseLog> dispense(
            @PathVariable int id,
            @RequestBody DispenseRequest req) {
        InvestigationalProductInventory.DispenseLog log = sampleService.dispenseInvestigationalProduct(
                id, req.getSubjectId(), req.getQuantity(),
                req.getDispensedBy(), req.getDispensingLocation());
        return ResponseEntity.ok(log);
    }
}

