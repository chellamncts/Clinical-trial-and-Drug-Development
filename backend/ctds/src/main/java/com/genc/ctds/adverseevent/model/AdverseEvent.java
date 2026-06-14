package com.genc.ctds.adverseevent.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "adverse_event")
public class AdverseEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subjectId;

    private LocalDate eventOnsetDate;

    private String eventDescription;

    private String severity;

    private String eventStatus;
    public Long getId() { return id; }

    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }

    public LocalDate getEventOnsetDate() { return eventOnsetDate; }
    public void setEventOnsetDate(LocalDate eventOnsetDate) { this.eventOnsetDate = eventOnsetDate; }

    public String getEventDescription() { return eventDescription; }
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getEventStatus() { return eventStatus; }
    public void setEventStatus(String eventStatus) { this.eventStatus = eventStatus; }
}
