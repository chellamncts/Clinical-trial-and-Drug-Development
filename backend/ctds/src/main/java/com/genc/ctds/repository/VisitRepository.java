package com.genc.ctds.repository;

import com.genc.ctds.model.VisitRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VisitRepository extends JpaRepository<VisitRecord, Long> {
    List<VisitRecord> findBySubjectId(Long subjectId);

}
