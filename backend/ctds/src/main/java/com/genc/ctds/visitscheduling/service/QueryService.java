package com.genc.ctds.visitscheduling.service;

import com.genc.ctds.visitscheduling.model.QueryRecord;
import com.genc.ctds.visitscheduling.model.QueryStatus;
import com.genc.ctds.visitscheduling.model.VisitRecord;
import com.genc.ctds.visitscheduling.repository.QueryRepository;
import com.genc.ctds.visitscheduling.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class QueryService {

    @Autowired
    private QueryRepository queryRepository;

    @Autowired
    private VisitRepository visitRepository;

    /** Raise a new query (OPEN) against a visit. */
    public QueryRecord raiseQuery(int visitId, String description) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Query description is required");
        }

        VisitRecord visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new RuntimeException("Visit not found: " + visitId));

        QueryRecord query = new QueryRecord();
        query.setVisit(visit);
        query.setDescription(description.trim());
        query.setStatus(QueryStatus.OPEN);
        query.setRaisedAt(LocalDateTime.now());

        QueryRecord saved = queryRepository.save(query);
        populateDisplay(saved);
        return saved;
    }

    /** Resolve a single query, recording who/what closed it. */
    public QueryRecord resolveQuery(int queryId, String resolutionNote) {
        if (resolutionNote == null || resolutionNote.trim().isEmpty()) {
            throw new IllegalArgumentException("A resolution note is required to resolve a query");
        }

        QueryRecord query = queryRepository.findById(queryId)
                .orElseThrow(() -> new RuntimeException("Query not found: " + queryId));

        if (query.getStatus() == QueryStatus.RESOLVED) {
            throw new IllegalStateException("Query " + queryId + " is already resolved");
        }

        query.setStatus(QueryStatus.RESOLVED);
        query.setResolutionNote(resolutionNote.trim());
        query.setResolvedAt(LocalDateTime.now());

        QueryRecord saved = queryRepository.save(query);
        populateDisplay(saved);
        return saved;
    }

    public List<QueryRecord> getAllQueries() {
        List<QueryRecord> queries = queryRepository.findAllByOrderByRaisedAtDesc();
        queries.forEach(this::populateDisplay);
        return queries;
    }

    public List<QueryRecord> getOpenQueries() {
        List<QueryRecord> queries = queryRepository.findByStatusOrderByRaisedAtDesc(QueryStatus.OPEN);
        queries.forEach(this::populateDisplay);
        return queries;
    }

    public int countOpenQueries() {
        return queryRepository.countByStatus(QueryStatus.OPEN);
    }

    /** Copy a few visit/subject fields onto the transient display fields for the UI. */
    private void populateDisplay(QueryRecord q) {
        VisitRecord v = q.getVisit();
        if (v != null) {
            q.setVisitId(v.getId());
            q.setVisitName(v.getVisitName());
            if (v.getTrialSubject() != null) {
                q.setSubjectId(v.getTrialSubject().getSubjectId());
            }
        }
    }
}

