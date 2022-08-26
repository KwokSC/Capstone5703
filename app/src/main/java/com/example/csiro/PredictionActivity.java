package com.example.csiro;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.csiro.util.TensorFlowPrediction;

import java.io.FileNotFoundException;

public class PredictionActivity extends AppCompatActivity {

    TensorFlowPrediction tensorFlowPrediction;
    private final static String MODEL_PATH = "file:///android_asset/tensor_model.h5";
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        predict();
        finish();
    }

    private void predict(){
        tensorFlowPrediction = new TensorFlowPrediction(getAssets(), MODEL_PATH);
        Uri imageUri = getIntent().getData();
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        int result = tensorFlowPrediction.predict(bitmap);
        Bundle bundle = new Bundle();

    }
}