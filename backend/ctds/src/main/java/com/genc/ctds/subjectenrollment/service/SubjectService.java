package com.genc.ctds.subjectenrollment.service;

import com.genc.ctds.subjectenrollment.model.TrialSubject;
import com.genc.ctds.subjectenrollment.model.SubjectStatus;
import com.genc.ctds.subjectenrollment.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository repository;

    // ✅ SCREEN
    public void screenSubject(TrialSubject subject) {
        subject.setSubjectStatus(SubjectStatus.SCREENED);
        repository.save(subject);
    }

    // ✅ ENROLL
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

    // ✅ WITHDRAW
    public void withdrawSubject(Integer subjectId) {
        Optional<TrialSubject> subject = repository.findById(subjectId);

        if (subject.isPresent()) {
            TrialSubject s = subject.get();
            s.setSubjectStatus(SubjectStatus.WITHDRAWN);
            repository.save(s);
        }
    }

    // ✅ GET ALL SUBJECTS
    public List<TrialSubject> getAllSubjects() {
        return repository.findAll();
    }

    // ✅ COMPLETE
    public void completeSubject(Integer subjectId) {
        Optional<TrialSubject> subject = repository.findById(subjectId);

        if (subject.isPresent()) {
            TrialSubject s = subject.get();
            s.setSubjectStatus(SubjectStatus.COMPLETED);
            repository.save(s);
        }
    }

    // ✅ GET SUBJECTS BY STATUS
    public List<TrialSubject> getSubjectsByStatus(SubjectStatus status) {
        return repository.findBySubjectStatus(status);
    }
}