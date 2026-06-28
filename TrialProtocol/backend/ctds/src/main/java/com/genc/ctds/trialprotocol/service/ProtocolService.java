package com.genc.ctds.trialprotocol.service;

import com.genc.ctds.trialprotocol.model.ClinicalSite;
import com.genc.ctds.trialprotocol.model.ClinicalSiteDTO;
import com.genc.ctds.trialprotocol.model.ProtocolStatus;
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
public class ProtocolService {
    @Autowired
    private TrialProtocolRepository trialProtocolRepository;

    @Autowired
    private ClinicalSiteRepository clinicalSiteRepository;

    @Transactional
    public TrialProtocol saveProtocol(TrialProtocol trialProtocol) {
        // Status is backend-managed: every new protocol starts in DRAFT.
        // Whatever status the client sends is ignored to keep the lifecycle valid.
        trialProtocol.setProtocolId(null);
        trialProtocol.setProtocolStatus(ProtocolStatus.DRAFT);
        if (trialProtocol.getVersionNumber() == null || trialProtocol.getVersionNumber() < 1) {
            trialProtocol.setVersionNumber(1);
        }
        return trialProtocolRepository.save(trialProtocol);
    }

    public List<TrialProtocol> getAllProtocols() {
        return trialProtocolRepository.findAllByOrderByProtocolId();
    }

    public TrialProtocol getProtocolDetails(Long protocolId) {
        return trialProtocolRepository.findById(protocolId)
                .orElseThrow(() -> new IllegalArgumentException("Protocol not found for id: " + protocolId));
    }

    /** Protocols that are eligible for site registration/activation (APPROVED or ACTIVE). */
    public List<TrialProtocol> getSiteEligibleProtocols() {
        return getAllProtocols().stream()
                .filter(p -> p.getProtocolStatus().allowsSiteWork())
                .toList();
    }

    /** Moves a protocol to a new status, enforcing the valid lifecycle transitions. */
    @Transactional
    public TrialProtocol updateProtocolStatus(Long protocolId, ProtocolStatus target) {
        TrialProtocol protocol = getProtocolDetails(protocolId);
        ProtocolStatus current = protocol.getProtocolStatus();

        if (target == ProtocolStatus.ACTIVE) {
            throw new IllegalStateException(
                    "A protocol becomes ACTIVE automatically when one of its sites is activated.");
        }
        if (!current.canTransitionTo(target)) {
            throw new IllegalStateException(
                    "Invalid status change: " + current + " -> " + target);
        }

        protocol.setProtocolStatus(target);
        return trialProtocolRepository.save(protocol);
    }

    public List<ClinicalSite> getAllSites() {
        return clinicalSiteRepository.findAll();
    }


    @Transactional
    public ClinicalSite registerSite(ClinicalSiteDTO form) {
        TrialProtocol protocol = getProtocolDetails(form.getProtocolId());

        // Sites can only be registered for APPROVED or ACTIVE protocols.
        if (!protocol.getProtocolStatus().allowsSiteWork()) {
            throw new IllegalStateException(
                    "Sites can only be registered for APPROVED or ACTIVE protocols. Current status: "
                            + protocol.getProtocolStatus());
        }

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

        TrialProtocol protocol = site.getTrialProtocol();

        // A site can only be activated while its protocol allows site work.
        if (!protocol.getProtocolStatus().allowsSiteWork()) {
            throw new IllegalStateException(
                    "Cannot activate a site because its protocol is " + protocol.getProtocolStatus()
                            + ". Approve the protocol first.");
        }

        // Not all readiness criteria met -> put the site on hold instead of activating.
        if (!isSiteReady(site)) {
            site.setSiteStatus(SiteStatus.ON_HOLD);
            return clinicalSiteRepository.save(site);
        }

        site.setSiteStatus(SiteStatus.ACTIVE);
        site.setActivationDate(LocalDate.now());

        // First activated site promotes the protocol from APPROVED to ACTIVE.
        if (protocol.getProtocolStatus() == ProtocolStatus.APPROVED) {
            protocol.setProtocolStatus(ProtocolStatus.ACTIVE);
            trialProtocolRepository.save(protocol);
        }

        return clinicalSiteRepository.save(site);
    }


    private boolean isSiteReady(ClinicalSiteDTO form) {
        return form.isEthicsApproved() && form.isStaffTrained() && form.isPharmacyReady();
    }

    private boolean isSiteReady(ClinicalSite site) {
        return site.isEthicsApproved() && site.isStaffTrained() && site.isPharmacyReady();
    }
}

