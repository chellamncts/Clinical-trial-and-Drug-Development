package org.example.repository;

import org.example.model.SampleLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleLogRepository extends JpaRepository<SampleLog, Integer> {
}
