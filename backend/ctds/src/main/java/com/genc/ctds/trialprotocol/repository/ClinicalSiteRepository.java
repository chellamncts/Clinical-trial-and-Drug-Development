package com.genc.ctds.trialprotocol.repository;

import com.genc.ctds.trialprotocol.model.ClinicalSite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClinicalSiteRepository extends JpaRepository<ClinicalSite, Long> {
    List<ClinicalSite> findByTrialProtocolProtocolId(Long protocolId);
}

