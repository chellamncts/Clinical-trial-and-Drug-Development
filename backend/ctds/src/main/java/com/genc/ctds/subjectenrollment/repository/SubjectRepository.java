package com.genc.ctds.subjectenrollment.repository;

import com.genc.ctds.subjectenrollment.model.TrialSubject;
import com.genc.ctds.subjectenrollment.model.SubjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<TrialSubject, Integer> {
    List<TrialSubject> findBySubjectStatus(SubjectStatus subjectStatus);

    long countBySubjectStatus(SubjectStatus subjectStatus);
}