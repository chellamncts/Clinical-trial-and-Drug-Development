package com.genc.ctds.visitscheduling.model;

import com.genc.ctds.subjectenrollment.model.TrialSubject;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class VisitRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", foreignKey = @ForeignKey(name = "fk_visit_subject"))
    private TrialSubject trialSubject;

    @Transient
    private Integer subjectId;

    public Integer getSubjectId() { return subjectId; }
    public void setSubjectId(Integer subjectId) { this.subjectId = subjectId; }

    private String visitName;
    private LocalDate visitDate;

    @Enumerated(EnumType.STRING)
    private CrfStatus crfStatus;

    private Integer queryCount;

    public Integer getId() {
        return id;
    }

    public TrialSubject getTrialSubject() {
        return trialSubject;
    }

    public void setTrialSubject(TrialSubject trialSubject) {
        this.trialSubject = trialSubject;
    }

    public String getVisitName() {
        return visitName;
    }

    public void setVisitName(String visitName) {
        this.visitName = visitName;
    }

    public LocalDate getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(LocalDate visitDate) {
        this.visitDate = visitDate;
    }

    public CrfStatus getCrfStatus() {
        return crfStatus;
    }

    public void setCrfStatus(CrfStatus crfStatus) {
        this.crfStatus = crfStatus;
    }

    public Integer getQueryCount() {
        return queryCount;
    }

    public void setQueryCount(Integer queryCount) {
        this.queryCount = queryCount;
    }
}

