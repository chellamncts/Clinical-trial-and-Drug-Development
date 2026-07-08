package com.genc.subjectenrollment.dto;

public class ProtocolDTO {

    private Long protocolId;
    private String trialTitle;
    private String therapeuticArea;
    private String phase;
    private String protocolStatus;
    private String inclusionCriteria;
    private String exclusionCriteria;

    public ProtocolDTO() {}

    public Long getProtocolId() { return protocolId; }
    public void setProtocolId(Long protocolId) { this.protocolId = protocolId; }

    public String getTrialTitle() { return trialTitle; }
    public void setTrialTitle(String trialTitle) { this.trialTitle = trialTitle; }

    public String getTherapeuticArea() { return therapeuticArea; }
    public void setTherapeuticArea(String therapeuticArea) { this.therapeuticArea = therapeuticArea; }

    public String getPhase() { return phase; }
    public void setPhase(String phase) { this.phase = phase; }

    public String getProtocolStatus() { return protocolStatus; }
    public void setProtocolStatus(String protocolStatus) { this.protocolStatus = protocolStatus; }

    public String getInclusionCriteria() { return inclusionCriteria; }
    public void setInclusionCriteria(String inclusionCriteria) { this.inclusionCriteria = inclusionCriteria; }

    public String getExclusionCriteria() { return exclusionCriteria; }
    public void setExclusionCriteria(String exclusionCriteria) { this.exclusionCriteria = exclusionCriteria; }
}

