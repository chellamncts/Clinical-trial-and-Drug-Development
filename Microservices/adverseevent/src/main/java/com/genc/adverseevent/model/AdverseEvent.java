package com.genc.adverseevent.model;

import com.genc.adverseevent.model.EventStatus;
import com.genc.adverseevent.model.Severity;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "adverse_event")
public class AdverseEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="event_id")
    private Long eventId; // Ensure this matches template bindings

    private String subjectId;
    private LocalDate eventOnsetDate;
    private String eventDescription;

    @Enumerated(EnumType.STRING)
    private Severity severity;

    @Enumerated(EnumType.STRING)
    private EventStatus eventStatus;

    // Getters and Setters
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }

    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }

    public LocalDate getEventOnsetDate() { return eventOnsetDate; }
    public void setEventOnsetDate(LocalDate eventOnsetDate) { this.eventOnsetDate = eventOnsetDate; }

    public String getEventDescription() { return eventDescription; }
    public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public EventStatus getEventStatus() { return eventStatus; }
    public void setEventStatus(EventStatus eventStatus) { this.eventStatus = eventStatus; }
}