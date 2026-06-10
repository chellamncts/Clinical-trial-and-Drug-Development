package org.example.service;

import org.springframework.stereotype.Service;
import java.util.List;

import org.example.model.SampleLog;
import org.example.repository.SampleLogRepository;

@Service
public class UserService {

    private final SampleLogRepository repo;

    public UserService(SampleLogRepository repo) {
        this.repo = repo;
    }

    public void saveSampleLog(SampleLog sampleLog) {
        repo.save(sampleLog);
    }

    public List<SampleLog> getAllSampleLogs() {
        return repo.findAll();
    }
}