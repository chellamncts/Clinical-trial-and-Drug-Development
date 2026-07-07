package com.genc.ctds.samplelog.config;

package com.genc.ctds.samplelog.config;
import com.genc.ctds.samplelog.model.InvestigationalProductInventory;
import com.genc.ctds.samplelog.repository.InvestigationalProductInventoryRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class InventoryDataInitializer {

    private final InvestigationalProductInventoryRepository inventoryRepository;

    public InventoryDataInitializer(InvestigationalProductInventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    @PostConstruct
    public void seedInventory() {
        if (inventoryRepository.count() > 0) {
            return;
        }

        InvestigationalProductInventory drug1 = new InvestigationalProductInventory();
        drug1.setInventoryId(1);
        drug1.setProductName("IP-101");
        drug1.setBatchNumber("BATCH-CT-001");
        drug1.setQuantityReceived(100);
        drug1.setQuantityDispensed(0);
        drug1.setQuantityAvailable(100);
        drug1.setStorageTemperatureC(5.0);
        drug1.setColdChainStatus(InvestigationalProductInventory.ColdChainStatus.OK);
        inventoryRepository.save(drug1);

        InvestigationalProductInventory drug2 = new InvestigationalProductInventory();
        drug2.setInventoryId(2);
        drug2.setProductName("IP-102");
        drug2.setBatchNumber("BATCH-CT-002");
        drug2.setQuantityReceived(80);
        drug2.setQuantityDispensed(0);
        drug2.setQuantityAvailable(80);
        drug2.setStorageTemperatureC(2.0);
        drug2.setColdChainStatus(InvestigationalProductInventory.ColdChainStatus.OK);
        inventoryRepository.save(drug2);

        InvestigationalProductInventory drug3 = new InvestigationalProductInventory();
        drug3.setInventoryId(3);
        drug3.setProductName("IP-103");
        drug3.setBatchNumber("BATCH-CT-003");
        drug3.setQuantityReceived(120);
        drug3.setQuantityDispensed(0);
        drug3.setQuantityAvailable(120);
        drug3.setStorageTemperatureC(20.0);
        drug3.setColdChainStatus(InvestigationalProductInventory.ColdChainStatus.OK);
        inventoryRepository.save(drug3);
    }
}
