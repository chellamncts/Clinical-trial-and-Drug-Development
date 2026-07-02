package com.genc.visit_scheduling.controller;

import com.genc.visit_scheduling.model.VisitRecord;
import com.genc.visit_scheduling.service.VisitService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
//controller

@RestController
@RequestMapping("/api/visits")
public class VisitController {
    private final VisitService service;
    public VisitController(VisitService service) { this.service = service; }

    @GetMapping
    public List<VisitRecord> all() { return service.all(); }

    @PostMapping
    public VisitRecord scheduleVisit(@RequestBody VisitRecord v) { return service.scheduleVisit(v); }

    @PutMapping("/{id}/crf")
    public VisitRecord recordCrfData(@PathVariable Long id, @RequestParam(defaultValue = "0") Integer queryCount) {
        return service.recordCrfData(id, queryCount);
    }

    @PutMapping("/{id}/lock")
    public VisitRecord lockCrf(@PathVariable Long id) { return service.lockCrf(id); }

    @GetMapping("/subject/{subjectId}")
    public List<VisitRecord> getVisitHistory(@PathVariable Long subjectId) { return service.getVisitHistory(subjectId); }
}

