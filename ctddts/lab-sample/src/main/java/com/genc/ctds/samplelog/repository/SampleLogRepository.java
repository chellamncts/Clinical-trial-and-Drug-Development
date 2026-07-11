package com.genc.ctds.samplelog.repository;

import com.genc.ctds.samplelog.model.SampleLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SampleLogRepository extends JpaRepository<SampleLog, Integer> {

	List<SampleLog> findBySubjectId(int subjectId);
}

