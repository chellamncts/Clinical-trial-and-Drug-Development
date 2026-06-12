package com.genc.ctds.visitscheduling.service;

import com.genc.ctds.visitscheduling.model.CrfStatus;
import com.genc.ctds.visitscheduling.model.VisitRecord;
import com.genc.ctds.visitscheduling.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class VisitService {
    @Autowired
    private VisitRepository visitRepository;

    // Schedule a new visit
    public VisitRecord scheduleVisit(VisitRecord visit) {
        // Default CRF status if not provided
        if (visit.getCrfStatus() == null) {
            visit.setCrfStatus(CrfStatus.PENDING);
        }
        return visitRepository.save(visit);
    }

    // Get recent visits (limit to last 10 for dashboard)
    public List<VisitRecord> getRecentVisits() {
        List<VisitRecord> visits = visitRepository.findTop10ByOrderByVisitDateDesc();
        visits.sort(Comparator.comparing(VisitRecord::getId));
        return visits;
    }

    // Get visit history for a subject
    public List<VisitRecord> getVisitHistory(Long subjectId) {
        return visitRepository.findBySubjectIdOrderByVisitDateAsc(subjectId);
    }

    // Summary counts for dashboard
    public int countScheduled() {
        return (int) visitRepository.count();
    }

    public int countPendingCrfs() {
        return visitRepository.countByCrfStatus(CrfStatus.PENDING);
    }

    public int countCompletedCrfs() {
        return visitRepository.countByCrfStatus(CrfStatus.COMPLETED);
    }

    public int countLockedCrfs() {
        return visitRepository.countByCrfStatus(CrfStatus.LOCKED);
    }

    public VisitRecord lockCrf(Long visitId) {
        VisitRecord visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("Visit not found: " + visitId));
        visit.setCrfStatus(CrfStatus.LOCKED);
        return visitRepository.save(visit);
    }

    public VisitRecord completeCrf(Long visitId) {
        VisitRecord visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("Visit not found: " + visitId));
        visit.setCrfStatus(CrfStatus.COMPLETED);
        return visitRepository.save(visit);
    }

    public List<VisitRecord> getAllVisits() {
        return visitRepository.findAll(Sort.by("id").ascending());
    }

    public List<VisitRecord> findBySubjectId(Long subjectId) {
        return visitRepository.findBySubjectIdOrderByVisitDateAsc(subjectId);
    }




}
