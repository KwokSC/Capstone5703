package com.example.csiro.util;

import android.content.res.AssetManager;
import android.graphics.Bitmap;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

public class TensorFlowPrediction {

    TensorFlowInferenceInterface tensorFlowInferenceInterface;
    static {System.loadLibrary("tensorflow_inference");}

    TensorFlowPrediction(AssetManager assetManager, String modelPath){
        tensorFlowInferenceInterface = new TensorFlowInferenceInterface(assetManager,modelPath);
    }

    public int predict(Bitmap bitmap){
        return 1;
    }

}
