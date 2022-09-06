package com.example.csiro.entity;

import java.io.Serializable;
import java.util.Date;

public class Result implements Serializable {

    private String resultId;

    private Date uploadDate;

    private String boxBrand;

    private Float reliability;

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

    public double getReliability() {
        return reliability;
    }

    public void setReliability(Float reliability) {
        this.reliability = reliability;
    }

}