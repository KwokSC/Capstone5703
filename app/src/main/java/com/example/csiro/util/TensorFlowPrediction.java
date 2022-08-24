package com.example.csiro.util;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.net.URI;

public class TensorFlowPrediction {

    private static final int IN_COL = 1;
    private static final int IN_ROW = 299*299;
    private static final int OUT_COL = 1;
    private static final int OUT_ROW = 1;

    // Input Name of the Model
    private static final String inputName = "input/x_input";
    // Output Name of the Model
    private static final String outputName = "output";

    TensorFlowInferenceInterface tensorFlowInferenceInterface;
    static {System.loadLibrary("tensorflow_inference");}

    TensorFlowPrediction(AssetManager assetManager, String modelPath){
        tensorFlowInferenceInterface = new TensorFlowInferenceInterface(assetManager,modelPath);
    }

    public int predict(Bitmap bitmap){
        // Transform Image to a 299*299 Float Array
        float[] inputData = bitmapToFloatArray(bitmap,299,299);

        // Give the Input Data to TensorFlow Interface with the Predefined Model
        tensorFlowInferenceInterface.feed(inputName, inputData, IN_COL, IN_ROW);

        // Define the Output Variable
        String[] outputNames = new String[] {outputName};

        // Execute the TensorFlow Interface
        tensorFlowInferenceInterface.run(outputNames);

        //
        int[] outputs = new int[OUT_COL*OUT_ROW];

        tensorFlowInferenceInterface.fetch(outputName, outputs);

        return outputs[0];
    }

    public static float[] bitmapToFloatArray(Bitmap bitmap, int rx, int ry){
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();
        float scaleWidth = ((float) rx) / width;
        float scaleHeight = ((float) ry) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        Log.i("TAG","bitmap width:"+bitmap.getWidth()+",height:"+bitmap.getHeight());
        Log.i("TAG","bitmap.getConfig():"+bitmap.getConfig());
        height = bitmap.getHeight();
        width = bitmap.getWidth();
        float[] result = new float[height*width];
        int k = 0;
        for(int j = 0;j < height;j++){
            for (int i = 0;i < width;i++){
                int argb = bitmap.getPixel(i,j);
                int r = Color.red(argb);
                int g = Color.green(argb);
                int b = Color.blue(argb);
                int a = Color.alpha(argb);
                assert(r==g && g==b);
                result[k++] = r / 255.0f;
            }
        }
        return result;
    }
}
