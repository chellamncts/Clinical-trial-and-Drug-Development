package com.genc.ctds.samplelog.service;

import com.genc.ctds.samplelog.model.SampleLog;
import com.genc.ctds.samplelog.repository.SampleLogRepository;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
public class SampleLogService {

    private final SampleLogRepository repo;

    public SampleLogService(SampleLogRepository repo) {
        this.repo = repo;
    }

    public SampleLog saveSampleLog(SampleLog sampleLog) {
        return repo.save(sampleLog);
    }

    public List<SampleLog> getAllSampleLogs() {
        return repo.findAll();
    }

    public List<SampleLog> getSamplesBySubject(int subjectId) {
        return repo.findAll().stream()
                .filter(sample -> sample.getSubjectId() == subjectId)
                .toList();
    }


}
