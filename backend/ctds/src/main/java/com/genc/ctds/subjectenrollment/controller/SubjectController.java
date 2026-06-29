package com.genc.ctds.subjectenrollment.controller;

import com.genc.ctds.subjectenrollment.model.TrialSubject;
import com.genc.ctds.subjectenrollment.service.SubjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API for subject enrollment, consumed by the static frontend pages
 * (enrollment.html / enrollmentStatus.html) via fetch().
 */
@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService service;

    public SubjectController(SubjectService service) {
        this.service = service;
    }

    // List all subjects
    @GetMapping
    public List<TrialSubject> getAllSubjects() {
        return service.getAllSubjects();
    }

    // Screen a new subject
    @PostMapping("/screen")
    public ResponseEntity<?> screenSubject(@RequestBody TrialSubject subject) {
        if (!subject.isConsentProvided()) {
            return ResponseEntity.badRequest().body(
                    Map.of("message",
                            "Subject must personally provide informed consent before screening."));
        }
        if (subject.getEnrollmentDate() == null) {
            subject.setEnrollmentDate(LocalDate.now());
        }
        service.screenSubject(subject);
        return ResponseEntity.ok(subject);
    }

    // Enroll a screened subject
    @PostMapping("/{subjectId}/enroll")
    public ResponseEntity<?> enrollSubject(@PathVariable Integer subjectId) {
        boolean enrolled = service.enrollSubject(subjectId);
        Map<String, Object> response = new HashMap<>();
        response.put("success", enrolled);
        if (!enrolled) {
            response.put("message", "Consent must be captured before enrollment.");
        }
        return ResponseEntity.ok(response);
    }

    // Withdraw a subject
    @PostMapping("/{subjectId}/withdraw")
    public ResponseEntity<?> withdrawSubject(@PathVariable Integer subjectId) {
        service.withdrawSubject(subjectId);
        return ResponseEntity.ok(Map.of("success", true));
    }
}