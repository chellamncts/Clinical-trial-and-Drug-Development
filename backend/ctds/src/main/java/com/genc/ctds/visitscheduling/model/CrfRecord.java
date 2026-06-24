package com.genc.ctds.visitscheduling.model;

import jakarta.persistence.*;

@Entity
public class CrfRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "visit_id", foreignKey = @ForeignKey(name = "fk_crf_visit"))
    private VisitRecord visit;

    private String bloodPressure;
    private Integer heartRate;
    private Double temperature;
    private Double weight;
    private Double height;
    private Double hemoglobin;
    private Integer wbcCount;
    private Double creatinine;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public VisitRecord getVisit() {
        return visit;
    }

    public void setVisit(VisitRecord visit) {
        this.visit = visit;
    }

    public String getBloodPressure() {
        return bloodPressure;
    }

    public void setBloodPressure(String bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public Integer getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Integer heartRate) {
        this.heartRate = heartRate;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {
        this.height = height;
    }

    public Double getHemoglobin() {
        return hemoglobin;
    }

    public void setHemoglobin(Double hemoglobin) {
        this.hemoglobin = hemoglobin;
    }

    public Integer getWbcCount() {
        return wbcCount;
    }

    public void setWbcCount(Integer wbcCount) {
        this.wbcCount = wbcCount;
    }

    public Double getCreatinine() {
        return creatinine;
    }

    public void setCreatinine(Double creatinine) {
        this.creatinine = creatinine;
    }
}
