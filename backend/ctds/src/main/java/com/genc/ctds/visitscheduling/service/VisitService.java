package com.genc.ctds.visitscheduling.service;

import com.genc.ctds.subjectenrollment.model.TrialSubject;
import com.genc.ctds.subjectenrollment.repository.SubjectRepository;
import com.genc.ctds.visitscheduling.model.CrfRecord;
import com.genc.ctds.visitscheduling.model.CrfStatus;
import com.genc.ctds.visitscheduling.model.VisitRecord;
import com.genc.ctds.visitscheduling.repository.CrfRepository;
import com.genc.ctds.visitscheduling.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VisitService {
    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private CrfRepository crfRepository;

    public VisitRecord scheduleVisit(VisitRecord visit) {
        Integer subjectId = (visit.getTrialSubject() != null)
                ? visit.getTrialSubject().getSubjectId()
                : null;

        if (subjectId == null) {
            throw new IllegalArgumentException("Subject ID is required");
        }

        TrialSubject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No subject found with ID " + subjectId));

        visit.setTrialSubject(subject);
        visit.setCrfStatus(CrfStatus.PENDING);
        return visitRepository.save(visit);
    }

    public void recordCrfData(int visitId, Map<String, String> crfData) {
        VisitRecord visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("Visit not found"));

        CrfRecord crf = crfRepository.findByVisitId(visitId)
                .orElse(new CrfRecord());

        crf.setVisit(visit);
        crf.setBloodPressure(crfData.get("bloodPressure"));
        crf.setHeartRate(Integer.valueOf(crfData.get("heartRate")));
        crf.setTemperature(Double.valueOf(crfData.get("temperature")));
        crf.setWeight(Double.valueOf(crfData.get("weight")));
        crf.setHeight(Double.valueOf(crfData.get("height")));
        crf.setHemoglobin(Double.valueOf(crfData.get("hemoglobin")));
        crf.setWbcCount(Integer.valueOf(crfData.get("wbcCount")));
        crf.setCreatinine(Double.valueOf(crfData.get("creatinine")));

        crfRepository.save(crf);

        visit.setCrfStatus(CrfStatus.COMPLETED);
        visitRepository.save(visit);
    }


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
        List<VisitRecord> visits = visitRepository.findAll(Sort.by("id").ascending());
        visits.forEach(this::populateSubjectId);
        return visits;
    }

    public List<VisitRecord> findBySubjectId(int subjectId) {
        List<VisitRecord> visits =
                visitRepository.findByTrialSubject_SubjectIdOrderByVisitDateAsc(subjectId);
        visits.forEach(this::populateSubjectId);
        return visits;
    }

    private void populateSubjectId(VisitRecord v) {
        if (v.getTrialSubject() != null) {
            v.setSubjectId(v.getTrialSubject().getSubjectId());
        }
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
