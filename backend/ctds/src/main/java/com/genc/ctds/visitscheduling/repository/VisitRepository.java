package com.genc.ctds.visitscheduling.repository;

import com.genc.ctds.visitscheduling.model.CrfStatus;
import com.genc.ctds.visitscheduling.model.VisitRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<VisitRecord, Integer> {

    List<VisitRecord> findByTrialSubject_SubjectIdOrderByVisitDateAsc(Integer subjectId);

    int countByCrfStatus(CrfStatus crfStatus);

    @Query("SELECT v.visitDate, COUNT(v) FROM VisitRecord v GROUP BY v.visitDate ORDER BY v.visitDate")
    List<Object[]> countVisitsByDate();
}
