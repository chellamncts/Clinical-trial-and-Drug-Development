package com.genc.trialprotocol.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@Entity
@Table(name = "trial_protocol")
public class TrialProtocol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long protocolId;

    @NotBlank
    private String trialTitle;
    private String therapeuticArea;

    @Enumerated(EnumType.STRING)
    private Phase phase;        // PHASE_I, PHASE_II, PHASE_III, PHASE_IV
    private LocalDate startDate;

    @Enumerated(EnumType.STRING)
    private ProtocolStatus protocolStatus; // DRAFT, APPROVED, ACTIVE, CLOSED

    // Eligibility criteria
    @Column(length = 2000)
    private String inclusionCriteria;
    @Column(length = 2000)
    private String exclusionCriteria;

    public Long getProtocolId() { return protocolId; }
    public void setProtocolId(Long protocolId) { this.protocolId = protocolId; }
    public String getTrialTitle() { return trialTitle; }
    public void setTrialTitle(String trialTitle) { this.trialTitle = trialTitle; }
    public String getTherapeuticArea() { return therapeuticArea; }
    public void setTherapeuticArea(String therapeuticArea) { this.therapeuticArea = therapeuticArea; }
    public Phase getPhase() { return phase; }
    public void setPhase(Phase phase) { this.phase = phase; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public ProtocolStatus getProtocolStatus() { return protocolStatus; }
    public void setProtocolStatus(ProtocolStatus protocolStatus) { this.protocolStatus = protocolStatus; }
    public String getInclusionCriteria() { return inclusionCriteria; }
    public void setInclusionCriteria(String inclusionCriteria) { this.inclusionCriteria = inclusionCriteria; }
    public String getExclusionCriteria() { return exclusionCriteria; }
    public void setExclusionCriteria(String exclusionCriteria) { this.exclusionCriteria = exclusionCriteria; }
}

