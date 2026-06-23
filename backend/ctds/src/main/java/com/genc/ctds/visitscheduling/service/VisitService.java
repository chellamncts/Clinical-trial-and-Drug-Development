package com.genc.ctds.visitscheduling.service;

import com.genc.ctds.subjectenrollment.model.TrialSubject;
import com.genc.ctds.subjectenrollment.repository.SubjectRepository;
import com.genc.ctds.visitscheduling.model.CrfStatus;
import com.genc.ctds.visitscheduling.model.VisitRecord;
import com.genc.ctds.visitscheduling.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VisitService {
    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    public VisitRecord scheduleVisit(VisitRecord visit) {
        if (visit.getCrfStatus() == null) {
            visit.setCrfStatus(CrfStatus.PENDING);
        }

        if (visit.getTrialSubject() != null && visit.getTrialSubject().getSubjectId() != null) {
            Integer subjectId = visit.getTrialSubject().getSubjectId();
            TrialSubject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new RuntimeException("Subject not found: " + subjectId));
            visit.setTrialSubject(subject);  // replace transient object with managed entity
        }

        return visitRepository.save(visit);
    }

    // Called when coordinator records clinical data
    public VisitRecord recordCrfData(int visitId, Integer queryCount) {
        VisitRecord visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("Visit not found: " + visitId));
        visit.setCrfStatus(CrfStatus.COMPLETED);
        if (queryCount != null) {
            visit.setQueryCount(queryCount);
        }
        return visitRepository.save(visit);
    }

    // Called when data manager locks the CRF
    public VisitRecord lockCrf(int visitId) {
        VisitRecord visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("Visit not found: " + visitId));
        visit.setCrfStatus(CrfStatus.LOCKED);
        return visitRepository.save(visit);
    }

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

    public List<VisitRecord> getVisitHistory() {
        return visitRepository.findAll(Sort.by("id").ascending());
    }

    public List<VisitRecord> findBySubjectId(int subjectId) {
        return visitRepository.findByTrialSubject_SubjectIdOrderByVisitDateAsc(subjectId);
    }

    public List<String> getVisitsTimelineLabels() {
        List<Object[]> results = visitRepository.countVisitsByDate();
        return results.stream()
                .map(r -> r[0].toString())
                .collect(Collectors.toList());
    }

    public List<Integer> getVisitsTimelineData() {
        List<Object[]> results = visitRepository.countVisitsByDate();
        return results.stream()
                .map(r -> ((Long) r[1]).intValue())
                .collect(Collectors.toList());
    }
}
