package com.example.csiro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.csiro.entity.Result;

import java.io.FileNotFoundException;

public class ResultActivity extends AppCompatActivity {

//    private ImageView imageView;
//    private TextView description;
//    private TextView brandName;

    private Result result;
    private Bitmap bitmap;
    private ResultFragment resultFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        imageView = findViewById(R.id.imageView_photo);
//        description = findViewById(R.id.textView_description);
//        brandName = findViewById(R.id.textView_prediction);

        // Obtain Result and Image from Prediction Activity.
        result = (Result) getIntent().getExtras().getSerializable("Result");
        Uri imageUri = getIntent().getExtras().getParcelable("ImageUri");

        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        ResultFragment.result = this.result;
        ResultFragment.image = this.bitmap;
    }


//    @Override
//    public void resultDisplay(Result result) {
//        imageView.setImageBitmap(result.getBitmap());
//        description.setText(result.getDescription() + "/n" + result.getReliability());
//        brandName.setText(result.getBoxBrand());
//    }
}