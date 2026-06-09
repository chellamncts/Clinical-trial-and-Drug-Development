package com.genc.ctds.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class VisitRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long subjectId;
    private String visitName;
    private LocalDate visitDate;

    @Enumerated(EnumType.STRING)
    private CrfStatus crfStatus;

    private int queryCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
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

    public int getQueryCount() {
        return queryCount;
    }

    public void setQueryCount(int queryCount) {
        this.queryCount = queryCount;
    }
}
