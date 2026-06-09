package com.example.clinical_trial.repository;

import com.example.clinical_trial.model.VisitRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<VisitRecord, Long> {
    List<VisitRecord> findBySubjectId(Long subjectId);
}
