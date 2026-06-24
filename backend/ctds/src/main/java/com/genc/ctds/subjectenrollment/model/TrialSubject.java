package com.genc.ctds.subjectenrollment.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class TrialSubject {

    @Id
    private Integer subjectId;
    private Integer protocolId;
    private Integer siteId;
    private LocalDate enrollmentDate;
    private String studyArm;

    @Enumerated(EnumType.STRING)
    private SubjectStatus subjectStatus;
    private boolean consentProvided;

    @OneToMany(mappedBy = "trialSubject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VisitRecord> visitRecords;

    public List<VisitRecord> getVisitRecords() {
        return visitRecords;
    }

    public void setVisitRecords(List<VisitRecord> visitRecords) {
        this.visitRecords = visitRecords;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(Integer protocolId) {
        this.protocolId = protocolId;
    }

    public Integer getSiteId() {
        return siteId;
    }

    public void setSiteId(Integer siteId) {
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

    public SubjectStatus getSubjectStatus() {
        return subjectStatus;
    }

    public void setSubjectStatus(SubjectStatus subjectStatus) {
        this.subjectStatus = subjectStatus;
    }
    public boolean isConsentProvided() {
        return consentProvided;
    }
    public void setConsentProvided(boolean consentProvided) {
        this.consentProvided = consentProvided;
    }
}