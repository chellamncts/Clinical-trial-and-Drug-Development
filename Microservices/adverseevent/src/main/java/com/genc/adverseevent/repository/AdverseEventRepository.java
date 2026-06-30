package com.genc.adverseevent.repository;

import com.genc.adverseevent.model.AdverseEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdverseEventRepository extends JpaRepository<AdverseEvent, Long> {
}