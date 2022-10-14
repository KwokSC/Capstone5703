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

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Deprecated
public class PredictionActivity extends AppCompatActivity {

    // Store Bitmap Transformed from Local Image Uri.
    private Bitmap bitmap;

    // Instantiate a Result Object for Storing Result.
    private Result result = new Result();

    // Probability Output for TensorFlow Model.
    private List<Category> probability;

    // Local Image Uri for Prediction.
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Prediction Calculation.
        predict();

        // Avoid Stack Reuse this Activity.
        finish();
    }

    private void predict(){
        try {
            // Instantiate A InceptionV3 Model.
            Inceptionv3 model = Inceptionv3.newInstance(this);

            // Obtain Model Version.


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