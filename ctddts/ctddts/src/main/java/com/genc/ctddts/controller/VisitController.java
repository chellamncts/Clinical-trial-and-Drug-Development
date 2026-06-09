package com.genc.ctddts.controller;

import com.genc.ctddts.model.VisitRecord;
import com.genc.ctddts.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/visits")
@CrossOrigin(origins = "http://localhost:63342")
public class VisitController {
    @Autowired
    private VisitService visitService;

    @PostMapping("/schedule")
    public VisitRecord scheduleVisit(@RequestBody VisitRecord visit) {
        return visitService.scheduleVisit(visit);
    }

    @GetMapping("/history/{subjectId}")
    public List<VisitRecord> getVisitHistory(@PathVariable Long subjectId) {
        return visitService.getVisitHistory(subjectId);
    }

    @PostMapping("/{visitId}/lock")
    public VisitRecord lockCrf(@PathVariable Long visitId) {
        return visitService.lockCrf(visitId);
    }
}

