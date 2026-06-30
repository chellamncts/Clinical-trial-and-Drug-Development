package com.genc.visit_scheduling.service;

import com.genc.visit_scheduling.exception.BusinessRuleException;
import com.genc.visit_scheduling.exception.ResourceNotFoundException;
import com.genc.visit_scheduling.model.VisitRecord;
import com.genc.visit_scheduling.repository.VisitRecordRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VisitService {
    private final VisitRecordRepository repo;

    public VisitService(VisitRecordRepository repo) {
        this.repo = repo;
    }

    public List<VisitRecord> all() { return repo.findAll(); }

    private VisitRecord get(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Visit not found: " + id));
    }

    public VisitRecord scheduleVisit(VisitRecord v) {
        // This microservice owns only visit data. The subject is referenced by
        // subjectId; subject eligibility lives in the Subject Enrollment service.
        if (v.getSubjectId() == null)
            throw new BusinessRuleException("subjectId is required to schedule a visit");
        v.setCrfStatus("PENDING");
        if (v.getQueryCount() == null) v.setQueryCount(0);
        return repo.save(v);
    }

    public VisitRecord recordCrfData(Long id, Integer queryCount) {
        VisitRecord v = get(id);
        if ("LOCKED".equals(v.getCrfStatus()))
            throw new BusinessRuleException("CRF is locked and cannot be edited");
        v.setQueryCount(queryCount == null ? 0 : queryCount);
        v.setCrfStatus("COMPLETED");
        return repo.save(v);
    }

    public VisitRecord lockCrf(Long id) {
        VisitRecord v = get(id);
        if (!"COMPLETED".equals(v.getCrfStatus()))
            throw new BusinessRuleException("Only COMPLETED CRFs can be locked");
        v.setCrfStatus("LOCKED");
        return repo.save(v);
    }

    public List<VisitRecord> getVisitHistory(Long subjectId) { return repo.findBySubjectId(subjectId); }
}


