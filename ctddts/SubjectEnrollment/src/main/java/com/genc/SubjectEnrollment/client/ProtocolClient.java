package com.genc.SubjectEnrollment.client;

import com.genc.SubjectEnrollment.dto.ProtocolDTO;
import com.genc.SubjectEnrollment.dto.SiteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "trialprotocol-service")
public interface ProtocolClient {

    @GetMapping("/api/protocols/{id}")
    ProtocolDTO getProtocol(@PathVariable("id") Long id);

    @GetMapping("/api/protocols/sites/{siteId}")
    SiteDTO getSite(@PathVariable("siteId") Long siteId);
}

