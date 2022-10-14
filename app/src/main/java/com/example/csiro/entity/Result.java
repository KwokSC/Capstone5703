package com.example.csiro.entity;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;

public class Result implements Serializable {

    private String resultId;

    private Bitmap bitmap;

    private Date uploadDate;

    private String boxBrand;

    private Float reliability;

    private String description;

    public String getResultId() {
        return resultId;
    }

    public void setResultId(String resultId) {
        this.resultId = resultId;
    }

    public Bitmap getBitmap() {return bitmap;}

    public void setBitmap(Bitmap bitmap) {this.bitmap = bitmap;}

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

    public String getDescription() { return description;}

    public void setDescription(String description) {this.description = description;}

    @Override
    public String toString() {
        return "Result{" +
                "resultId='" + resultId + '\'' +
                ", bitmap=" + bitmap +
                ", uploadDate=" + uploadDate +
                ", boxBrand='" + boxBrand + '\'' +
                ", reliability=" + reliability +
                ", description='" + description + '\'' +
                '}';
    }
}