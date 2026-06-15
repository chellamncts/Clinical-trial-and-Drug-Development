package com.genc.ctds.auth.repository;

import com.genc.ctds.auth.model.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {
    List<UserActivity> findTop8ByOrderByCreatedAtDesc();
}

