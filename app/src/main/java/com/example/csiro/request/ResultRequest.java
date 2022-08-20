package com.example.csiro.request;


import com.example.csiro.entity.ResponseObject;
import com.example.csiro.entity.Result;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ResultRequest {

    @POST("/result/uploadPic")
    Call<ResponseObject> uploadPic(@Part MultipartBody.Part file);

    @GET("/result/getResult")
    Call<ResponseObject<Result>> getResult(@Query("resultId")String id);
}