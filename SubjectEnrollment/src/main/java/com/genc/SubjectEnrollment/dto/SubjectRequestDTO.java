package com.genc.SubjectEnrollment.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class SubjectRequestDTO {

    @NotNull(message = "Protocol ID is required")
    private Integer protocolId;

    @NotNull(message = "Site ID is required")
    private Integer siteId;

    private String studyArm;
    private LocalDate screeningDate;
    private LocalDate enrollmentDate;
    private String consentVersion;
    private LocalDate consentDate;
    private String consentedBy;

    public SubjectRequestDTO() {}

    public Integer getProtocolId() { return protocolId; }
    public void setProtocolId(Integer protocolId) { this.protocolId = protocolId; }

    public Integer getSiteId() { return siteId; }
    public void setSiteId(Integer siteId) { this.siteId = siteId; }

    public String getStudyArm() { return studyArm; }
    public void setStudyArm(String studyArm) { this.studyArm = studyArm; }

    public LocalDate getScreeningDate() { return screeningDate; }
    public void setScreeningDate(LocalDate screeningDate) { this.screeningDate = screeningDate; }

    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }

    public String getConsentVersion() { return consentVersion; }
    public void setConsentVersion(String consentVersion) { this.consentVersion = consentVersion; }

    public LocalDate getConsentDate() { return consentDate; }
    public void setConsentDate(LocalDate consentDate) { this.consentDate = consentDate; }

    public String getConsentedBy() { return consentedBy; }
    public void setConsentedBy(String consentedBy) { this.consentedBy = consentedBy; }
}
