package com.example.clinical_trial.service;

import com.example.clinical_trial.model.CrfStatus;
import com.example.clinical_trial.model.VisitRecord;
import com.example.clinical_trial.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisitService {
    @Autowired
    private VisitRepository visitRepository;

    // Schedule a new visit
    public VisitRecord scheduleVisit(VisitRecord visit) {
        // If frontend sends CRF status, use it; otherwise default to PENDING
        if (visit.getCrfStatus() == null) {
            visit.setCrfStatus(CrfStatus.PENDING);
        }
        return visitRepository.save(visit);
    }


    // Get visit history for a subject
    public List<VisitRecord> getVisitHistory(Long subjectId) {
        return visitRepository.findBySubjectId(subjectId);
    }

    // Lock CRF for a visit
    public VisitRecord lockCrf(Long visitId) {
        VisitRecord visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("Visit not found"));
        visit.setCrfStatus(CrfStatus.LOCKED);
        return visitRepository.save(visit);
    }
}
