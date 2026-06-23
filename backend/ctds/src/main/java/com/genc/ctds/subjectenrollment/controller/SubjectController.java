package com.genc.ctds.subjectenrollment.controller;

import com.genc.ctds.subjectenrollment.model.TrialSubject;
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

    @GetMapping("/screenSubject")
    public String screenSubject(Model model) {
        model.addAttribute("subject", new TrialSubject());
        return "enrollment";
    }

    @PostMapping("/screenSubject")
    public String saveScreenedSubject(Model model, @ModelAttribute TrialSubject subject) {
        if (!subject.isConsentProvided()) {
            model.addAttribute("subject", subject);
            model.addAttribute("consentError", "Subject must personally provide informed consent before screening.");
            return "enrollment";
        }
        if (subject.getEnrollmentDate() == null) {
            subject.setEnrollmentDate(LocalDate.now());
        }
        service.screenSubject(subject);
        List<TrialSubject> subjects = service.getAllSubjects();
        model.addAttribute("subjects", subjects);
        return "enrollmentStatus";
    }

    @PostMapping("/enrollSubject")
    public String enrollSubject(@RequestParam Integer subjectId, Model model) {
        boolean enrolled = service.enrollSubject(subjectId);
        if (!enrolled) {
            model.addAttribute("message", "Consent must be captured before enrollment.");
        }
        List<TrialSubject> subjects = service.getAllSubjects();
        model.addAttribute("subjects", subjects);
        return "enrollmentStatus";
    }

    @PostMapping("/withdrawSubject")
    public String withdrawSubject(@RequestParam Integer subjectId, Model model) {
        service.withdrawSubject(subjectId);
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