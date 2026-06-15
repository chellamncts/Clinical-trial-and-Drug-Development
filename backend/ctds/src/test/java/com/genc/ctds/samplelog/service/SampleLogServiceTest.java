package com.genc.ctds.samplelog.service;

import com.genc.ctds.samplelog.model.InvestigationalProductInventory;
import com.genc.ctds.samplelog.model.SampleLog;
import com.genc.ctds.samplelog.repository.InvestigationalProductInventoryRepository;
import com.genc.ctds.samplelog.repository.SampleLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SampleLogServiceTest {

    @Mock
    private SampleLogRepository sampleLogRepository;

    @Mock
    private InvestigationalProductInventoryRepository inventoryRepository;

    private SampleLogService service;

    @BeforeEach
    void setUp() {
        service = new SampleLogService(sampleLogRepository, inventoryRepository);
    }

    @Test
    void collectSample_setsDefaultStateAndCustody() {
        SampleLog sample = new SampleLog();
        sample.setSubjectId(1001);

        when(sampleLogRepository.save(any(SampleLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SampleLog saved = service.collectSample(sample);

        assertEquals(SampleLog.SampleStatus.COLLECTED, saved.getSampleStatus());
        assertEquals(SampleLog.CustodyStatus.COLLECTED, saved.getCustodyStatus());
        assertTrue(saved.getCustodyLog().startsWith("Collected on "));
    }

    @Test
    void recordLabResult_marksSampleAnalyzed() {
        SampleLog sample = new SampleLog();
        sample.setSampleId(10);

        when(sampleLogRepository.findById(10)).thenReturn(Optional.of(sample));
        when(sampleLogRepository.save(any(SampleLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SampleLog updated = service.recordLabResult(10, "Hemoglobin: Normal");

        assertEquals("Hemoglobin: Normal", updated.getLabResult());
        assertEquals(SampleLog.SampleStatus.ANALYZED, updated.getSampleStatus());
        assertEquals(SampleLog.CustodyStatus.COMPLETED, updated.getCustodyStatus());
    }

    @Test
    void recordLabResult_throwsWhenSampleMissing() {
        when(sampleLogRepository.findById(99)).thenReturn(Optional.empty());
        when(sampleLogRepository.findBySubjectId(99)).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> service.recordLabResult(99, "Any result"));
    }

    @Test
    void recordLabResult_fallsBackToLatestSampleBySubjectId() {
        SampleLog older = new SampleLog();
        older.setSampleId(1);
        older.setSubjectId(3001);
        older.setCollectionDate(LocalDate.of(2026, 1, 10));

        SampleLog latest = new SampleLog();
        latest.setSampleId(2);
        latest.setSubjectId(3001);
        latest.setCollectionDate(LocalDate.of(2026, 1, 15));

        when(sampleLogRepository.findById(3001)).thenReturn(Optional.empty());
        when(sampleLogRepository.findBySubjectId(3001)).thenReturn(List.of(older, latest));
        when(sampleLogRepository.save(any(SampleLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SampleLog updated = service.recordLabResult(3001, "PCR: Negative");

        assertEquals(2, updated.getSampleId());
        assertEquals("PCR: Negative", updated.getLabResult());
        assertEquals(SampleLog.SampleStatus.ANALYZED, updated.getSampleStatus());
    }

    @Test
    void dispenseInvestigationalProduct_updatesInventoryAndSampleTransit() {
        SampleLog subjectSample = new SampleLog();
        subjectSample.setSampleId(5);
        subjectSample.setSubjectId(2002);
        subjectSample.setCollectionDate(LocalDate.now());

        InvestigationalProductInventory inventory = new InvestigationalProductInventory();
        inventory.setProductName("Drug-A");
        inventory.setBatchNumber("BATCH-11");
        inventory.setQuantityReceived(50);
        inventory.setQuantityAvailable(25);
        inventory.setQuantityDispensed(25);

        when(inventoryRepository.findByProductNameAndBatchNumber("Drug-A", "BATCH-11"))
                .thenReturn(Optional.of(inventory));
        when(sampleLogRepository.findBySubjectId(2002)).thenReturn(List.of(subjectSample));
        when(inventoryRepository.save(any(InvestigationalProductInventory.class))).thenAnswer(i -> i.getArgument(0));
        when(sampleLogRepository.save(any(SampleLog.class))).thenAnswer(i -> i.getArgument(0));

        InvestigationalProductInventory updated = service.dispenseInvestigationalProduct(
                2002,
                "Drug-A",
                "BATCH-11",
                5,
                5.5
        );

        assertEquals(20, updated.getQuantityAvailable());
        assertEquals(30, updated.getQuantityDispensed());
        assertEquals(InvestigationalProductInventory.ColdChainStatus.OK, updated.getColdChainStatus());

        ArgumentCaptor<SampleLog> sampleCaptor = ArgumentCaptor.forClass(SampleLog.class);
        verify(sampleLogRepository).save(sampleCaptor.capture());
        assertEquals(SampleLog.SampleStatus.IN_TRANSIT, sampleCaptor.getValue().getSampleStatus());
        assertEquals(SampleLog.CustodyStatus.IN_TRANSIT, sampleCaptor.getValue().getCustodyStatus());
        assertEquals(5.5, sampleCaptor.getValue().getColdChainTemperatureC());
    }

    @Test
    void dispenseInvestigationalProduct_autoRestocksWhenQuantityInsufficient() {
        InvestigationalProductInventory inventory = new InvestigationalProductInventory();
        inventory.setProductName("Drug-B");
        inventory.setBatchNumber("BATCH-22");
        inventory.setQuantityReceived(10);
        inventory.setQuantityAvailable(2);
        inventory.setQuantityDispensed(8);

        when(inventoryRepository.findByProductNameAndBatchNumber("Drug-B", "BATCH-22"))
                .thenReturn(Optional.of(inventory));
        when(sampleLogRepository.findBySubjectId(9999)).thenReturn(List.of());
        when(inventoryRepository.save(any(InvestigationalProductInventory.class))).thenAnswer(i -> i.getArgument(0));

        InvestigationalProductInventory updated = service.dispenseInvestigationalProduct(
                9999,
                "Drug-B",
                "BATCH-22",
                5,
                7.2
        );

        assertEquals(0, updated.getQuantityAvailable());
        assertEquals(13, updated.getQuantityDispensed());
        assertEquals(13, updated.getQuantityReceived());
    }
}

