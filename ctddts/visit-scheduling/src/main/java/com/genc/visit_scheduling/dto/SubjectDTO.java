package com.genc.visit_scheduling.dto;

public class SubjectDTO {
    private Integer subjectId;
    private Integer protocolId;
    private Integer siteId;
    private String subjectStatus;
    public Integer getSubjectId()         { return subjectId; }
    public void setSubjectId(Integer v)   { this.subjectId = v; }
    public Integer getProtocolId()        { return protocolId; }
    public void setProtocolId(Integer v)  { this.protocolId = v; }
    public Integer getSiteId()            { return siteId; }
    public void setSiteId(Integer v)      { this.siteId = v; }
    public String getSubjectStatus()      { return subjectStatus; }
    public void setSubjectStatus(String v){ this.subjectStatus = v; }
}

