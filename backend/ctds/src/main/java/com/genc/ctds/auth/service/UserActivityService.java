package com.genc.ctds.auth.service;

import com.genc.ctds.auth.model.User;
import com.genc.ctds.auth.model.UserActivity;
import com.genc.ctds.auth.repository.UserActivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserActivityService {

    private final UserActivityRepository userActivityRepository;

    public UserActivityService(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    public void logUserCreated(User user, String actor) {
        UserActivity activity = new UserActivity();
        activity.setActionType("USER_CREATED");
        activity.setDetails(user.getUsername() + " added as " + formatRole(user.getRole().name()));
        activity.setPerformedBy(resolveActor(actor));
        userActivityRepository.save(activity);
    }

    public List<UserActivity> getRecentActivities() {
        return userActivityRepository.findTop8ByOrderByCreatedAtDesc();
    }

    private String resolveActor(String actor) {
        if (actor == null || actor.isBlank()) {
            return "SYSTEM";
        }
        return actor.trim();
    }

    private String formatRole(String roleName) {
        return switch (roleName) {
            case "ADMIN" -> "Admin";
            case "CLINICAL_RESEARCH_COORDINATOR" -> "CRC";
            case "PRINCIPAL_INVESTIGATOR" -> "PI";
            case "DATA_MANAGER" -> "Data Manager";
            case "PHARMACOVIGILANCE_OFFICER" -> "PV";
            default -> roleName.replace('_', ' ');
        };
    }
}

