package com.genc.trialprotocol.service;

import com.genc.trialprotocol.exception.BusinessRuleException;
import com.genc.trialprotocol.exception.ResourceNotFoundException;
import com.genc.trialprotocol.model.ProtocolStatus;
import com.genc.trialprotocol.model.Site;
import com.genc.trialprotocol.model.SiteStatus;
import com.genc.trialprotocol.model.TrialProtocol;
import com.genc.trialprotocol.repository.SiteRepository;
import com.genc.trialprotocol.repository.TrialProtocolRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProtocolService {
    private final TrialProtocolRepository repo;
    private final SiteRepository siteRepo;

    public ProtocolService(TrialProtocolRepository repo, SiteRepository siteRepo) {
        this.repo = repo;
        this.siteRepo = siteRepo;
    }

    public TrialProtocol createProtocol(TrialProtocol p) {
        if (p.getProtocolStatus() == null) p.setProtocolStatus(ProtocolStatus.DRAFT);
        return repo.save(p);
    }

    public List<TrialProtocol> all() { return repo.findAll(); }

    public TrialProtocol getProtocolDetails(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Protocol not found: " + id));
    }

    public Site registerSite(Site site) {
        TrialProtocol p = getProtocolDetails(site.getProtocolId());
        if (!(p.getProtocolStatus() == ProtocolStatus.APPROVED
                || p.getProtocolStatus() == ProtocolStatus.ACTIVE))
            throw new BusinessRuleException("Sites can only be registered to APPROVED or ACTIVE protocols");
        site.setSiteStatus(SiteStatus.REGISTERED);
        return siteRepo.save(site);
    }

    public Site activateSite(Long siteId) {
        Site s = siteRepo.findById(siteId).orElseThrow(() -> new ResourceNotFoundException("Site not found: " + siteId));
        TrialProtocol p = getProtocolDetails(s.getProtocolId());
        if (!(p.getProtocolStatus() == ProtocolStatus.APPROVED
                || p.getProtocolStatus() == ProtocolStatus.ACTIVE))
            throw new BusinessRuleException("Protocol must be APPROVED or ACTIVE before site activation");
        s.setSiteStatus(SiteStatus.ACTIVE);
        return siteRepo.save(s);
    }

    public List<Site> allSites() { return siteRepo.findAll(); }

    public Site getSiteById(Long siteId) {
        return siteRepo.findById(siteId)
                .orElseThrow(() -> new ResourceNotFoundException("Site not found: " + siteId));
    }

    public TrialProtocol updateStatus(Long id, ProtocolStatus target) {
        TrialProtocol p = getProtocolDetails(id);
        ProtocolStatus current = p.getProtocolStatus();
        if (current == null) current = ProtocolStatus.DRAFT;

        if (current == target)
            throw new BusinessRuleException("Protocol is already " + target);

        boolean allowed = switch (target) {
            case APPROVED -> current == ProtocolStatus.DRAFT;
            case ACTIVE   -> current == ProtocolStatus.APPROVED;
            case CLOSED   -> current == ProtocolStatus.APPROVED
                          || current == ProtocolStatus.ACTIVE;
            case DRAFT    -> false; // cannot revert to DRAFT
        };
        if (!allowed)
            throw new BusinessRuleException("Invalid status transition: " + current + " \u2192 " + target);

        p.setProtocolStatus(target);
        return repo.save(p);
    }