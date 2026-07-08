package com.genc.subjectenrollment.controller;

import com.genc.subjectenrollment.dto.ConsentForm;
import com.genc.subjectenrollment.dto.SubjectRequestDTO;
import com.genc.subjectenrollment.dto.SubjectResponseDTO;
import com.genc.subjectenrollment.model.SubjectStatus;
import com.genc.subjectenrollment.service.SubjectService;
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

    @PostMapping("/screen")
    public ResponseEntity<SubjectResponseDTO> screenSubject(
            @Valid @RequestBody SubjectRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(subjectService.screenSubject(dto));
    }

    @PutMapping("/{id}/enroll")
    public ResponseEntity<SubjectResponseDTO> enrollSubject(@PathVariable Integer id) {
        return ResponseEntity.ok(subjectService.enrollSubject(id));
    }

    @PutMapping("/{id}/consent")
    public ResponseEntity<SubjectResponseDTO> captureConsent(
            @PathVariable Integer id,
            @RequestBody ConsentForm form) {
        return ResponseEntity.ok(subjectService.captureConsent(id, form));
    }

    @PutMapping("/{id}/withdraw")
    public ResponseEntity<SubjectResponseDTO> withdrawSubject(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {
        String reason = body.getOrDefault("reason", "No reason provided");
        return ResponseEntity.ok(subjectService.withdrawSubject(id, reason));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<SubjectResponseDTO> completeSubject(@PathVariable Integer id) {
        return ResponseEntity.ok(subjectService.completeSubject(id));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubjectResponseDTO> getSubjectById(@PathVariable Integer id) {
        return ResponseEntity.ok(subjectService.getSubjectById(id));
    }

    @GetMapping
    public ResponseEntity<List<SubjectResponseDTO>> getAllSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjects());
    }

    @GetMapping("/protocol/{protocolId}")
    public ResponseEntity<List<SubjectResponseDTO>> getSubjectsByProtocol(
            @PathVariable Integer protocolId) {
        return ResponseEntity.ok(subjectService.getSubjectsByProtocol(protocolId));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<SubjectResponseDTO>> getSubjectsByStatus(
            @PathVariable SubjectStatus status) {
        return ResponseEntity.ok(subjectService.getSubjectsByStatus(status));
    }
}
