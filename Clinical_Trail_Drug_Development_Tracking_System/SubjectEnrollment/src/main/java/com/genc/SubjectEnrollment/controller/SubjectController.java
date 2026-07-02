package com.genc.SubjectEnrollment.controller;

import com.genc.SubjectEnrollment.dto.ConsentForm;
import com.genc.SubjectEnrollment.dto.SubjectRequestDTO;
import com.genc.SubjectEnrollment.dto.SubjectResponseDTO;
import com.genc.SubjectEnrollment.model.SubjectStatus;
import com.genc.SubjectEnrollment.service.SubjectService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    /** Screen a new subject (status → SCREENED) */
    @PostMapping("/screen")
    public ResponseEntity<SubjectResponseDTO> screenSubject(
            @Valid @RequestBody SubjectRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subjectService.screenSubject(dto));
    }

    /** Enroll a screened subject (status → ENROLLED) */
    @PutMapping("/{id}/enroll")
    public ResponseEntity<SubjectResponseDTO> enrollSubject(@PathVariable Integer id) {
        return ResponseEntity.ok(subjectService.enrollSubject(id));
    }

    /** Capture or update informed consent */
    @PutMapping("/{id}/consent")
    public ResponseEntity<SubjectResponseDTO> captureConsent(
            @PathVariable Integer id,
            @RequestBody ConsentForm form) {
        return ResponseEntity.ok(subjectService.captureConsent(id, form));
    }

    /** Withdraw a subject (status → WITHDRAWN) */
    @PutMapping("/{id}/withdraw")
    public ResponseEntity<SubjectResponseDTO> withdrawSubject(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        String reason = body.getOrDefault("reason", "No reason provided");
        return ResponseEntity.ok(subjectService.withdrawSubject(id, reason));
    }

    /** Complete a subject (status → COMPLETED) */
    @PutMapping("/{id}/complete")
    public ResponseEntity<SubjectResponseDTO> completeSubject(@PathVariable Integer id) {
        return ResponseEntity.ok(subjectService.completeSubject(id));
    }

    /** Get a single subject by ID */
    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponseDTO> getSubjectById(@PathVariable Integer id) {
        return ResponseEntity.ok(subjectService.getSubjectById(id));
    }

    /** Get all subjects */
    @GetMapping
    public ResponseEntity<List<SubjectResponseDTO>> getAllSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }

    /** Get subjects by protocol */
    @GetMapping("/protocol/{protocolId}")
    public ResponseEntity<List<SubjectResponseDTO>> getSubjectsByProtocol(
            @PathVariable Integer protocolId) {
        return ResponseEntity.ok(subjectService.getSubjectsByProtocol(protocolId));
    }

    /** Get subjects filtered by status */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<SubjectResponseDTO>> getSubjectsByStatus(
            @PathVariable SubjectStatus status) {
        return ResponseEntity.ok(subjectService.getSubjectsByStatus(status));
    }
}
