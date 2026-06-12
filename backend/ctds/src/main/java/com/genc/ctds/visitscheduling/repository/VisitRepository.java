package com.genc.ctds.visitscheduling.repository;

import com.genc.ctds.visitscheduling.model.CrfStatus;
import com.genc.ctds.visitscheduling.model.VisitRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<VisitRecord, Long> {
    List<VisitRecord> findTop10ByOrderByVisitDateDesc();

    List<VisitRecord> findBySubjectIdOrderByVisitDateAsc(Long subjectId);

    int countByCrfStatus(CrfStatus crfStatus);
    List<VisitRecord> findTop10ByOrderByIdAsc();
}
