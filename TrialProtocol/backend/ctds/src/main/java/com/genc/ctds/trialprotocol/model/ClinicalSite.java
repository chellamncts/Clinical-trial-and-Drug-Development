package com.genc.ctds.trialprotocol.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "clinical_sites")
public class ClinicalSite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long siteId;

    @Column(nullable = false, unique = true, length = 50)
    private String siteCode;

    @Column(nullable = false, length = 150)
    private String siteName;

    @Column(nullable = false, length = 150)
    private String location;

    @Column(nullable = false, length = 150)
    private String principalInvestigatorName;

    @Column(nullable = false)
    private boolean ethicsApproved;

    @Column(nullable = false)
    private boolean staffTrained;

    @Column(nullable = false)
    private boolean pharmacyReady;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SiteStatus siteStatus = SiteStatus.REGISTERED;

    private LocalDate activationDate;

    @ManyToOne
    @JoinColumn(name = "protocol_id")
    private TrialProtocol trialProtocol;

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPrincipalInvestigatorName() {
        return principalInvestigatorName;
    }

    public void setPrincipalInvestigatorName(String principalInvestigatorName) {
        this.principalInvestigatorName = principalInvestigatorName;
    }

    public boolean isEthicsApproved() {
        return ethicsApproved;
    }

    public void setEthicsApproved(boolean ethicsApproved) {
        this.ethicsApproved = ethicsApproved;
    }

    public boolean isStaffTrained() {
        return staffTrained;
    }

    public void setStaffTrained(boolean staffTrained) {
        this.staffTrained = staffTrained;
    }

    public boolean isPharmacyReady() {
        return pharmacyReady;
    }

    public void setPharmacyReady(boolean pharmacyReady) {
        this.pharmacyReady = pharmacyReady;
    }

    public SiteStatus getSiteStatus() {
        return siteStatus;
    }

    public void setSiteStatus(SiteStatus siteStatus) {
        this.siteStatus = siteStatus;
    }

    public LocalDate getActivationDate() {
        return activationDate;
    }

    public void setActivationDate(LocalDate activationDate) {
        this.activationDate = activationDate;
    }

    public TrialProtocol getTrialProtocol() {
        return trialProtocol;
    }

    public void setTrialProtocol(TrialProtocol trialProtocol) {
        this.trialProtocol = trialProtocol;
    }
}

