package com.genc.ctds.visitscheduling.repository;

import com.genc.ctds.visitscheduling.model.CrfRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CrfRepository extends JpaRepository<CrfRecord, Integer> {
    Optional<CrfRecord> findByVisitId(Integer visitId);
}
