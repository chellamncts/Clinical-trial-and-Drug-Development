package com.genc.subjectenrollment.service;

import com.genc.subjectenrollment.client.ProtocolClient;
import com.genc.subjectenrollment.dto.ConsentForm;
import com.genc.subjectenrollment.dto.ProtocolDTO;
import com.genc.subjectenrollment.dto.SiteDTO;
import com.genc.subjectenrollment.dto.SubjectRequestDTO;
import com.genc.subjectenrollment.dto.SubjectResponseDTO;
import com.genc.subjectenrollment.exception.BusinessRuleException;
import com.genc.subjectenrollment.exception.ResourceNotFoundException;
import com.genc.subjectenrollment.model.SubjectStatus;
import com.genc.subjectenrollment.model.TrialSubject;
import com.genc.subjectenrollment.repository.TrialSubjectRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubjectService {

    private final TrialSubjectRepository subjectRepository;
    private final ProtocolClient protocolClient;

    public SubjectService(TrialSubjectRepository subjectRepository,
                          ProtocolClient protocolClient) {
        this.subjectRepository = subjectRepository;
        this.protocolClient = protocolClient;
    }

    // ── Screen a new subject ─────────────────────────────────────
    public SubjectResponseDTO screenSubject(SubjectRequestDTO dto) {

        // 1. Validate protocol exists and is ACTIVE via trialprotocol-service
        ProtocolDTO protocol;
        try {
            protocol = protocolClient.getProtocol(dto.getProtocolId().longValue());
        } catch (Exception e) {
            throw new BusinessRuleException(
                    "Protocol ID " + dto.getProtocolId() + " not found in trialprotocol-service.");
        }
        if (!"ACTIVE".equals(protocol.getProtocolStatus())) {
            throw new BusinessRuleException(
                    "Protocol must be ACTIVE to screen a subject. Current status: "
                            + protocol.getProtocolStatus());
        }

        // 2. Validate site only if siteId is provided
        if (dto.getSiteId() != null) {
            SiteDTO site;
            try {
                site = protocolClient.getSite(dto.getSiteId().longValue());
            } catch (Exception e) {
                throw new BusinessRuleException(
                        "Site ID " + dto.getSiteId() + " not found in trialprotocol-service.");
            }
            if (!site.getProtocolId().equals(dto.getProtocolId().longValue())) {
                throw new BusinessRuleException(
                        "Site " + dto.getSiteId() + " does not belong to protocol " + dto.getProtocolId());
            }
            if (!"ACTIVE".equals(site.getSiteStatus())) {
                throw new BusinessRuleException(
                        "Site must be ACTIVE to screen a subject. Current site status: "
                                + site.getSiteStatus());
            }
        }

        // 3. All checks passed — save subject as SCREENED
        TrialSubject subject = new TrialSubject();
        subject.setProtocolId(dto.getProtocolId());
        subject.setSiteId(dto.getSiteId());
        subject.setStudyArm(dto.getStudyArm());
        subject.setScreeningDate(dto.getScreeningDate() != null ? dto.getScreeningDate() : LocalDate.now());
        subject.setConsentVersion(dto.getConsentVersion());
        subject.setConsentDate(dto.getConsentDate());
        subject.setConsentedBy(dto.getConsentedBy());
        subject.setSubjectStatus(SubjectStatus.SCREENED);

        return mapToResponse(subjectRepository.save(subject));
    }

    // ── Enroll a screened subject ────────────────────────────────
    public SubjectResponseDTO enrollSubject(Integer subjectId) {
        TrialSubject subject = findById(subjectId);
        if (subject.getSubjectStatus() != SubjectStatus.SCREENED) {
            throw new BusinessRuleException(
                    "Subject must be SCREENED before enrollment. Current status: " + subject.getSubjectStatus());
        }
        subject.setSubjectStatus(SubjectStatus.ENROLLED);
        subject.setEnrollmentDate(LocalDate.now());
        return mapToResponse(subjectRepository.save(subject));
    }

    // ── Capture / update informed consent ───────────────────────
    public SubjectResponseDTO captureConsent(Integer subjectId, ConsentForm form) {
        TrialSubject subject = findById(subjectId);
        if (subject.getSubjectStatus() == SubjectStatus.WITHDRAWN) {
            throw new BusinessRuleException("Cannot capture consent for a WITHDRAWN subject.");
        }
        subject.setConsentVersion(form.getConsentVersion());
        subject.setConsentDate(form.getConsentDate());
        subject.setConsentedBy(form.getConsentedBy());
        return mapToResponse(subjectRepository.save(subject));
    }

    // ── Withdraw a subject ───────────────────────────────────────
    public SubjectResponseDTO withdrawSubject(Integer subjectId, String reason) {
        TrialSubject subject = findById(subjectId);
        if (subject.getSubjectStatus() == SubjectStatus.WITHDRAWN) {
            throw new BusinessRuleException("Subject is already WITHDRAWN.");
        }
        if (subject.getSubjectStatus() == SubjectStatus.COMPLETED) {
            throw new BusinessRuleException("Cannot withdraw a COMPLETED subject.");
        }
        subject.setSubjectStatus(SubjectStatus.WITHDRAWN);
        subject.setWithdrawalReason(reason);
        return mapToResponse(subjectRepository.save(subject));
    }

    // ── Mark an enrolled subject as completed ────────────────────
    public SubjectResponseDTO completeSubject(Integer subjectId) {
        TrialSubject subject = findById(subjectId);
        if (subject.getSubjectStatus() != SubjectStatus.ENROLLED) {
            throw new BusinessRuleException(
                    "Subject must be ENROLLED to be completed. Current status: " + subject.getSubjectStatus());
        }
        subject.setSubjectStatus(SubjectStatus.COMPLETED);
        return mapToResponse(subjectRepository.save(subject));
    }

    // ── Queries ──────────────────────────────────────────────────
    public SubjectResponseDTO getSubjectById(Integer subjectId) {
        return mapToResponse(findById(subjectId));
    }

    public List<SubjectResponseDTO> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<SubjectResponseDTO> getSubjectsByProtocol(Integer protocolId) {
        return subjectRepository.findByProtocolId(protocolId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<SubjectResponseDTO> getSubjectsByStatus(SubjectStatus status) {
        return subjectRepository.findBySubjectStatus(status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ── Helpers ──────────────────────────────────────────────────
    private TrialSubject findById(Integer id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject not found with ID: " + id));
    }

    private SubjectResponseDTO mapToResponse(TrialSubject s) {
        SubjectResponseDTO response = new SubjectResponseDTO();
        response.setSubjectId(s.getSubjectId());
        response.setProtocolId(s.getProtocolId());
        response.setSiteId(s.getSiteId());
        response.setScreeningDate(s.getScreeningDate());
        response.setEnrollmentDate(s.getEnrollmentDate());
        response.setStudyArm(s.getStudyArm());
        response.setSubjectStatus(s.getSubjectStatus());
        response.setConsentVersion(s.getConsentVersion());
        response.setConsentDate(s.getConsentDate());
        response.setConsentedBy(s.getConsentedBy());
        response.setWithdrawalReason(s.getWithdrawalReason());
        return response;
    }
}