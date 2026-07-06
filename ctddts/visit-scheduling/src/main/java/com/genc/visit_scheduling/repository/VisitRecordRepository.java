package com.genc.visit_scheduling.repository;

import com.genc.visit_scheduling.model.VisitRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VisitRecordRepository extends JpaRepository<VisitRecord, Long> {
    List<VisitRecord> findBySubjectId(Long subjectId);
}

