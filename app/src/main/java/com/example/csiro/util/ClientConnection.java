package com.example.csiro.util;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientConnection extends AppCompatActivity {

    public static OkHttpClient client;
    public static Retrofit retrofit;

    public static void connectionBuild(String url){

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(message -> Log.i("TAG", message)).setLevel(HttpLoggingInterceptor.Level.BODY);

        client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

}