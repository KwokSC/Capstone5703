package com.example.csiro.entity;

import java.util.Date;

public class Result {

    private String resultId;

    private Date uploadDate;

    private String boxBrand;

    private Boolean isPositive;

    private double reliability;

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public Date getUploadDate() {
        return uploadDate;
    }

    public void setUploadDate(Date uploadDate) {
        this.uploadDate = uploadDate;
    }

    public String getBoxBrand() { return boxBrand; }

    public void setBoxBrand(String boxBrand) { this.boxBrand = boxBrand; }

    public Boolean getPositive() {
        return isPositive;
    }

    public void setPositive(Boolean positive) {
        isPositive = positive;
    }

    public double getReliability() {
        return reliability;
    }

    public void setReliability(double reliability) {
        this.reliability = reliability;
    }

}