package com.example.csiro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.csiro.entity.Result;
import com.example.csiro.ml.Inceptionv3;
import com.example.csiro.util.TensorFlowPrediction;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PredictionActivity extends AppCompatActivity {

//    TensorFlowPrediction tensorFlowPrediction;
//    private final static String MODEL_PATH = "file:///android_asset/InceptionV3.pb";
    private Bitmap bitmap;
    private Result result = new Result();
    private List<Category> probability;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        predict();
    }

//    private void predict(){
//        tensorFlowPrediction = new TensorFlowPrediction(getAssets(), MODEL_PATH);
//        Uri imageUri = getIntent().getParcelableExtra("imageUri");
//        Log.i("PATH", imageUri.getPath());
//        try {
//            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
//            result = tensorFlowPrediction.predict(bitmap);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        Intent intent = new Intent(this, ResultActivity.class);
//        intent.putExtra("Result", result);
//        startActivity(intent);
//    }

    private void predict(){
        try {
            // Instantiate A InceptionV3 Model.
            Inceptionv3 model = Inceptionv3.newInstance(this);

            // Obtain Image from Capture or Album Activity and Transform it into a Bitmap.
            imageUri = getIntent().getParcelableExtra("ImageUri");
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));

            // Creates Inputs for Reference.
            TensorImage image = TensorImage.fromBitmap(bitmap);

            // Runs Model inference and Gets Result.
            Inceptionv3.Outputs outputs = model.process(image);
            probability = outputs.getProbabilityAsCategoryList();

            // Releases Model Resources If No Longer Used.
            model.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Get the Prediction Result with the Maximum Probability.
        float max = 0;
        String brand = null;
        for (Category category:probability) {
            if(category.getScore() > max) {
                max = category.getScore();
                brand = category.getLabel();
            }
        }

        // Set Values for Empty Result Object.
        result.setResultId(new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date()));
        result.setBoxBrand(brand);
        result.setUploadDate(new Date());
        result.setReliability(max);
        Log.i("Result", result.toString());

        // Pass Result Object and Uploaded Image to Result Activity.
        Intent toResult = new Intent(this, ResultActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("Result", result);
        bundle.putParcelable("ImageUri", imageUri);
        toResult.putExtras(bundle);
        startActivity(toResult);
    }
}