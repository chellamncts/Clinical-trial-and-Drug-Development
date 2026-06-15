package com.genc.ctds.controller;

import com.genc.ctds.auth.model.RoleType;
import com.genc.ctds.auth.model.User;
import com.genc.ctds.auth.service.UserActivityService;
import com.genc.ctds.auth.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserActivityService userActivityService;
    @GetMapping({"/", "/home"})
    public String home() {
        return "home";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session) {
        Object role = session.getAttribute("role");
        if (role == null) {
            return "redirect:/login";
        }
        String username = (String) session.getAttribute("username");
        return switch (role.toString()) {
            case "ADMIN" -> "admin-dashboard";
            case "CLINICAL_RESEARCH_COORDINATOR" -> "crc-dashboard";
            case "PRINCIPAL_INVESTIGATOR" -> "pi-dashboard";
            case "DATA_MANAGER" -> "datamanager-dashboard";
            case "PHARMACOVIGILANCE_OFFICER" -> "pv-dashboard";
            default -> "redirect:/login";
        };
    }
    @GetMapping("/userManagement")
    public String userManagement(Model model) {
        model.addAttribute("users",new User());
        model.addAttribute("roles", RoleType.values());
        List<User> user1 = authService.getAllUsers();
        model.addAttribute("allUsers",user1);

        Map<RoleType, Long> roleDistribution = authService.getRoleDistribution();
        model.addAttribute("roleLabels", roleDistribution.keySet().stream().map(this::formatRoleLabel).toList());
        model.addAttribute("roleCounts", roleDistribution.values());
        model.addAttribute("recentActivities", userActivityService.getRecentActivities());

        return "userManagement.html";
    }
    @PostMapping("/userManagement")
    public String saveUser(@ModelAttribute User user, HttpSession session){
        String actor = (String) session.getAttribute("username");
        authService.saveUser(user, actor);
        return "redirect:/userManagement";
    }

    private String formatRoleLabel(RoleType roleType) {
        return switch (roleType) {
            case ADMIN -> "Admin";
            case CLINICAL_RESEARCH_COORDINATOR -> "CRC";
            case PRINCIPAL_INVESTIGATOR -> "PI";
            case DATA_MANAGER -> "Data Manager";
            case PHARMACOVIGILANCE_OFFICER -> "PV";
        };
    }
}
