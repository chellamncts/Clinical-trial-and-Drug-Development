package com.genc.trialprotocol.controller;

import com.genc.trialprotocol.model.ProtocolStatus;
import com.genc.trialprotocol.model.Site;
import com.genc.trialprotocol.model.TrialProtocol;
import com.genc.trialprotocol.service.ProtocolService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/protocols")
public class ProtocolController {
    private final ProtocolService service;
    public ProtocolController(ProtocolService service) { this.service = service; }

    @PostMapping
    public TrialProtocol createProtocol(@Valid @RequestBody TrialProtocol p) { return service.createProtocol(p); }

    @GetMapping
    public List<TrialProtocol> all() { return service.all(); }

    @GetMapping("/{id}")
    public TrialProtocol getProtocolDetails(@PathVariable Long id) { return service.getProtocolDetails(id); }

    @PutMapping("/{id}/status")
    public TrialProtocol updateStatus(@PathVariable Long id, @RequestParam ProtocolStatus status) { // id = 3 status = approve
        return service.updateStatus(id, status); //updateStatus(3,approve)
    }

    @PostMapping("/sites")
    public Site registerSite(@RequestBody Site site) { return service.registerSite(site); }

    @PutMapping("/sites/{siteId}/activate")
    public Site activateSite(@PathVariable Long siteId) { return service.activateSite(siteId); }

    @GetMapping("/sites")
    public List<Site> allSites() { return service.allSites(); }

    @GetMapping("/sites/{siteId}")
    public Site getSiteById(@PathVariable Long siteId) { return service.getSiteById(siteId); }
}