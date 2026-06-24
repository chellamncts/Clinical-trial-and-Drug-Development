package com.genc.ctds.subjectenrollment.service;

import com.genc.ctds.subjectenrollment.model.TrialSubject;
import com.genc.ctds.subjectenrollment.model.SubjectStatus;
import com.genc.ctds.subjectenrollment.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository repository;
    public void saveSubject(TrialSubject subject) {
        repository.save(subject);
    }
    public List<TrialSubject> getAllSubjects() {
        return repository.findAll();
    }
    public long countEnrollment() {
        return repository.countBySubjectStatus(SubjectStatus.ENROLLED);
    }
    public long countScreened() {
        return repository.countBySubjectStatus(SubjectStatus.SCREENED);
    }
    public long countWithdrawn() {
        return repository.countBySubjectStatus("WITHDRAWN");
    }
}