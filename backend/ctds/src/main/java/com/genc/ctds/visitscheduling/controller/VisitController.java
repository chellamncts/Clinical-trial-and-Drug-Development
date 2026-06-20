package com.genc.ctds.visitscheduling.controller;

import com.genc.ctds.visitscheduling.model.VisitRecord;
import com.genc.ctds.visitscheduling.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/visits")
public class VisitController {

    @Autowired
    private VisitService visitService;

    @GetMapping("/visitScheduling")
    public String showVisitPage(Model model) {
        model.addAttribute("scheduledVisits", visitService.countScheduled());
        model.addAttribute("pendingCrfs", visitService.countPendingCrfs());
        model.addAttribute("completedCrfs", visitService.countCompletedCrfs());
        model.addAttribute("lockedCrfs", visitService.countLockedCrfs());
        model.addAttribute("visit", new VisitRecord());
        return "visitScheduling";
    }

    @PostMapping("/schedule")
    public String scheduleVisit(@ModelAttribute VisitRecord visit) {
        visitService.scheduleVisit(visit);
        return "redirect:/visits/crfPage";
    }

    @GetMapping("/crfPage")
    public String showAllVisits(Model model) {
        model.addAttribute("visits", visitService.getAllVisits());
        return "crfPage";
    }

    @GetMapping("/search")
    public String searchVisits(@RequestParam("subjectId") Long subjectId, Model model) {
        model.addAttribute("visits", visitService.findBySubjectId(subjectId));
        return "crfPage";
    }

    @PostMapping("/lock/{visitId}")
    public String lockCrfFromPage(@PathVariable Long visitId) {
        visitService.lockCrf(visitId);
        return "redirect:/visits/crfPage";
    }

    @PostMapping("/complete/{visitId}")
    public String completeCrfFromPage(@PathVariable Long visitId) {
        visitService.completeCrf(visitId);
        return "redirect:/visits/crfPage";
    }
}