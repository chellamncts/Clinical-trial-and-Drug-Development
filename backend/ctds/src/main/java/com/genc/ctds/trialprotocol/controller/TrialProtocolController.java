package com.genc.ctds.trialprotocol.controller;
import com.genc.ctds.trialprotocol.model.Phase;
import com.genc.ctds.trialprotocol.model.ProtocolStatus;
import com.genc.ctds.trialprotocol.model.ClinicalSiteDTO;
import com.genc.ctds.trialprotocol.model.SiteStatus;
import com.genc.ctds.trialprotocol.model.TrialProtocol;
import com.genc.ctds.trialprotocol.service.TrialProtocolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class TrialProtocolController {
    @Autowired
    private TrialProtocolService trialProtocolService;

    @GetMapping("/createProtocol")
    public String createProtocol(Model model) {
        model.addAttribute("protocolObject", new TrialProtocol());
        model.addAttribute("phases", Phase.values());
        model.addAttribute("protocolStatuses", ProtocolStatus.values());
        List<TrialProtocol> protocols = trialProtocolService.getAllProtocols();
        model.addAttribute("protocols", protocols);
        return "protocolCreation";
    }

    @PostMapping("/postProtocol")
    public String saveProtocol(@ModelAttribute("protocolObject") TrialProtocol trialProtocol) {
        trialProtocolService.saveProtocol(trialProtocol);
        return "redirect:/createProtocol";
    }

    @GetMapping("/protocol/{protocolId}")
    public String getProtocolDetails(@PathVariable Long protocolId, Model model) {
        TrialProtocol protocol = trialProtocolService.getProtocolDetails(protocolId);
        model.addAttribute("protocol", protocol);
        model.addAttribute("sites", trialProtocolService.getSitesForProtocol(protocolId));
        return "protocol-details";
    }

    @GetMapping("/sites")
    public String viewSites(Model model) {
        model.addAttribute("clinicalSiteDTO", new ClinicalSiteDTO());
        model.addAttribute("protocols", trialProtocolService.getAllProtocols());
        model.addAttribute("sites", trialProtocolService.getAllSites());
        model.addAttribute("siteStatuses", SiteStatus.values());
        return "sites";
    }

    @PostMapping("/sites/register")
    public String registerSite(@ModelAttribute ClinicalSiteDTO clinicalSiteDTO) {
        trialProtocolService.registerSite(clinicalSiteDTO);
        return "redirect:/sites";
    }

    @PostMapping("/sites/{siteId}/activate")
    public String activateSite(@PathVariable Long siteId) {
        trialProtocolService.activateSite(siteId);
        return "redirect:/sites";
    }
}
