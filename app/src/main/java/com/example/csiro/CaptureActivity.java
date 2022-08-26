package com.example.csiro;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class CaptureActivity extends AppCompatActivity {

    private Uri imageUri;
    public String path = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OpenCamera();
        finish();
    }

    private void OpenCamera(){
        String imageName = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());

        File outputImage = new File(getExternalCacheDir(), imageName+".jpg");

        Objects.requireNonNull(outputImage.getParentFile()).mkdirs();

        try
        {
            if(outputImage.exists())
            {
                outputImage.delete();
            }
            boolean a = outputImage.createNewFile();
            Log.e("createNewFile", String.valueOf(a));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        if(Build.VERSION.SDK_INT>=24)
        {
            imageUri= FileProvider.getUriForFile(this,"com.example.csiro.fileprovider", outputImage);

            path=imageUri.getPath();
            Log.e(">7:",path);
        }
        else {
            imageUri= Uri.fromFile(outputImage);
            path=imageUri.getPath();

            Log.e("<7:",imageUri.getPath());

        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);

        Intent toPrediction = new Intent(this, PredictionActivity.class);
        toPrediction.setData(imageUri);
        startActivity(toPrediction);

    }
}