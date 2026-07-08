package com.genc.subjectenrollment.repository;

import com.genc.subjectenrollment.model.SubjectStatus;
import com.genc.subjectenrollment.model.TrialSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrialSubjectRepository extends JpaRepository<TrialSubject, Integer> {

    List<TrialSubject> findByProtocolId(Integer protocolId);

    List<TrialSubject> findBySubjectStatus(SubjectStatus subjectStatus);

    List<TrialSubject> findBySiteId(Integer siteId);
}

