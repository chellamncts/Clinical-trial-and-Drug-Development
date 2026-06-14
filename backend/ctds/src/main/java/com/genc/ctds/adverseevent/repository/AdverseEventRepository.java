package com.genc.ctds.adverseevent.repository;

import com.genc.ctds.adverseevent.model.AdverseEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdverseEventRepository extends JpaRepository<AdverseEvent, Long> {
}
