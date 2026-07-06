package com.genc.ctds.samplelog.dto;

public class CollectSampleRequest {

    private int subjectId;
    private String sampleType;
    private String collectionDate;
    private String labResult;

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public String getSampleType() { return sampleType; }
    public void setSampleType(String sampleType) { this.sampleType = sampleType; }

    public String getCollectionDate() { return collectionDate; }
    public void setCollectionDate(String collectionDate) { this.collectionDate = collectionDate; }

    public String getLabResult() { return labResult; }
    public void setLabResult(String labResult) { this.labResult = labResult; }
}

