package com.genc.ctds.service;

import com.genc.ctds.model.VisitRecord;
import com.genc.ctds.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class VisitService {
    @Autowired
    private VisitRepository visitRepository;

    public VisitRecord scheduleVisit(VisitRecord visit) {
        if (visit.getCrfStatus() == null) {
            visit.setCrfStatus(com.genc.ctds.model.CrfStatus.PENDING);
        }
        return visitRepository.save(visit);
    }

    public List<VisitRecord> getVisitHistory(Long subjectId) {
        return visitRepository.findBySubjectId(subjectId);
    }

    public VisitRecord lockCrf(Long visitId) {
        VisitRecord record = visitRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("Visit not found"));
        record.setCrfStatus(com.genc.ctds.model.CrfStatus.LOCKED);
        return visitRepository.save(record);
    }
}
