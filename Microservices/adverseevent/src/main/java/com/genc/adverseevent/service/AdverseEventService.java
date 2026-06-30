package com.genc.adverseevent.service;

import com.genc.adverseevent.model.AdverseEvent;
import com.genc.adverseevent.model.EventStatus;
import com.genc.adverseevent.model.Severity;
import com.genc.adverseevent.repository.AdverseEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdverseEventService {

    @Autowired
    private AdverseEventRepository repository;

    public AdverseEvent saveEvent(AdverseEvent event) {
        AdverseEvent savedEvent = repository.save(event);
        classifySeriousness(savedEvent);
        return savedEvent;
    }

    public List<AdverseEvent> getAllEvents() {
        return repository.findAll();
    }

    public Map<String, Long> calculateDashboardMetrics(List<AdverseEvent> allEvents) {
        List<AdverseEvent> safeEvents = allEvents == null ? List.of() : allEvents;
        Map<String, Long> metrics = new HashMap<>();
        metrics.put("totalAe", (long) safeEvents.size());
        metrics.put("saeAlerts", safeEvents.stream()
                .filter(e -> e.getSeverity() == Severity.SEVERE)
                .count());
        metrics.put("underReview", safeEvents.stream()
                .filter(e -> e.getEventStatus() == EventStatus.UNDER_REVIEW)
                .count());
        metrics.put("closed", safeEvents.stream()
                .filter(e -> e.getEventStatus() == EventStatus.RESOLVED)
                .count());
        return metrics;
    }

    public void classifySeriousness(AdverseEvent event) {
        // Fixed: Added a null-safe check to prevent NullPointerException if severity is left empty
        if (event != null && event.getSeverity() == Severity.SEVERE) {
            submitSafetyReport(event);
        }
    }

    private void submitSafetyReport(AdverseEvent event) {
        System.out.println("ALERT: Serious Adverse Event workflow triggered automatically for Event ID: " + event.getEventId());
    }
}