package com.genc.ctds.trialprotocol.model;

public class ClinicalSiteDTO {

    private Long protocolId;
    private String siteCode;
    private String siteName;
    private String location;
    private String principalInvestigatorName;
    private boolean ethicsApproved;
    private boolean staffTrained;
    private boolean pharmacyReady;

    public Long getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(Long protocolId) {
        this.protocolId = protocolId;
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
}

