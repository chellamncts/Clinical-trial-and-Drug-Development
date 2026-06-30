package com.genc.Lab_Sample_and_IP_Tracking.samplelog.repository;

import com.genc.Lab_Sample_and_IP_Tracking.samplelog.model.InvestigationalProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvestigationalProductInventoryRepository extends JpaRepository<InvestigationalProductInventory, Integer> {

    List<InvestigationalProductInventory> findAllByOrderByInventoryIdAsc();
}


