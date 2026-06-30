package com.genc.ctds.adverseevent.controller;

import com.genc.ctds.adverseevent.model.AdverseEvent;
import com.genc.ctds.adverseevent.model.EventStatus;
import com.genc.ctds.adverseevent.model.Severity;
import com.genc.ctds.adverseevent.service.AdverseEventService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/events")
public class AdverseEventController {

    private final AdverseEventService service;

    public AdverseEventController(AdverseEventService service) {
        this.service = service;
    }

    @GetMapping("/pharma/dashboard")
    public String showDashboard(
            @RequestParam(name = "section", required = false, defaultValue = "dashboard") String section,
            @RequestParam(name = "message", required = false) String message,
            Model model) {

        List<AdverseEvent> allEvents = service.getAllEvents();
        Map<String, Long> metrics = service.calculateDashboardMetrics(allEvents);

        model.addAttribute("events", allEvents);
        model.addAttribute("totalAe", metrics.get("totalAe"));
        model.addAttribute("saeAlerts", metrics.get("saeAlerts"));
        model.addAttribute("underReview", metrics.get("underReview"));
        model.addAttribute("closed", metrics.get("closed"));
        model.addAttribute("activeSection", section);
        model.addAttribute("message", message);

        return "Dashboard";
    }

    @GetMapping("/form")
    public String showEventForm(
            @RequestParam(name = "message", required = false) String message,
            Model model) {
        model.addAttribute("event", new AdverseEvent());
        model.addAttribute("severities", Severity.values());
        model.addAttribute("eventStatuses", EventStatus.values());
        model.addAttribute("message", message);
        return "event-form";
    }

    @GetMapping("/list")
    public String showEventList(
            @RequestParam(name = "searchSubjectId", required = false) String searchSubjectId,
            @RequestParam(name = "searchSeverity", required = false) String searchSeverity,
            @RequestParam(name = "searchStatus", required = false) String searchStatus,
            @RequestParam(name = "message", required = false) String message,
            Model model) {

        List<AdverseEvent> filteredEvents = service.getAllEvents();

        if (searchSubjectId != null && !searchSubjectId.isBlank()) {
            filteredEvents = filteredEvents.stream()
                    .filter(e -> e.getSubjectId() != null
                            && e.getSubjectId().toLowerCase().contains(searchSubjectId.toLowerCase().trim()))
                    .toList();
        }
        if (searchSeverity != null && !searchSeverity.isBlank()) {
            filteredEvents = filteredEvents.stream()
                    .filter(e -> e.getSeverity() != null && e.getSeverity().name().equals(searchSeverity))
                    .toList();
        }
        if (searchStatus != null && !searchStatus.isBlank()) {
            filteredEvents = filteredEvents.stream()
                    .filter(e -> e.getEventStatus() != null && e.getEventStatus().name().equals(searchStatus))
                    .toList();
        }

        model.addAttribute("events", filteredEvents);
        model.addAttribute("severities", Severity.values());
        model.addAttribute("eventStatuses", EventStatus.values());
        model.addAttribute("searchSubjectId", searchSubjectId);
        model.addAttribute("searchSeverity", searchSeverity);
        model.addAttribute("searchStatus", searchStatus);
        model.addAttribute("message", message);

        return "event-list";
    }

    @GetMapping("/new")
    public String showForm() {
        return "redirect:/events/form";
    }

    @PostMapping("/save")
    public String saveEvent(@ModelAttribute("event") AdverseEvent event) {
        service.saveEvent(event);
        return "redirect:/events/list?message=Event%20submitted%20successfully";
    }

    @GetMapping("/success")
    public String successPage() {
        return "redirect:/events/list";
    }

    @GetMapping("/all")
    public String showAllEvents() {
        return "redirect:/events/list";
    }
}