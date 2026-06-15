package com.genc.ctds.samplelog.controller;

import com.genc.ctds.samplelog.model.SampleLog;
import com.genc.ctds.samplelog.service.SampleLogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SampleLogController{

    private final SampleLogService service;

    public SampleLogController(SampleLogService service) {
        this.service = service;
    }
    //It gives the form an empty object to fill.
    @GetMapping("/UserForm")
    public String showForm(Model model) {
        model.addAttribute("user", new SampleLog());
        return "UserForm";
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute SampleLog user, Model model) {
        SampleLog saved = service.saveSampleLog(user);//This sends the form data to the service for saving.
        //Adds sample ID to model with name subId
        model.addAttribute("subId", saved.getSampleId());//To display the generated ID on result page.
        model.addAttribute("sample", saved);
        return "result";
    }


    /** View all samples. */
    @GetMapping("/sample/list")
    public String listSamples(Model model) {
        List<SampleLog> samples = service.getAllSampleLogs();
        model.addAttribute("samples", samples);
        model.addAttribute("totalCount", samples.size());
        return "sample-list";
    }



    /** View samples for a specific subject. */
    @GetMapping("/sample/subject/{subjectId}")
    public String viewBySubject(@PathVariable int subjectId, Model model) {
        List<SampleLog> samples = service.getSamplesBySubject(subjectId);
        model.addAttribute("samples", samples);
        model.addAttribute("totalCount", samples.size());
        return "sample-list";
    }

    /** Search only by subjectId and show all sample details for that subject. */
    @GetMapping("/sample/search")
    public String searchSample(@RequestParam(required = false) Integer subjectId,
                               Model model) {
        if (subjectId == null) {
            model.addAttribute("error", "Please enter a Subject ID to search");
            model.addAttribute("searched", false);
            return "sample-search";
        }

        List<SampleLog> samples = service.getSamplesBySubject(subjectId);
        if (!samples.isEmpty()) {
            model.addAttribute("samples", samples);
            model.addAttribute("subjectId", subjectId);
            model.addAttribute("success", "Found " + samples.size() + " sample(s) for Subject " + subjectId);
        } else {
            model.addAttribute("error", "No samples found for Subject ID " + subjectId);
        }
        model.addAttribute("searched", true);
        return "sample-search";
    }

    /** Display empty search form. */
    @GetMapping("/sample/search-form")
    public String showSearchForm(Model model) {
        model.addAttribute("searched", false);
        return "sample-search";
    }
}
