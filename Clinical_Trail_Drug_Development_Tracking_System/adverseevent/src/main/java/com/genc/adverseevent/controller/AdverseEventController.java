package com.genc.adverseevent.controller;

import com.genc.adverseevent.model.AdverseEvent;
import com.genc.adverseevent.service.AdverseEventService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class AdverseEventController {

    private final AdverseEventService service;

    public AdverseEventController(AdverseEventService service) {
        this.service = service;
    }

    /** GET /api/events — all events */
    @GetMapping
    public ResponseEntity<List<AdverseEvent>> getAllEvents() {
        return ResponseEntity.ok(service.getAllEvents());
    }

    /** POST /api/events — report a new event */
    @PostMapping
    public ResponseEntity<AdverseEvent> reportEvent(@RequestBody AdverseEvent event) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.reportEvent(event));
    }

    /** PUT /api/events/{id}/classify — classify seriousness → UNDER_REVIEW */
    @PutMapping("/{id}/classify")
    public ResponseEntity<AdverseEvent> classify(@PathVariable Long id) {
        return ResponseEntity.ok(service.classify(id));
    }

    /** PUT /api/events/{id}/submit — submit safety report */
    @PutMapping("/{id}/submit")
    public ResponseEntity<AdverseEvent> submitReport(@PathVariable Long id) {
        return ResponseEntity.ok(service.submitSafetyReport(id));
    }

    /** PUT /api/events/{id}/resolve — resolve the event */
    @PutMapping("/{id}/resolve")
    public ResponseEntity<AdverseEvent> resolve(@PathVariable Long id) {
        return ResponseEntity.ok(service.resolve(id));
    }

    /** GET /api/events/subject/{subjectId} — events for a specific subject */
    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<AdverseEvent>> getBySubject(@PathVariable Long subjectId) {
        return ResponseEntity.ok(service.getEventsBySubject(subjectId));
    }

    /** GET /api/events/visit/{visitId} — events linked to a specific visit */
    @GetMapping("/visit/{visitId}")
    public ResponseEntity<List<AdverseEvent>> getByVisit(@PathVariable Long visitId) {
        return ResponseEntity.ok(service.getEventsByVisit(visitId));
    }
}