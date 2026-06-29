package com.genc.ctds.visitscheduling.repository;

import com.genc.ctds.visitscheduling.model.QueryRecord;
import com.genc.ctds.visitscheduling.model.QueryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QueryRepository extends JpaRepository<QueryRecord, Integer> {

    List<QueryRecord> findAllByOrderByRaisedAtDesc();

    List<QueryRecord> findByStatusOrderByRaisedAtDesc(QueryStatus status);

    int countByStatus(QueryStatus status);
}

