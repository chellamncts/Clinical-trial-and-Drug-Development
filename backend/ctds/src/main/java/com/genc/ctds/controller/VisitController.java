package com.genc.ctds.controller;

import com.genc.ctds.model.VisitRecord;
import com.genc.ctds.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RestController
@RequestMapping("/visits")
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

    @PutMapping("/lock/{visitId}")
    public VisitRecord lockCrf(@PathVariable Long visitId) {
        return visitService.lockCrf(visitId);
    }

}
