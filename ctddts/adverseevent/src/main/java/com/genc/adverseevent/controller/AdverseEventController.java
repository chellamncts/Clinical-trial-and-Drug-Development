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

    @GetMapping
    public ResponseEntity<List<AdverseEvent>> getAllEvents() {
        return ResponseEntity.ok(service.getAllEvents());
    }

    @PostMapping
    public ResponseEntity<AdverseEvent> reportEvent(@RequestBody AdverseEvent event) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.reportEvent(event));
    }

    @PutMapping("/{id}/classify")
    public ResponseEntity<AdverseEvent> classify(@PathVariable Long id) {
        return ResponseEntity.ok(service.classify(id));
    }

    @PutMapping("/{id}/submit")
    public ResponseEntity<AdverseEvent> submitReport(@PathVariable Long id) {
        return ResponseEntity.ok(service.submitSafetyReport(id));
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<AdverseEvent> resolve(@PathVariable Long id) {
        return ResponseEntity.ok(service.resolve(id));
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<List<AdverseEvent>> getBySubject(@PathVariable Long subjectId) {
        return ResponseEntity.ok(service.getEventsBySubject(subjectId));
    }

    @GetMapping("/visit/{visitId}")
    public ResponseEntity<List<AdverseEvent>> getByVisit(@PathVariable Long visitId) {
        return ResponseEntity.ok(service.getEventsByVisit(visitId));
    }
}