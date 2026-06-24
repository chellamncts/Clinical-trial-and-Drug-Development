package com.genc.ctds.subjectenrollment.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.genc.ctds.subjectenrollment.model.SubjectStatus;
import com.genc.ctds.subjectenrollment.model.TrialSubject;
import com.genc.ctds.subjectenrollment.repository.SubjectRepository;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository repository;

    public void screenSubject(TrialSubject subject) {
        subject.setSubjectStatus(SubjectStatus.SCREENED);
        repository.save(subject);
    }

    public boolean enrollSubject(Integer subjectId) {
        Optional<TrialSubject> subject = repository.findById(subjectId);
        if (subject.isPresent()) {
            TrialSubject s = subject.get();
            if (!s.isConsentProvided()) {
                return false;
            }
            s.setSubjectStatus(SubjectStatus.ENROLLED);
            repository.save(s);
            return true;
        }
        return false;
    }

    public List<TrialSubject> getAllSubjects() {
        return repository.findAll();
    }

    public void withdrawSubject(Integer subjectId) {
        Optional<TrialSubject> subject = repository.findById(subjectId);
        if (subject.isPresent()) {
            TrialSubject s = subject.get();
            s.setSubjectStatus(SubjectStatus.WITHDRAWN);
            repository.save(s);
        }
    }
    public long countEnrollment() {
        return repository.countBySubjectStatus(SubjectStatus.ENROLLED);
    }

    public long countScreened() {
        return repository.countBySubjectStatus(SubjectStatus.SCREENED);
    }

    public long countWithdrawn() {
        return repository.countBySubjectStatus(SubjectStatus.WITHDRAWN);
    }
}
