package com.genc.ctds.samplelog.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "investigational_product_inventory")
public class InvestigationalProductInventory {

    @Id
    private int inventoryId;

    private String productName;

    private String batchNumber;

    private int quantityReceived;

    private int quantityDispensed;

    private int quantityAvailable;

    private Double storageTemperatureC;

    @Enumerated(EnumType.STRING)
    private ColdChainStatus coldChainStatus;

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }

    public int getQuantityReceived() {
        return quantityReceived;
    }

    public void setQuantityReceived(int quantityReceived) {
        this.quantityReceived = quantityReceived;
    }

    public int getQuantityDispensed() {
        return quantityDispensed;
    }

    public void setQuantityDispensed(int quantityDispensed) {
        this.quantityDispensed = quantityDispensed;
    }

    public int getQuantityAvailable() {
        return quantityAvailable;
    }

    public void setQuantityAvailable(int quantityAvailable) {
        this.quantityAvailable = quantityAvailable;
    }

    public Double getStorageTemperatureC() {
        return storageTemperatureC;
    }

    public void setStorageTemperatureC(Double storageTemperatureC) {
        this.storageTemperatureC = storageTemperatureC;
    }

    public ColdChainStatus getColdChainStatus() {
        return coldChainStatus;
    }

    public void setColdChainStatus(ColdChainStatus coldChainStatus) {
        this.coldChainStatus = coldChainStatus;
    }

    public enum ColdChainStatus {
        OK,
        EXCURSION,
        UNKNOWN
    }

    public static class DispenseLog {
        private int dispenseId;
        private int inventoryId;
        private int subjectId;
        private int quantityDispensed;
        private String dispensedBy;
        private String dispensingLocation;
        private LocalDateTime dispensedAt;

        public int getDispenseId() {
            return dispenseId;
        }

        public void setDispenseId(int dispenseId) {
            this.dispenseId = dispenseId;
        }

        public int getInventoryId() {
            return inventoryId;
        }

        public void setInventoryId(int inventoryId) {
            this.inventoryId = inventoryId;
        }

        public int getSubjectId() {
            return subjectId;
        }

        public void setSubjectId(int subjectId) {
            this.subjectId = subjectId;
        }


        public int getQuantityDispensed() {
            return quantityDispensed;
        }

        public void setQuantityDispensed(int quantityDispensed) {
            this.quantityDispensed = quantityDispensed;
        }

        public String getDispensedBy() {
            return dispensedBy;
        }

        public void setDispensedBy(String dispensedBy) {
            this.dispensedBy = dispensedBy;
        }

        public String getDispensingLocation() {
            return dispensingLocation;
        }

        public void setDispensingLocation(String dispensingLocation) {
            this.dispensingLocation = dispensingLocation;
        }

        public LocalDateTime getDispensedAt() {
            return dispensedAt;
        }

        public void setDispensedAt(LocalDateTime dispensedAt) {
            this.dispensedAt = dispensedAt;
        }
    }
}


