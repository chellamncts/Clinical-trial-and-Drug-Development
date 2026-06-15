package com.genc.ctds.subjectenrollment.controller;
import com.genc.ctds.subjectenrollment.model.TrialSubject;
import com.genc.ctds.subjectenrollment.service.SubjectService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@Controller
public class SubjectController {
    private SubjectService service;
    public SubjectController(SubjectService service) {
        this.service = service;
    }
    @GetMapping("/enrollment")
    public String loadForm(Model model){
        model.addAttribute("subject", new TrialSubject());
        return "enrollment";
    }
    @PostMapping("/enroll")
    public String enroll(Model model, @ModelAttribute TrialSubject subject, @RequestParam("action") String action){
        switch(action) {
            case "enroll":
                subject.setSubjectStatus("ENROLLED");
                break;
            case "screen":
                subject.setSubjectStatus("SCREENED");
                break;
            case "withdraw":
                subject.setSubjectStatus("WITHDRAWN");
                break;
            default:
                subject.setSubjectStatus("SCREENED");
                break;
        }
        service.saveSubject(subject);
        model.addAttribute("data", subject);
        return "enrollmentStatus";
    }

}
