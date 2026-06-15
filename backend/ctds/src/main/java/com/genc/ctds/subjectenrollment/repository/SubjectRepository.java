package com.genc.ctds.subjectenrollment.repository;

import com.genc.ctds.subjectenrollment.model.TrialSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectRepository extends JpaRepository<TrialSubject,String> {
}
