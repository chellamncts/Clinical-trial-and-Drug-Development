package com.genc.ctds.trialprotocol.service;

import com.genc.ctds.trialprotocol.model.TrialProtocol;
import com.genc.ctds.trialprotocol.repository.TrialProtocolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TrialProtocolService {
    @Autowired
    private TrialProtocolRepository trialProtocolRepository;
    public void saveProtocol(TrialProtocol trialProtocol) {
        trialProtocolRepository.save(trialProtocol);
    }
    public List<TrialProtocol> getAllProtocols() {
        return trialProtocolRepository.findAll();
    }
}
