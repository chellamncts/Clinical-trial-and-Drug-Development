package com.genc.ctds.trialprotocol.controller;

import com.genc.ctds.trialprotocol.model.Phase;
import com.genc.ctds.trialprotocol.model.ProtocolStatus;
import com.genc.ctds.trialprotocol.model.TrialProtocol;
import com.genc.ctds.trialprotocol.service.TrialProtocolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
        model.addAttribute("pro", ProtocolStatus.values());
        List<TrialProtocol> protocols = trialProtocolService.getAllProtocols();
        model.addAttribute("protocols", protocols);
        return "protocolCreation";
    }
    @PostMapping("/postProtocol")
    public String saveProtocol(@ModelAttribute TrialProtocol trialProtocol,Model model) {
        trialProtocolService.saveProtocol(trialProtocol);

        return "redirect:/createProtocol";
    }
    @GetMapping("/sites")
    public String viewSites(Model model) {
        return "sites";
    }
}
