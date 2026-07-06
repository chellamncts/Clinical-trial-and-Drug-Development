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

    @GetMapping("/samples")
    public List<SampleLog> getAllSamples() {
        return sampleLogRepository.findAll();
    }

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

    @GetMapping("/samples/subject/{subjectId}")
    public List<SampleLog> getSamplesBySubject(@PathVariable int subjectId) {
        return sampleService.getSamplesBySubject(subjectId);
    }

    @GetMapping("/samples/{id}")
    public SampleLog getSample(@PathVariable int id) {
        return sampleService.getSample(id);
    }

    @PutMapping("/samples/{id}/transit")
    public SampleLog markInTransit(@PathVariable int id) {
        return sampleService.updateSampleStatus(id, SampleLog.SampleStatus.IN_TRANSIT);
    }

    @PutMapping("/samples/{id}/result")
    public SampleLog recordLabResult(@PathVariable int id,
                                      @RequestBody LabResultRequest req) {
        return sampleService.recordLabResult(id, req.getLabResult());
    }

    @PutMapping("/samples/{id}/destroy")
    public SampleLog destroySample(@PathVariable int id) {
        return sampleService.updateSampleStatus(id, SampleLog.SampleStatus.DESTROYED);
    }

    @GetMapping("/inventory")
    public List<InvestigationalProductInventory> getInventory() {
        return sampleService.getInventoryStatus();
    }

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

