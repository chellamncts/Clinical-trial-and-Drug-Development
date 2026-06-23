package com.genc.ctds.crcBackend.controller;


import com.genc.ctds.crcBackend.model.CrcDashboard;
import com.genc.ctds.subjectenrollment.service.SubjectService;
import com.genc.ctds.visitscheduling.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Controller
public class CrcDashboardController {
    @Autowired
    private VisitService visitService;
    @Autowired
    private SubjectService subjectService;



    @GetMapping("/crcDashboard")
    public String showDashboard(Model model) {
        CrcDashboard crcDashboard = new CrcDashboard();

        crcDashboard.setScheduledVisits(visitService.countScheduled());
        crcDashboard.setPendingCrfs(visitService.countPendingCrfs());
        crcDashboard.setCompletedCrfs(visitService.countCompletedCrfs());
        crcDashboard.setLockedCrfs(visitService.countLockedCrfs());
      //  crcDashboard.setOpenQueries(crcService.countOpenQueries());
        crcDashboard.setEnrolledSubjects((int) subjectService.countEnrollment());

        crcDashboard.setVisitsTimelineLabels(visitService.getVisitsTimelineLabels());
        crcDashboard.setVisitsTimelineData(visitService.getVisitsTimelineData());
        crcDashboard.setSubjectEnrollmentData(Arrays.asList(
                (int) subjectService.countScreened(),
                (int) subjectService.countEnrollment(),
                (int) subjectService.countWithdrawn()
        ));

        model.addAttribute("crcDashboard", crcDashboard);

        return "crcDashboard";
    }

}

