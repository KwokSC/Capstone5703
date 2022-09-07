package com.example.csiro;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.example.csiro.entity.Result;

import java.io.FileNotFoundException;

public class ResultActivity extends AppCompatActivity {

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Uri imageUri = Uri.parse(getIntent().getStringExtra("imgUri"));
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Result result = (Result) getIntent().getSerializableExtra("Result");
        ResultFragment resultFragment = ResultFragment.newInstance(result, bitmap);
    }

}