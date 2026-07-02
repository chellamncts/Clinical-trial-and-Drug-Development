package com.genc.visit_scheduling.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "visit_record")
public class VisitRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long visitId;

    private Long subjectId;
    private String visitName;
    private LocalDate visitDate;
    private String crfStatus;
    private Integer queryCount;
    private String visitWindow;

    public Long getVisitId() { return visitId; }
    public void setVisitId(Long visitId) { this.visitId = visitId; }
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    public String getVisitName() { return visitName; }
    public void setVisitName(String visitName) { this.visitName = visitName; }
    public LocalDate getVisitDate() { return visitDate; }
    public void setVisitDate(LocalDate visitDate) { this.visitDate = visitDate; }
    public String getCrfStatus() { return crfStatus; }
    public void setCrfStatus(String crfStatus) { this.crfStatus = crfStatus; }
    public Integer getQueryCount() { return queryCount; }
    public void setQueryCount(Integer queryCount) { this.queryCount = queryCount; }
    public String getVisitWindow() { return visitWindow; }
    public void setVisitWindow(String visitWindow) { this.visitWindow = visitWindow; }
}

