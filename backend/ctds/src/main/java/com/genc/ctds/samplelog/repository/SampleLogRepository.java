package com.genc.ctds.samplelog.repository;

//import org.example.model.SampleLog;
import com.genc.ctds.samplelog.model.SampleLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleLogRepository extends JpaRepository<SampleLog, Integer> {
}
