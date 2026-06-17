package com.genc.ctds.trialprotocol.service;

import com.genc.ctds.trialprotocol.model.ClinicalSite;
import com.genc.ctds.trialprotocol.model.ProtocolStatus;
import com.genc.ctds.trialprotocol.model.SiteRegistrationForm;
import com.genc.ctds.trialprotocol.model.SiteStatus;
import com.genc.ctds.trialprotocol.model.TrialProtocol;
import com.genc.ctds.trialprotocol.repository.ClinicalSiteRepository;
import com.genc.ctds.trialprotocol.repository.TrialProtocolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TrialProtocolService {
    @Autowired
    private TrialProtocolRepository trialProtocolRepository;

    @Autowired
    private ClinicalSiteRepository clinicalSiteRepository;

    @Transactional
    public TrialProtocol saveProtocol(TrialProtocol trialProtocol) {
        if (trialProtocol.getVersionNumber() == null || trialProtocol.getVersionNumber() < 1) {
            trialProtocol.setVersionNumber(1);
        }
        if (trialProtocol.getProtocolStatus() == null) {
            trialProtocol.setProtocolStatus(ProtocolStatus.DRAFT);
        }
        return trialProtocolRepository.save(trialProtocol);
    }

    public List<TrialProtocol> getAllProtocols() {
        return trialProtocolRepository.findAllByOrderByStartDateDesc();
    }

    public TrialProtocol getProtocolDetails(Long protocolId) {
        return trialProtocolRepository.findById(protocolId)
                .orElseThrow(() -> new IllegalArgumentException("Protocol not found for id: " + protocolId));
    }

    public List<ClinicalSite> getAllSites() {
        return clinicalSiteRepository.findAll();
    }

    public List<ClinicalSite> getSitesForProtocol(Long protocolId) {
        return clinicalSiteRepository.findByTrialProtocolProtocolId(protocolId);
    }

    @Transactional
    public ClinicalSite registerSite(SiteRegistrationForm form) {
        TrialProtocol protocol = getProtocolDetails(form.getProtocolId());

        ClinicalSite site = new ClinicalSite();
        site.setSiteCode(form.getSiteCode());
        site.setSiteName(form.getSiteName());
        site.setLocation(form.getLocation());
        site.setPrincipalInvestigatorName(form.getPrincipalInvestigatorName());
        site.setEthicsApproved(form.isEthicsApproved());
        site.setStaffTrained(form.isStaffTrained());
        site.setPharmacyReady(form.isPharmacyReady());
        site.setTrialProtocol(protocol);
        site.setSiteStatus(isSiteReady(form) ? SiteStatus.READY_FOR_ACTIVATION : SiteStatus.REGISTERED);

        return clinicalSiteRepository.save(site);
    }

    @Transactional
    public ClinicalSite activateSite(Long siteId) {
        ClinicalSite site = clinicalSiteRepository.findById(siteId)
                .orElseThrow(() -> new IllegalArgumentException("Site not found for id: " + siteId));

        if (!isSiteReady(site)) {
            site.setSiteStatus(SiteStatus.ON_HOLD);
            return clinicalSiteRepository.save(site);
        }

        site.setSiteStatus(SiteStatus.ACTIVE);
        site.setActivationDate(LocalDate.now());

        TrialProtocol protocol = site.getTrialProtocol();
        if (protocol.getProtocolStatus() == ProtocolStatus.APPROVED) {
            protocol.setProtocolStatus(ProtocolStatus.ACTIVE);
            trialProtocolRepository.save(protocol);
        }

        return clinicalSiteRepository.save(site);
    }

    public boolean isSiteReady(Long siteId) {
        ClinicalSite site = clinicalSiteRepository.findById(siteId)
                .orElseThrow(() -> new IllegalArgumentException("Site not found for id: " + siteId));
        return isSiteReady(site);
    }

    private boolean isSiteReady(SiteRegistrationForm form) {
        return form.isEthicsApproved() && form.isStaffTrained() && form.isPharmacyReady();
    }

    private boolean isSiteReady(ClinicalSite site) {
        return site.isEthicsApproved() && site.isStaffTrained() && site.isPharmacyReady();
    }
}
