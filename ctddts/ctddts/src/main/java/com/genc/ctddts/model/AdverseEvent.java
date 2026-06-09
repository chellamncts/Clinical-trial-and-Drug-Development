package com.genc.ctddts.model;

import jakarta.persistence.Entity;

@Entity
public class AdverseEvent {

    private int eventId;
    private int subjectId;
    private String eventDescription;
    private String severity;

}
