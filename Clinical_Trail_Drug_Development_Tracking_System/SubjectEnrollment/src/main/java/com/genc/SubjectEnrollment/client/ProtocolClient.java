package com.genc.SubjectEnrollment.client;

import com.genc.SubjectEnrollment.dto.ProtocolDTO;
import com.genc.SubjectEnrollment.dto.SiteDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Feign client for trialprotocol-service.
 * Eureka resolves "trialprotocol-service" → actual host:port automatically.
 */
@FeignClient(name = "trialprotocol-service")
public interface ProtocolClient {

    /** Fetch protocol details — used to check status is ACTIVE */
    @GetMapping("/api/protocols/{id}")
    ProtocolDTO getProtocol(@PathVariable("id") Long id);

    /** Fetch site details — used to check site belongs to protocol and is ACTIVE */
    @GetMapping("/api/protocols/sites/{siteId}")
    SiteDTO getSite(@PathVariable("siteId") Long siteId);
}

