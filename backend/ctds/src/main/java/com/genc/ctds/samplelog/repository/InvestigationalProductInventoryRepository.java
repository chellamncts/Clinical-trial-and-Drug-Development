package com.genc.ctds.samplelog.repository;

import com.genc.ctds.samplelog.model.InvestigationalProductInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvestigationalProductInventoryRepository extends JpaRepository<InvestigationalProductInventory, Integer> {

    List<InvestigationalProductInventory> findAllByOrderByInventoryIdAsc();
}

