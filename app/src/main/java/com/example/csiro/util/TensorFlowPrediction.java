package com.example.csiro.util;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import com.example.csiro.entity.Result;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TensorFlowPrediction {

    // Input Shape
    private static final int IN_COL = 1;
    private static final int IN_ROW = 300*300;

    // Output Shape
    private static final int OUT_COL = 3;
    private static final int OUT_ROW = 1;

    // Input Name of the Model
    private static final String INPUT_NAME = "input";
    // Output Name of the Model
    private static final String OUTPUT_NAME = "output";

    TensorFlowInferenceInterface tensorFlowInferenceInterface;
    static {System.loadLibrary("tensorflow_inference");}

    public TensorFlowPrediction(AssetManager assetManager, String modelPath){
        tensorFlowInferenceInterface = new TensorFlowInferenceInterface(assetManager,modelPath);
    }

    public Result predict(Bitmap bitmap){
        // Transform Image to a 299*299 Float Array
        float[] inputData = bitmapToFloatArray(bitmap,300,300);

        // Give the Input Data to TensorFlow Interface with the Predefined Model
        tensorFlowInferenceInterface.feed(INPUT_NAME, inputData, IN_COL, IN_ROW);

        // Execute the TensorFlow Interface
        tensorFlowInferenceInterface.run(new String[] {OUTPUT_NAME});

        // Define the Output Variable with Specific Shape
        float[] outputs = new float[OUT_COL*OUT_ROW];

        // Fetch the Prediction Result and Save it in the Output Variable
        tensorFlowInferenceInterface.fetch(OUTPUT_NAME, outputs);

        return getResult(outputs);
    }

    public static float[] bitmapToFloatArray(Bitmap bitmap, int x, int y){

        // Obtain Height & Weight of Bitmap
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        // Calculate the Ratio of Scale Shape
        float scaleWidth = ((float) x) / width;
        float scaleHeight = ((float) y) / height;

        // Transform the Bitmap to Specific Shape
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        Log.i("TAG","bitmap width:"+bitmap.getWidth()+",height:"+bitmap.getHeight());
        Log.i("TAG","bitmap.getConfig():"+bitmap.getConfig());

        // Obtain the Transformed Bitmap for Iteration
        height = bitmap.getHeight();
        width = bitmap.getWidth();

        float[] result = new float[height*width];
        int k = 0;
        for(int j = 0;j < height;j++){
            for (int i = 0;i < width;i++){
                int pixel = bitmap.getPixel(i,j);
                int r = Color.red(pixel);
                int g = Color.green(pixel);
                int b = Color.blue(pixel);
                int a = Color.alpha(pixel);
                assert(r==g && g==b);
                result[k++] = r / 255.0f;
            }
        }
        return result;
    }

    public Result getResult(float[] outputs){

        int index = -1;
        float temp = 0;
        Result result = new Result();
        String brandName = null;

        for(int i = 0; i<outputs.length; i++){
            if(outputs[i] > temp){
                index = i;
                temp = outputs[i];
            }
        }

        switch (index){
            case 0:
                brandName = "CIGA";
                break;
            case 1:
                brandName = "RightSign";
                break;
            case 2:
                brandName = "Clungene";
                break;
        }

        result.setResultId(new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date()));
        result.setBoxBrand(brandName);
        result.setUploadDate(new Date());
        result.setReliability(temp);
        return result;
    }
}
