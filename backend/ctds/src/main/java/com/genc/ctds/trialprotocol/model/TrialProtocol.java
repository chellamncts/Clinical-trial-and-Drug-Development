package com.genc.ctds.trialprotocol.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trial_protocols")
public class TrialProtocol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long protocolId;

    @Column(nullable = false, length = 150)
    private String trialTitle;

    @Column(nullable = false, length = 200)
    private String therapeuticArea;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Phase phase;

    @Column(nullable = false)
    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProtocolStatus protocolStatus;

    @Column(nullable = false)
    private Integer versionNumber = 1;

    @Lob
    private String inclusionCriteria;

    @Lob
    private String exclusionCriteria;

    @OneToMany(mappedBy = "trialProtocol", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClinicalSite> sites = new ArrayList<>();

    public Long getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(Long protocolId) {
        this.protocolId = protocolId;
    }

    public String getTrialTitle() {
        return trialTitle;
    }

    public void setTrialTitle(String trialTitle) {
        this.trialTitle = trialTitle;
    }

    public String getTherapeuticArea() {
        return therapeuticArea;
    }

    public void setTherapeuticArea(String therapeuticArea) {
        this.therapeuticArea = therapeuticArea;
    }

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public ProtocolStatus getProtocolStatus() {
        return protocolStatus;
    }

    public void setProtocolStatus(ProtocolStatus protocolStatus) {
        this.protocolStatus = protocolStatus;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }

    public String getInclusionCriteria() {
        return inclusionCriteria;
    }

    public void setInclusionCriteria(String inclusionCriteria) {
        this.inclusionCriteria = inclusionCriteria;
    }

    public String getExclusionCriteria() {
        return exclusionCriteria;
    }

    public void setExclusionCriteria(String exclusionCriteria) {
        this.exclusionCriteria = exclusionCriteria;
    }

    public List<ClinicalSite> getSites() {
        return sites;
    }

    public void setSites(List<ClinicalSite> sites) {
        this.sites = sites;
    }

    public Long getProtocol_id() {
        return protocolId;
    }

    public void setProtocol_id(Long protocolId) {
        this.protocolId = protocolId;
    }

    public String getProtocol_title() {
        return trialTitle;
    }

    public void setProtocol_title(String protocolTitle) {
        this.trialTitle = protocolTitle;
    }
}
