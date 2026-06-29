package com.genc.ctds.visitscheduling.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "trial_query")
public class QueryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id", foreignKey = @ForeignKey(name = "fk_query_visit"))
    @JsonIgnore
    private VisitRecord visit;

    @Transient
    private Integer visitId;
    @Transient
    private Integer subjectId;
    @Transient
    private String visitName;

    private String description;

    @Enumerated(EnumType.STRING)
    private QueryStatus status;

    @Column(length = 1000)
    private String resolutionNote;

    private LocalDateTime raisedAt;
    private LocalDateTime resolvedAt;


    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public VisitRecord getVisit() { return visit; }
    public void setVisit(VisitRecord visit) { this.visit = visit; }

    public Integer getVisitId() { return visitId; }
    public void setVisitId(Integer visitId) { this.visitId = visitId; }

    public Integer getSubjectId() { return subjectId; }
    public void setSubjectId(Integer subjectId) { this.subjectId = subjectId; }

    public String getVisitName() { return visitName; }
    public void setVisitName(String visitName) { this.visitName = visitName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public QueryStatus getStatus() { return status; }
    public void setStatus(QueryStatus status) { this.status = status; }

    public String getResolutionNote() { return resolutionNote; }
    public void setResolutionNote(String resolutionNote) { this.resolutionNote = resolutionNote; }

    public LocalDateTime getRaisedAt() { return raisedAt; }
    public void setRaisedAt(LocalDateTime raisedAt) { this.raisedAt = raisedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }
}

