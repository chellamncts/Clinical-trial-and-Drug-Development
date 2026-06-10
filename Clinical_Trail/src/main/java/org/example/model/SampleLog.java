package org.example.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class SampleLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sampleId;

    private int subjectId;

    private String sampleType;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate collectionDate;

    // Getters and setters
    public int getSampleId() { return sampleId; }
    public void setSampleId(int sampleId) { this.sampleId = sampleId; }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public String getSampleType() { return sampleType; }
    public void setSampleType(String sampleType) { this.sampleType = sampleType; }

    public LocalDate getCollectionDate() { return collectionDate; }
    public void setCollectionDate(LocalDate collectionDate) { this.collectionDate = collectionDate; }
}
