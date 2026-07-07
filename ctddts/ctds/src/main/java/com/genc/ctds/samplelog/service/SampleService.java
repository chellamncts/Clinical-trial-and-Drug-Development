package com.genc.ctds.samplelog.service;

import com.genc.ctds.samplelog.model.InvestigationalProductInventory;
import com.genc.ctds.samplelog.model.SampleLog;
import com.genc.ctds.samplelog.repository.InvestigationalProductInventoryRepository;
import com.genc.ctds.samplelog.repository.SampleLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SampleService {

    private final SampleLogRepository sampleLogRepository;
    private final InvestigationalProductInventoryRepository inventoryRepository;
    private int dispenseSequence = 1;
    private static final Map<SampleLog.SampleStatus, Set<SampleLog.SampleStatus>> ALLOWED_TRANSITIONS = Map.of(
            SampleLog.SampleStatus.COLLECTED, Set.of(SampleLog.SampleStatus.IN_TRANSIT),
            SampleLog.SampleStatus.IN_TRANSIT, Set.of(SampleLog.SampleStatus.ANALYZED),
            SampleLog.SampleStatus.ANALYZED, Set.of(SampleLog.SampleStatus.DESTROYED),
            SampleLog.SampleStatus.DESTROYED, Set.of()
    );
    public List<SampleLog> getAllSamples() {
        return sampleLogRepository.findAll();
    }

    public SampleService(
            SampleLogRepository sampleLogRepository,
            InvestigationalProductInventoryRepository inventoryRepository) {
        this.sampleLogRepository = sampleLogRepository;
        this.inventoryRepository = inventoryRepository;
    }

    @Transactional
    public SampleLog collectSample(SampleLog sampleLog) {
        if (sampleLog.getSubjectId() <= 0) {
            throw new IllegalArgumentException("subjectId must be greater than zero");
        }
        if (sampleLog.getSampleType() == null || sampleLog.getSampleType().isBlank()) {
            throw new IllegalArgumentException("sampleType is required");
        }
        if (sampleLog.getCollectionDate() == null) {
            sampleLog.setCollectionDate(LocalDate.now());
        }
        sampleLog.setSampleStatus(SampleLog.SampleStatus.COLLECTED);
        return sampleLogRepository.save(sampleLog);
    }

    @Transactional
    public SampleLog recordLabResult(int sampleId, String labResult) {
        if (sampleId <= 0) {
            throw new IllegalArgumentException("sampleId must be greater than zero");
        }
        if (labResult == null || labResult.isBlank()) {
            throw new IllegalArgumentException("labResult is required");
        }
        SampleLog sample = sampleLogRepository.findById(sampleId)
                .orElseThrow(() -> new IllegalArgumentException("Sample not found: " + sampleId));
        if (sample.getSampleStatus() != SampleLog.SampleStatus.IN_TRANSIT) {
            throw new IllegalArgumentException(
                    "Lab result can only be recorded when sample is IN_TRANSIT. Current status: " + sample.getSampleStatus());
        }
        sample.setLabResult(labResult);
        sample.setSampleStatus(SampleLog.SampleStatus.ANALYZED);
        return sampleLogRepository.save(sample);
    }


    @Transactional
    public SampleLog updateSampleStatus(int sampleId, SampleLog.SampleStatus targetStatus) {
        if (sampleId <= 0) {
            throw new IllegalArgumentException("sampleId must be greater than zero");
        }
        if (targetStatus == null) {
            throw new IllegalArgumentException("target sampleStatus is required");
        }
        SampleLog sample = sampleLogRepository.findById(sampleId)
                .orElseThrow(() -> new IllegalArgumentException("Sample not found: " + sampleId));

        SampleLog.SampleStatus current = sample.getSampleStatus();
        Set<SampleLog.SampleStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, Set.of());
        if (!allowed.contains(targetStatus)) {
            throw new IllegalArgumentException(
                    "Invalid transition " + current + " -> " + targetStatus
                            + ". Allowed next: " + allowed);
        }
        sample.setSampleStatus(targetStatus);


        return sampleLogRepository.save(sample);
    }

    public SampleLog getSample(int sampleId) {
        if (sampleId <= 0) {
            throw new IllegalArgumentException("sampleId must be greater than zero");
        }
        return sampleLogRepository.findById(sampleId)
                .orElseThrow(() -> new IllegalArgumentException("Sample not found: " + sampleId));
    }

    @Transactional
    public InvestigationalProductInventory.DispenseLog dispenseInvestigationalProduct(
            int inventoryId,
            int subjectId,
            int quantity,
            String dispensedBy,
            String dispensingLocation) {
        if (inventoryId <= 0) {
            throw new IllegalArgumentException("inventoryId must be greater than zero");
        }
        if (subjectId <= 0) {
            throw new IllegalArgumentException("subjectId must be greater than zero");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Dispense quantity must be greater than zero");
        }
        if (dispensedBy == null || dispensedBy.isBlank()) {
            throw new IllegalArgumentException("dispensedBy (name of person dispensing) is required");
        }
        if (dispensingLocation == null || dispensingLocation.isBlank()) {
            throw new IllegalArgumentException("dispensingLocation is required");
        }

        InvestigationalProductInventory inventory = inventoryRepository.findById(inventoryId)
                .orElseThrow(() -> new IllegalArgumentException("Inventory not found: " + inventoryId));
        if (quantity > inventory.getQuantityAvailable()) {
            throw new IllegalArgumentException("Insufficient inventory for dispense request");
        }

        inventory.setQuantityDispensed(inventory.getQuantityDispensed() + quantity);
        inventory.setQuantityAvailable(inventory.getQuantityReceived() - inventory.getQuantityDispensed());
        inventoryRepository.save(inventory);

        InvestigationalProductInventory.DispenseLog log = new InvestigationalProductInventory.DispenseLog();
        log.setDispenseId(dispenseSequence++);
        log.setInventoryId(inventoryId);
        log.setSubjectId(subjectId);
        log.setQuantityDispensed(quantity);
        log.setDispensedBy(dispensedBy);
        log.setDispensingLocation(dispensingLocation);
        log.setDispensedAt(LocalDateTime.now());
        return log;
    }

    public List<InvestigationalProductInventory> getInventoryStatus() {
        return inventoryRepository.findAllByOrderByInventoryIdAsc();
    }

    public List<SampleLog> getSamplesBySubject(int subjectId) {
        if (subjectId <= 0) {
            throw new IllegalArgumentException("subjectId must be greater than zero");
        }
        return sampleLogRepository.findBySubjectId(subjectId);
    }
}