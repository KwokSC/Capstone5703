package com.example.csiro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.csiro.util.TensorFlowPrediction;

public class PredictionActivity extends AppCompatActivity {

    TensorFlowPrediction tensorFlowPrediction;
    private final static String MODEL_PATH = "file:///android_asset/tensor_model.h5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_prediction);

        Log.i("PredictionActivity", "Run Prediction Activity");

        tensorFlowPrediction = new TensorFlowPrediction(getAssets(), MODEL_PATH);
    }
}