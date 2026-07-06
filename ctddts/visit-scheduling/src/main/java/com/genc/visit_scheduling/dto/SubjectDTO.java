package com.genc.visit_scheduling.dto;

public class SubjectDTO {
    private Integer subjectId;
    private Integer protocolId;
    private Integer siteId;
    private String subjectStatus;
    private String studyArm;
    private String screeningDate;
    private String enrollmentDate;

    public SubjectDTO() {}

    public Integer getSubjectId()         { return subjectId; }
    public void setSubjectId(Integer v)   { this.subjectId = v; }
    public Integer getProtocolId()        { return protocolId; }
    public void setProtocolId(Integer v)  { this.protocolId = v; }
    public Integer getSiteId()            { return siteId; }
    public void setSiteId(Integer v)      { this.siteId = v; }
    public String getSubjectStatus()      { return subjectStatus; }
    public void setSubjectStatus(String v){ this.subjectStatus = v; }
    public String getStudyArm()           { return studyArm; }
    public void setStudyArm(String v)     { this.studyArm = v; }
    public String getScreeningDate()      { return screeningDate; }
    public void setScreeningDate(String v){ this.screeningDate = v; }
    public String getEnrollmentDate()     { return enrollmentDate; }
    public void setEnrollmentDate(String v){ this.enrollmentDate = v; }
}

