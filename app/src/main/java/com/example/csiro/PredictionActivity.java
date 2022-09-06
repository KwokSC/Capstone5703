package com.example.csiro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.csiro.entity.Result;
import com.example.csiro.util.TensorFlowPrediction;

import java.io.FileNotFoundException;

public class PredictionActivity extends AppCompatActivity {

    TensorFlowPrediction tensorFlowPrediction;
    private final static String MODEL_PATH = "file:///android_asset/DenseNet169.h5";
    Bitmap bitmap;
    Result result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        predict();
        finish();
    }

    private void predict(){
        tensorFlowPrediction = new TensorFlowPrediction(getAssets(), MODEL_PATH);
        Uri imageUri = Uri.parse(getIntent().getStringExtra("imgUri"));
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            result = tensorFlowPrediction.predict(bitmap);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("Result", result);
        startActivity(intent);
    }
}