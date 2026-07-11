package com.genc.ctds.samplelog.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "sample_log")
public class SampleLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sampleId;

    private int subjectId;

    @Column(length = 50)
    private String sampleType;

    private LocalDate collectionDate;

    @Column(length = 255)
    private String labResult;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('COLLECTED','IN_TRANSIT','ANALYZED','DESTROYED')")
    private SampleStatus sampleStatus;

    public SampleLog() {
        this.sampleStatus = SampleStatus.COLLECTED;
    }

    public int getSampleId() { return sampleId; }
    public void setSampleId(int sampleId) { this.sampleId = sampleId; }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public String getSampleType() { return sampleType; }
    public void setSampleType(String sampleType) { this.sampleType = sampleType; }

    public LocalDate getCollectionDate() { return collectionDate; }
    public void setCollectionDate(LocalDate collectionDate) { this.collectionDate = collectionDate; }

    public String getLabResult() { return labResult; }
    public void setLabResult(String labResult) { this.labResult = labResult; }

    public SampleStatus getSampleStatus() { return sampleStatus; }
    public void setSampleStatus(SampleStatus sampleStatus) { this.sampleStatus = sampleStatus; }

    public enum SampleStatus {
        COLLECTED("Collected"),
        IN_TRANSIT("In Transit"),
        ANALYZED("Analyzed"),
        DESTROYED("Destroyed");

        private final String displayName;

        SampleStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
