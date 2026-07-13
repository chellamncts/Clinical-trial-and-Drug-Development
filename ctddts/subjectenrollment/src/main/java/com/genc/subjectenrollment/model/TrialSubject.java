package com.genc.subjectenrollment.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "TrialSubject")
public class TrialSubject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer subjectId;

    @Column(nullable = false)
    private Integer protocolId;

    @Column(nullable = false)
    private Integer siteId;

    @Column(nullable = false)
    private LocalDate screeningDate;

    @Column(nullable = false)
    private LocalDate enrollmentDate;

    @Column(nullable = false)
    private String studyArm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubjectStatus subjectStatus;

    @Column(nullable = false)
    private String consentVersion;

    @Column(nullable = false)
    private LocalDate consentDate;

    @Column(nullable = false)
    private String consentedBy;

    @Column(nullable = false)
    private String withdrawalReason;

    public TrialSubject() {}

    public TrialSubject(Integer subjectId, Integer protocolId, Integer siteId,
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
}
