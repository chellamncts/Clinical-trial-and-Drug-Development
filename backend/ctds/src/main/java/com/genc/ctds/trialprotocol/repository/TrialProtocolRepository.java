package com.genc.ctds.trialprotocol.repository;

import com.genc.ctds.trialprotocol.model.TrialProtocol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrialProtocolRepository extends JpaRepository<TrialProtocol, Long> {
	List<TrialProtocol> findAllByOrderByStartDateDesc();
}
