package com.genc.adverseevent.repository;

import com.genc.adverseevent.model.AdverseEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AdverseEventRepository extends JpaRepository<AdverseEvent, Long> {
    List<AdverseEvent> findBySubjectId(Long subjectId);
    List<AdverseEvent> findByVisitId(Long visitId);
}