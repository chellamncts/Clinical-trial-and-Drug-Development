package com.genc.ctds.subjectenrollment.controller;

import com.genc.ctds.subjectenrollment.model.TrialSubject;
import com.genc.ctds.subjectenrollment.model.SubjectStatus;
import com.genc.ctds.subjectenrollment.service.SubjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@Controller
public class SubjectController {

    private final SubjectService service;
    public SubjectController(SubjectService service) {
        this.service = service;
    }

    //Load form
    @GetMapping("/enrollment")
    public String loadForm(Model model) {
        model.addAttribute("subject", new TrialSubject());
        return "enrollment";
    }
    @PostMapping("/enroll")
    public String enroll(Model model, @ModelAttribute TrialSubject subject) {
        if (subject.getEnrollmentDate() == null) {
            subject.setEnrollmentDate(LocalDate.now());
        }
        service.saveSubject(subject);
        List<TrialSubject> subjects = service.getAllSubjects();
        model.addAttribute("subjects", subjects);
        return "enrollmentStatus";
    }
    //  View all subjects
    @GetMapping("/enrollmentStatus")
    public String viewEnrollmentStatus(Model model) {
        List<TrialSubject> subjects = service.getAllSubjects();
        model.addAttribute("subjects", subjects);
        return "enrollmentStatus";
    }
}