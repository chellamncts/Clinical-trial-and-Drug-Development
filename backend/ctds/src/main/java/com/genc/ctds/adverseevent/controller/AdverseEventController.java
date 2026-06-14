package com.genc.ctds.adverseevent.controller;
import com.genc.ctds.adverseevent.model.AdverseEvent;
import com.genc.ctds.adverseevent.model.Severity;
import com.genc.ctds.adverseevent.service.AdverseEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@Controller
@RequestMapping("/events")
public class AdverseEventController {
        @Autowired
        private AdverseEventService service;


        @GetMapping("/new")
        public String showForm(Model model) {
            model.addAttribute("event", new AdverseEvent());
            model.addAttribute("severityOptions", Severity.values());
            return "event-form";
        }
        @PostMapping("/save")
        public String saveEvent(@ModelAttribute AdverseEvent event, RedirectAttributes redirectAttributes) {
            System.out.println("Saving event...");
            service.saveEvent(event);
            redirectAttributes.addFlashAttribute("message", "Event submitted successfully.");
            return "redirect:/events/all";
        }
        @GetMapping("/success")
        public String successPage() {
            return "success";
        }
        @GetMapping("/all")
        public String showAllEvents(Model model) {
            model.addAttribute("events", service.getAllEvents());
            return "event-list";
        }
    }