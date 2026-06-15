package com.genc.ctds.subjectenrollment.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDate;

@Entity
public class TrialSubject {
    @Id
    private String subjectId;
    private String protocolId;
    private String siteId;
    private LocalDate enrollmentDate;
    private String studyArm;
    private String subjectStatus;
    private boolean consentProvided;
    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(String protocolId) {
        this.protocolId = protocolId;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public String getStudyArm() {
        return studyArm;
    }

    public void setStudyArm(String studyArm) {
        this.studyArm = studyArm;
    }

    public String getSubjectStatus() {
        return subjectStatus;
    }

    public void setSubjectStatus(String subjectStatus) {
        this.subjectStatus = subjectStatus;
    }

    public boolean isConsentProvided() {
        return consentProvided;
    }

    public void setConsentProvided(boolean consentProvided) {
        this.consentProvided = consentProvided;
    }
}
