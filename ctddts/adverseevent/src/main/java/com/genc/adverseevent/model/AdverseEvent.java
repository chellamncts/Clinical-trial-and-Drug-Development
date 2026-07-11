package com.genc.adverseevent.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "adverse_event")
public class AdverseEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private Long eventId;

    @Column(nullable = false)
    @NotNull(message = "Subject ID is required")
    private Long subjectId;

    private LocalDate eventOnsetDate;

    @Column(columnDefinition = "TEXT")
    private String eventDescription;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventStatus eventStatus = EventStatus.REPORTED;
    private String seriousness;

    private boolean safetyReportSubmitted = false;

    private String meddraCode;

    @NotNull(message = "Visit ID is mandatory")
    private Long visitId;

    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }

    public LocalDate getEventOnsetDate() { return eventOnsetDate; }
    public void setEventOnsetDate(LocalDate eventOnsetDate) { this.eventOnsetDate = eventOnsetDate; }

    public String getEventDescription() { return eventDescription; }
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public EventStatus getEventStatus() { return eventStatus; }
    public void setEventStatus(EventStatus eventStatus) { this.eventStatus = eventStatus; }

    public String getSeriousness() { return seriousness; }
    public void setSeriousness(String seriousness) { this.seriousness = seriousness; }

    public boolean isSafetyReportSubmitted() { return safetyReportSubmitted; }
    public void setSafetyReportSubmitted(boolean safetyReportSubmitted) { this.safetyReportSubmitted = safetyReportSubmitted; }

    public String getMeddraCode() { return meddraCode; }
    public void setMeddraCode(String meddraCode) { this.meddraCode = meddraCode; }

    public Long getVisitId() { return visitId; }
    public void setVisitId(Long visitId) { this.visitId = visitId; }
}