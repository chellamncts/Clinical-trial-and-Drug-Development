package com.genc.ctds.crcBackend.controller;


import com.genc.ctds.crcBackend.model.CrcDashboard;
import com.genc.ctds.visitscheduling.model.VisitRecord;
import com.genc.ctds.visitscheduling.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;

@Controller
public class CrcDashboardController {
    @Autowired
    private VisitService visitService;
//    private final QueryService queryService;
//    private final SubjectService subjectService;


    @GetMapping("/crcDashboard")
    public String showDashboard(Model model) {
        CrcDashboard crcDashboard = new CrcDashboard();

//         Summary counts
        crcDashboard.setScheduledVisits(visitService.countScheduled());
        crcDashboard.setPendingCrfs(visitService.countPendingCrfs());
        crcDashboard.setCompletedCrfs(visitService.countCompletedCrfs());
        crcDashboard.setLockedCrfs(visitService.countLockedCrfs());
//        crcDashboard.setOpenQueries(visitService.countOpen());
//        crcDashboard.setEnrolledSubjects(subjectService.countEnrolled());

        // Chart data
        crcDashboard.setVisitsTimelineLabels(Arrays.asList("2026-06-01", "2026-06-02", "2026-06-03"));
        crcDashboard.setVisitsTimelineData(Arrays.asList(3, 5, 4));
        crcDashboard.setSubjectEnrollmentData(Arrays.asList(10, 25, 2));

//         Recent visits (replace with dynamic service call)
        List<VisitRecord> recentVisits = visitService.getRecentVisits();
        crcDashboard.setRecentVisits(recentVisits);

        // Add to Thymeleaf model
        model.addAttribute("crcDashboard", crcDashboard);

        return "crcDashboard"; // maps to dashboard.html (Thymeleaf template)
    }
}

