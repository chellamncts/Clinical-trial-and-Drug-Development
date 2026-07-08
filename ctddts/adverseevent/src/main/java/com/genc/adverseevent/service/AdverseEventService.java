package com.genc.adverseevent.service;

import com.genc.adverseevent.model.AdverseEvent;
import com.genc.adverseevent.model.EventStatus;
import com.genc.adverseevent.model.Severity;
import com.genc.adverseevent.repository.AdverseEventRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class AdverseEventService {

    private final AdverseEventRepository repository;

    public AdverseEventService(AdverseEventRepository repository) {
        this.repository = repository;
    }

    public AdverseEvent reportEvent(AdverseEvent event) {
        event.setEventStatus(EventStatus.REPORTED);
        event.setSafetyReportSubmitted(false);
        return repository.save(event);
    }

    public AdverseEvent classify(Long id) {
        AdverseEvent e = findById(id);
        if (e.getEventStatus() == EventStatus.RESOLVED || e.getEventStatus() == EventStatus.FATAL) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot classify a " + e.getEventStatus() + " event.");
        }
        // Classify based on severity
        String seriousness = (e.getSeverity() == Severity.SEVERE) ? "SERIOUS" : "NOT_SERIOUS";
        e.setSeriousness(seriousness);
        // Assign a simple MedDRA placeholder code
        if (e.getMeddraCode() == null) {
            e.setMeddraCode(e.getSeverity() == Severity.SEVERE ? "10000228" :
                            e.getSeverity() == Severity.MODERATE ? "10047700" : "10001316");
        }
        e.setEventStatus(EventStatus.UNDER_REVIEW);
        return repository.save(e);
    }

    /** Submit safety report — only for SERIOUS + UNDER_REVIEW events */
    public AdverseEvent submitSafetyReport(Long id) {
        AdverseEvent e = findById(id);
        if (e.getEventStatus() != EventStatus.UNDER_REVIEW) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Event must be UNDER_REVIEW to submit a safety report.");
        }
        if (!"SERIOUS".equals(e.getSeriousness())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only SERIOUS events require a safety report submission.");
        }
        if (e.isSafetyReportSubmitted()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Safety report already submitted for this event.");
        }
        e.setSafetyReportSubmitted(true);
        System.out.println("[AdverseEvent] Safety report submitted for Event #" + id);
        return repository.save(e);
    }

    /** Resolve event — must be UNDER_REVIEW */
    public AdverseEvent resolve(Long id) {
        AdverseEvent e = findById(id);
        if (e.getEventStatus() != EventStatus.UNDER_REVIEW) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Event must be UNDER_REVIEW to be resolved. Current: " + e.getEventStatus());
        }
        e.setEventStatus(EventStatus.RESOLVED);
        return repository.save(e);
    }

    public List<AdverseEvent> getAllEvents() { return repository.findAll(); }

    public List<AdverseEvent> getEventsBySubject(Long subjectId) { return repository.findBySubjectId(subjectId); }

    public List<AdverseEvent> getEventsByVisit(Long visitId) { return repository.findByVisitId(visitId); }

    private AdverseEvent findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found: " + id));
    }
}