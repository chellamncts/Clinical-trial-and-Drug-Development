package com.genc.ctds.controller;

import com.genc.ctds.auth.model.RoleType;
import com.genc.ctds.auth.model.User;
import com.genc.ctds.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class HomeController {
    @Autowired
    private AuthService authService;
    @GetMapping({"/", "/home"})
    public String home() {
        return "home";
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication, Model model) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }
        model.addAttribute("username", authentication.getName());

        if (hasRole(authentication, "ADMIN")) {
            return "admin-dashboard";
        }
        if (hasRole(authentication, "CLINICAL_RESEARCH_COORDINATOR")) {
            return "crc-dashboard";
        }
        if (hasRole(authentication, "PRINCIPAL_INVESTIGATOR")) {
            return "pi-dashboard";
        }
        if (hasRole(authentication, "DATA_MANAGER")) {
            return "datamanager-dashboard";
        }
        if (hasRole(authentication, "PHARMACOVIGILANCE_OFFICER")) {
            return "pv-dashboard";
        }

        return "redirect:/login";
    }
    @GetMapping("/userManagement")
    public String userManagement(Model model) {
        model.addAttribute("users",new User());
        model.addAttribute("roles", RoleType.values());
        List<User> user1 = authService.getAllUsers();
        model.addAttribute("allUsers",user1);
        return "userManagement";
    }
    @PostMapping("/userManagement")
    public String saveUser(@ModelAttribute User user, Authentication authentication){
        String actor = authentication == null ? null : authentication.getName();
        authService.saveUser(user,actor);
        return "redirect:/userManagement";
    }

    private boolean hasRole(Authentication authentication, String role) {
        String expectedAuthority = "ROLE_" + role;
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (expectedAuthority.equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
