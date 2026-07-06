package com.genc.ctds.samplelog.service;

import com.genc.ctds.samplelog.model.InvestigationalProductInventory;
import com.genc.ctds.samplelog.model.SampleLog;
import com.genc.ctds.samplelog.repository.SampleLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SampleServiceTest {

    @Mock
    private SampleLogRepository sampleLogRepository;

    @InjectMocks
    private SampleService sampleService;

    @Test
    void collectSample_setsCollectedStatusAndSaves() {
        SampleLog request = new SampleLog();
        request.setSubjectId(101);
        request.setSampleType("Blood");
        request.setCollectionDate(LocalDate.of(2026, 6, 23));

        SampleLog persisted = new SampleLog();
        persisted.setSampleId(77);
        persisted.setSubjectId(101);
        persisted.setSampleType("Blood");
        persisted.setCollectionDate(request.getCollectionDate());

        when(sampleLogRepository.save(any(SampleLog.class))).thenReturn(persisted);

        SampleLog result = sampleService.collectSample(request);

        assertEquals(77, result.getSampleId());
        verify(sampleLogRepository).save(any(SampleLog.class));
    }

    @Test
    void collectSample_throwsWhenSampleTypeMissing() {
        SampleLog request = new SampleLog();
        request.setSubjectId(101);

        assertThrows(IllegalArgumentException.class,
                () -> sampleService.collectSample(request));
    }

    @Test
    void recordLabResult_marksSampleAnalyzed() {
        SampleLog sample = new SampleLog();
        sample.setSampleId(11);
        sample.setSubjectId(301);

        when(sampleLogRepository.findById(11)).thenReturn(Optional.of(sample));
        when(sampleLogRepository.save(any(SampleLog.class))).thenReturn(sample);

        SampleLog updated = sampleService.recordLabResult(11, "Normal");

        assertEquals("Normal", updated.getLabResult());
        assertEquals(SampleLog.SampleStatus.ANALYZED, updated.getSampleStatus());
    }

    @Test
    void dispenseInvestigationalProduct_updatesAccountability() {
        InvestigationalProductInventory.DispenseLog result = sampleService.dispenseInvestigationalProduct(
                1, 222, 3, "Pharmacist", "Site Pharmacy");

        assertEquals(1, result.getInventoryId());
        assertEquals(3, result.getQuantityDispensed());

        List<InvestigationalProductInventory> inventoryStatus = sampleService.getInventoryStatus();
        assertEquals(1, inventoryStatus.size());
        assertEquals(97, inventoryStatus.get(0).getQuantityAvailable());
    }

    @Test
    void dispenseInvestigationalProduct_throwsOnInsufficientInventory() {
        sampleService.dispenseInvestigationalProduct(1, 111, 95, "Tester", "Pharmacy");

        assertThrows(IllegalArgumentException.class,
                () -> sampleService.dispenseInvestigationalProduct(1, 111, 6, "Tester", "Pharmacy"));
    }

    @Test
    void getSamplesBySubject_returnsRepositoryRows() {
        SampleLog sample = new SampleLog();
        sample.setSubjectId(44);
        when(sampleLogRepository.findBySubjectId(44)).thenReturn(List.of(sample));

        List<SampleLog> result = sampleService.getSamplesBySubject(44);

        assertEquals(1, result.size());
        assertEquals(44, result.get(0).getSubjectId());
    }
}


