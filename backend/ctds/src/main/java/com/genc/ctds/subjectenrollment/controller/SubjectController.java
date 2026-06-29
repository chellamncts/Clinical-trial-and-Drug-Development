package com.genc.ctds.subjectenrollment.controller;

import com.genc.ctds.subjectenrollment.model.SubjectStatus;
import com.genc.ctds.subjectenrollment.model.TrialSubject;
import com.genc.ctds.subjectenrollment.service.SubjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class SubjectController {

    private final SubjectService service;

    public SubjectController(SubjectService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/screenSubject";
    }

    @GetMapping("/screenSubject")
    public TrialSubject screenSubject() {
        return new TrialSubject();
    }

    // ✅ SCREEN (create)
    @PostMapping("/screenSubject")
    public Object saveScreenedSubject(@RequestBody TrialSubject subject) {

        if (!subject.isConsentProvided()) {
            return "Subject must personally provide informed consent before screening.";
        }

        if (subject.getEnrollmentDate() == null) {
            subject.setEnrollmentDate(LocalDate.now());
        }

        service.screenSubject(subject);
        return "Subject screened successfully";
    }

    // ✅ ENROLL (only update one subject)
    @PostMapping("/enrollSubject")
    public Object enrollSubject(@RequestParam Integer subjectId) {

        boolean enrolled = service.enrollSubject(subjectId);

        if (!enrolled) {
            return "Consent must be captured before enrollment.";
        }

        return "Subject enrolled successfully";
    }

    // ✅ WITHDRAW (only update one subject)
    @PostMapping("/withdrawSubject")
    public Object withdrawSubject(@RequestParam Integer subjectId) {

        service.withdrawSubject(subjectId);
        return "Subject withdrawn successfully";
    }

    // ✅ GET ALL SCREENED
    @GetMapping("/screenedSubjects")
    public List<TrialSubject> getScreenedSubjects() {
        return service.getSubjectsByStatus(SubjectStatus.SCREENED);
    }

    // ✅ GET ALL ENROLLED
    @GetMapping("/enrolledSubjects")
    public List<TrialSubject> getEnrolledSubjects() {
        return service.getSubjectsByStatus(SubjectStatus.ENROLLED);
    }

    // ✅ GET ALL WITHDRAWN
    @GetMapping("/withdrawnSubjects")
    public List<TrialSubject> getWithdrawnSubjects() {
        return service.getSubjectsByStatus(SubjectStatus.WITHDRAWN);
    }

    // ✅ ALL subjects
    @GetMapping("/enrollmentStatus")
    public List<TrialSubject> viewEnrollmentStatus() {
        return service.getAllSubjects();
    }

    // ✅ COMPLETE SUBJECT
    @PostMapping("/completeSubject")
    public Object completeSubject(@RequestParam Integer subjectId) {

        service.completeSubject(subjectId);
        return "Subject marked as completed";
    }

}