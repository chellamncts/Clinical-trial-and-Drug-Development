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
    private final AtomicInteger dispenseSequence = new AtomicInteger(1);

    /** Allowed forward transitions for the sample lifecycle. */
    private static final Map<SampleLog.SampleStatus, Set<SampleLog.SampleStatus>> ALLOWED_TRANSITIONS = Map.of(
            SampleLog.SampleStatus.COLLECTED, Set.of(SampleLog.SampleStatus.IN_TRANSIT),
            SampleLog.SampleStatus.IN_TRANSIT, Set.of(SampleLog.SampleStatus.ANALYZED),
            SampleLog.SampleStatus.ANALYZED, Set.of(SampleLog.SampleStatus.DESTROYED),
            SampleLog.SampleStatus.DESTROYED, Set.of()
    );

    public SampleService(
            SampleLogRepository sampleLogRepository,
            InvestigationalProductInventoryRepository inventoryRepository) {
        this.sampleLogRepository = sampleLogRepository;
        this.inventoryRepository = inventoryRepository;
        seedInventoryIfEmpty();
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

    /**
     * Moves a sample to the requested next state, enforcing the lifecycle:
     * COLLECTED -> IN_TRANSIT -> ANALYZED -> DESTROYED.
     * Automatically records the date for each transition.
     */
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
        log.setDispenseId(dispenseSequence.getAndIncrement());
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

    private void seedInventoryIfEmpty() {
        if (inventoryRepository.count() > 0) {
            return;
        }
        
        // Drug 1: IP-101
        InvestigationalProductInventory drug1 = new InvestigationalProductInventory();
        drug1.setInventoryId(1);
        drug1.setProductName("IP-101");
        drug1.setBatchNumber("BATCH-CT-001");
        drug1.setQuantityReceived(100);
        drug1.setQuantityDispensed(0);
        drug1.setQuantityAvailable(100);
        drug1.setStorageTemperatureC(5.0);
        drug1.setColdChainStatus(InvestigationalProductInventory.ColdChainStatus.OK);
        inventoryRepository.save(drug1);
        
        // Drug 2: IP-102
        InvestigationalProductInventory drug2 = new InvestigationalProductInventory();
        drug2.setInventoryId(2);
        drug2.setProductName("IP-102");
        drug2.setBatchNumber("BATCH-CT-002");
        drug2.setQuantityReceived(80);
        drug2.setQuantityDispensed(0);
        drug2.setQuantityAvailable(80);
        drug2.setStorageTemperatureC(2.0);
        drug2.setColdChainStatus(InvestigationalProductInventory.ColdChainStatus.OK);
        inventoryRepository.save(drug2);
        
        // Drug 3: IP-103
        InvestigationalProductInventory drug3 = new InvestigationalProductInventory();
        drug3.setInventoryId(3);
        drug3.setProductName("IP-103");
        drug3.setBatchNumber("BATCH-CT-003");
        drug3.setQuantityReceived(120);
        drug3.setQuantityDispensed(0);
        drug3.setQuantityAvailable(120);
        drug3.setStorageTemperatureC(20.0);
        drug3.setColdChainStatus(InvestigationalProductInventory.ColdChainStatus.OK);
        inventoryRepository.save(drug3);
    }
}
