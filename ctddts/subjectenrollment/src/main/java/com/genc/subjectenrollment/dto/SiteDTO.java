package com.genc.subjectenrollment.dto;

public class SiteDTO {

    private Long siteId;
    private Long protocolId;
    private String siteName;
    private String location;
    private String principalInvestigator;
    private String siteStatus;

    public SiteDTO() {}

    public Long getSiteId() { return siteId; }
    public void setSiteId(Long siteId) { this.siteId = siteId; }

    public Long getProtocolId() { return protocolId; }
    public void setProtocolId(Long protocolId) { this.protocolId = protocolId; }

    public String getSiteName() { return siteName; }
    public void setSiteName(String siteName) { this.siteName = siteName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPrincipalInvestigator() { return principalInvestigator; }
    public void setPrincipalInvestigator(String principalInvestigator) { this.principalInvestigator = principalInvestigator; }

    public String getSiteStatus() { return siteStatus; }
    public void setSiteStatus(String siteStatus) { this.siteStatus = siteStatus; }
}

