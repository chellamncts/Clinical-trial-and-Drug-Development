package com.genc.ctds.samplelog.dto;

public class DispenseRequest {
    private int subjectId;
    private int quantity;
    private String dispensedBy;
    private String dispensingLocation;

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getDispensedBy() { return dispensedBy; }
    public void setDispensedBy(String dispensedBy) { this.dispensedBy = dispensedBy; }

    public String getDispensingLocation() { return dispensingLocation; }
    public void setDispensingLocation(String dispensingLocation) { this.dispensingLocation = dispensingLocation; }
}

