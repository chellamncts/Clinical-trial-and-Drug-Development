package com.genc.ctds.trialprotocol.model;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
public class TrialProtocol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long protocol_id;

    @Column(nullable = false, unique = true, length = 50)
    private String protocol_title;

    @Column(nullable = false, length = 200)
    private String therapeuticArea;
    @Enumerated(EnumType.STRING)
    private Phase phase;
    private LocalDate startDate;
    @Enumerated(EnumType.STRING)
    private ProtocolStatus protocolStatus;

    public Long getProtocol_id() {
        return protocol_id;
    }

    public void setProtocol_id(Long protocol_id) {
        this.protocol_id = protocol_id;
    }

    public String getProtocol_title() {
        return protocol_title;
    }

    public void setProtocol_title(String protocol_title) {
        this.protocol_title = protocol_title;
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
}
