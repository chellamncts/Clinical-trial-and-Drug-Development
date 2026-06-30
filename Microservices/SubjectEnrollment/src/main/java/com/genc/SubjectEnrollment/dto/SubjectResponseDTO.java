package com.genc.SubjectEnrollment.dto;

import com.genc.SubjectEnrollment.model.SubjectStatus;
import java.time.LocalDate;

public class SubjectResponseDTO {

    private Integer subjectId;
    private Integer protocolId;
    private Integer siteId;
    private LocalDate screeningDate;
    private LocalDate enrollmentDate;
    private String studyArm;
    private SubjectStatus subjectStatus;
    private String consentVersion;
    private LocalDate consentDate;
    private String consentedBy;
    private String withdrawalReason;

    // ── No-arg constructor ───────────────────────────────────────
    public SubjectResponseDTO() {}

    // ── All-arg constructor ──────────────────────────────────────
    public SubjectResponseDTO(Integer subjectId, Integer protocolId, Integer siteId,
                               LocalDate screeningDate, LocalDate enrollmentDate, String studyArm,
                               SubjectStatus subjectStatus, String consentVersion,
                               LocalDate consentDate, String consentedBy, String withdrawalReason) {
        this.subjectId = subjectId;
        this.protocolId = protocolId;
        this.siteId = siteId;
        this.screeningDate = screeningDate;
        this.enrollmentDate = enrollmentDate;
        this.studyArm = studyArm;
        this.subjectStatus = subjectStatus;
        this.consentVersion = consentVersion;
        this.consentDate = consentDate;
        this.consentedBy = consentedBy;
        this.withdrawalReason = withdrawalReason;
    }

    // ── Getters & Setters ────────────────────────────────────────
    public Integer getSubjectId() { return subjectId; }
    public void setSubjectId(Integer subjectId) { this.subjectId = subjectId; }

    public Integer getProtocolId() { return protocolId; }
    public void setProtocolId(Integer protocolId) { this.protocolId = protocolId; }

    public Integer getSiteId() { return siteId; }
    public void setSiteId(Integer siteId) { this.siteId = siteId; }

    public LocalDate getScreeningDate() { return screeningDate; }
    public void setScreeningDate(LocalDate screeningDate) { this.screeningDate = screeningDate; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public String getStudyArm() { return studyArm; }
    public void setStudyArm(String studyArm) { this.studyArm = studyArm; }

    public SubjectStatus getSubjectStatus() { return subjectStatus; }
    public void setSubjectStatus(SubjectStatus subjectStatus) { this.subjectStatus = subjectStatus; }

    public String getConsentVersion() { return consentVersion; }
    public void setConsentVersion(String consentVersion) { this.consentVersion = consentVersion; }

    public LocalDate getConsentDate() { return consentDate; }
    public void setConsentDate(LocalDate consentDate) { this.consentDate = consentDate; }

    public String getConsentedBy() { return consentedBy; }
    public void setConsentedBy(String consentedBy) { this.consentedBy = consentedBy; }

    public String getWithdrawalReason() { return withdrawalReason; }
    public void setWithdrawalReason(String withdrawalReason) { this.withdrawalReason = withdrawalReason; }

    // ── Builder ──────────────────────────────────────────────────
    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Integer subjectId;
        private Integer protocolId;
        private Integer siteId;
        private LocalDate screeningDate;
        private LocalDate enrollmentDate;
        private String studyArm;
        private SubjectStatus subjectStatus;
        private String consentVersion;
        private LocalDate consentDate;
        private String consentedBy;
        private String withdrawalReason;

        public Builder subjectId(Integer v)          { this.subjectId = v; return this; }
        public Builder protocolId(Integer v)         { this.protocolId = v; return this; }
        public Builder siteId(Integer v)             { this.siteId = v; return this; }
        public Builder screeningDate(LocalDate v)    { this.screeningDate = v; return this; }
        public Builder enrollmentDate(LocalDate v)   { this.enrollmentDate = v; return this; }
        public Builder studyArm(String v)            { this.studyArm = v; return this; }
        public Builder subjectStatus(SubjectStatus v){ this.subjectStatus = v; return this; }
        public Builder consentVersion(String v)      { this.consentVersion = v; return this; }
        public Builder consentDate(LocalDate v)      { this.consentDate = v; return this; }
        public Builder consentedBy(String v)         { this.consentedBy = v; return this; }
        public Builder withdrawalReason(String v)    { this.withdrawalReason = v; return this; }

        public SubjectResponseDTO build() {
            return new SubjectResponseDTO(subjectId, protocolId, siteId, screeningDate, enrollmentDate,
                    studyArm, subjectStatus, consentVersion, consentDate, consentedBy, withdrawalReason);
        }
    }
}
