
package com.genc.ctds.adverseevent.service;

import com.genc.ctds.adverseevent.model.AdverseEvent;
import com.genc.ctds.adverseevent.repository.AdverseEventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdverseEventService {

    @Autowired
    private AdverseEventRepository repository;

    public AdverseEvent saveEvent(AdverseEvent event) {
        return repository.save(event);
    }

    // ✅ NEW METHOD
    public List<AdverseEvent> getAllEvents() {
        return repository.findAll();
    }
}