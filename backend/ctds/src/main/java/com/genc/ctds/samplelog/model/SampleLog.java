package com.genc.ctds.samplelog.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "sample_log")
@SuppressWarnings("unused")
public class SampleLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int sampleId;

    private int subjectId;

    private String sampleType;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate collectionDate;

    private String labResult;

    @Column(length = 500)
    private String custodyLog;

    private Double coldChainTemperatureC;

    @Enumerated(EnumType.STRING)
    private CustodyStatus custodyStatus;

    @Enumerated(EnumType.STRING)
    private SampleStatus sampleStatus;

    public SampleLog() {
        this.sampleStatus = SampleStatus.COLLECTED;
        this.custodyStatus = CustodyStatus.COLLECTED;
    }

    // Getters and setters
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

    public String getCustodyLog() { return custodyLog; }
    public void setCustodyLog(String custodyLog) { this.custodyLog = custodyLog; }

    public Double getColdChainTemperatureC() { return coldChainTemperatureC; }
    public void setColdChainTemperatureC(Double coldChainTemperatureC) { this.coldChainTemperatureC = coldChainTemperatureC; }

    public CustodyStatus getCustodyStatus() { return custodyStatus; }
    public void setCustodyStatus(CustodyStatus custodyStatus) { this.custodyStatus = custodyStatus; }

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

    public enum CustodyStatus {
        COLLECTED,
        HANDED_OVER,
        IN_TRANSIT,
        RECEIVED_AT_LAB,
        COMPLETED
    }
}
