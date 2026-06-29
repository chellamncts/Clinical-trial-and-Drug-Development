package com.genc.ctds.visitscheduling.controller;

import com.genc.ctds.visitscheduling.model.VisitRecord;
import com.genc.ctds.visitscheduling.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/visits")
public class VisitController {

    @Autowired
    private VisitService visitService;

    // GET all visits  ->  JSON list
    @GetMapping
    public List<VisitRecord> getAllVisits() {
        return visitService.getVisitHistory();
    }

    // Schedule a visit  ->  returns saved VisitRecord
    @PostMapping("/schedule")
    public ResponseEntity<VisitRecord> scheduleVisit(@RequestBody VisitRecord visit) {
        return ResponseEntity.ok(visitService.scheduleVisit(visit));
    }

    // Search by subjectId  ->  JSON list
    @GetMapping("/search")
    public List<VisitRecord> searchVisits(@RequestParam("subjectId") int subjectId) {
        return visitService.findBySubjectId(subjectId);
    }

    // Record CRF data  ->  no body needed, 200 OK
    @PostMapping("/recordCrf/{id}")
    public ResponseEntity<Void> recordCrf(@PathVariable("id") int id,
                                          @RequestBody Map<String, String> crfData) {
        visitService.recordCrfData(id, crfData);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/lock/{visitId}")
    public ResponseEntity<VisitRecord> lockCrf(@PathVariable int visitId) {
        return ResponseEntity.ok(visitService.lockCrf(visitId));
    }
}