package com.genc.ctds.trialprotocol.controller;

import com.genc.ctds.trialprotocol.model.ClinicalSite;
import com.genc.ctds.trialprotocol.model.ClinicalSiteDTO;
import com.genc.ctds.trialprotocol.model.ProtocolStatus;
import com.genc.ctds.trialprotocol.model.TrialProtocol;
import com.genc.ctds.trialprotocol.service.ProtocolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Module 3.1 - Trial Protocol Design & Site Activation.
 * Single REST controller exposing protocol and site-activation actions.
 */
@RestController
@CrossOrigin(origins = "*")
public class ProtocolController {

    @Autowired
    private ProtocolService protocolService;

    // ---------- Protocols ----------

    // POST /api/protocols -> create a new protocol
    @PostMapping("/api/protocols")
    public ResponseEntity<TrialProtocol> createProtocol(@RequestBody TrialProtocol protocol) {
        TrialProtocol saved = protocolService.saveProtocol(protocol);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // GET /api/protocols -> list all protocols
    @GetMapping("/api/protocols")
    public List<TrialProtocol> getAllProtocols() {
        return protocolService.getAllProtocols();
    }

    // GET /api/protocols/{id} -> protocol details
    @GetMapping("/api/protocols/{id}")
    public ResponseEntity<TrialProtocol> getProtocolDetails(@PathVariable Long id) {
        return ResponseEntity.ok(protocolService.getProtocolDetails(id));
    }

    // GET /api/protocols/site-eligible -> only APPROVED/ACTIVE protocols (for the Sites tab)
    @GetMapping("/api/protocols/site-eligible")
    public List<TrialProtocol> getSiteEligibleProtocols() {
        return protocolService.getSiteEligibleProtocols();
    }

    // PUT /api/protocols/{id}/status?value=APPROVED|CLOSED -> change protocol status
    @PutMapping("/api/protocols/{id}/status")
    public ResponseEntity<TrialProtocol> updateProtocolStatus(@PathVariable Long id,
                                                              @RequestParam("value") ProtocolStatus value) {
        return ResponseEntity.ok(protocolService.updateProtocolStatus(id, value));
    }

    // ---------- Sites ----------

    // POST /api/sites -> register a clinical site
    @PostMapping("/api/sites")
    public ResponseEntity<ClinicalSite> registerSite(@RequestBody ClinicalSiteDTO siteDTO) {
        ClinicalSite saved = protocolService.registerSite(siteDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // GET /api/sites -> list all sites
    @GetMapping("/api/sites")
    public List<ClinicalSite> getAllSites() {
        return protocolService.getAllSites();
    }

    // POST /api/sites/{id}/activate -> activate a site
    @PostMapping("/api/sites/{id}/activate")
    public ResponseEntity<ClinicalSite> activateSite(@PathVariable Long id) {
        ClinicalSite activated = protocolService.activateSite(id);
        return ResponseEntity.ok(activated);
    }
}

