package com.genc.visit_scheduling.client;

import com.genc.visit_scheduling.dto.SubjectDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;

@FeignClient(name = "SubjectEnrollment")
public interface SubjectClient {

    @GetMapping("/api/subjects/{id}")
    SubjectDTO getSubject(@PathVariable("id") Long id);

    @GetMapping("/api/subjects")
    List<SubjectDTO> getAllSubjects();
}

