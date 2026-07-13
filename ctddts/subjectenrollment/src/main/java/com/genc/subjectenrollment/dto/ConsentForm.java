package com.genc.subjectenrollment.dto;

import java.time.LocalDate;

public class ConsentForm {

    private String consentVersion;
    private LocalDate consentDate;
    private String consentedBy;
    private String notes;

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public ConsentForm() {}

    public String getConsentVersion() { return consentVersion; }
    public void setConsentVersion(String consentVersion) { this.consentVersion = consentVersion; }

    public LocalDate getConsentDate() { return consentDate; }
    public void setConsentDate(LocalDate consentDate) { this.consentDate = consentDate; }

    public String getConsentedBy() { return consentedBy; }
    public void setConsentedBy(String consentedBy) { this.consentedBy = consentedBy; }
}
