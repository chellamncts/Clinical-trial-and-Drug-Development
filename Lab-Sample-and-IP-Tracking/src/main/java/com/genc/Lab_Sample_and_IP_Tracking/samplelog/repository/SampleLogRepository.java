package com.genc.Lab_Sample_and_IP_Tracking.samplelog.repository;

import com.genc.Lab_Sample_and_IP_Tracking.samplelog.model.SampleLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SampleLogRepository extends JpaRepository<SampleLog, Integer> {

	List<SampleLog> findBySubjectId(int subjectId);
}


