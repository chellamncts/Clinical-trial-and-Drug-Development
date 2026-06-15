package com.genc.ctds.subjectenrollment.controller;

import com.genc.ctds.subjectenrollment.model.TrialSubject;
import com.genc.ctds.subjectenrollment.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Controller
public class SubjectController {
    @Autowired
    private SubjectService service;
    public SubjectController(SubjectService service) {
        this.service = service;
    }
    @GetMapping("/enrollment")
    public String loadForm(Model model) {
        model.addAttribute("subject", new TrialSubject());
        return "enrollment";
    }
    @PostMapping("/enroll")
    public String enroll(Model model, @ModelAttribute TrialSubject subject, @RequestParam("action") String action) {
        if (subject.getEnrollmentDate() == null) {
            subject.setEnrollmentDate(LocalDate.now());
        }
        switch(action) {
            case "enroll":
                subject.setSubjectStatus("ENROLLED");
                break;
            case "screen":
                subject.setSubjectStatus("SCREENED");
                break;
            case "withdraw":
                subject.setSubjectStatus("WITHDRAWN");
                break;
            default:
                subject.setSubjectStatus("SCREENED");
                break;
        }
        service.saveSubject(subject);
        // Get all subjects from database
        List<TrialSubject> subjects = service.getAllSubjects();
        model.addAttribute("subjects", subjects);
        return "enrollmentStatus";
    }

    @GetMapping("/enrollmentStatus")
    public String viewEnrollmentStatus(Model model) {
        List<TrialSubject> subjects = service.getAllSubjects();
        model.addAttribute("subjects", subjects);
        return "enrollmentStatus";
    }
}
