package com.csiro.capstone;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.csiro.capstone.databinding.FragmentMainBinding;
import com.csiro.capstone.util.UriTransformer;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;

    // Image Uri.
    private Uri imageUri;

    // Image File.
    private File imageFile;

    // Result Receiver Object of Album.
    ActivityResultLauncher<Intent> albumActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imageUri = result.getData().getData();
                        Log.i("Authority", imageUri.getAuthority());
                    }
                }
            });

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();

        // If User Capture a Photo or Upload One from Album,
        // Pass It to Prediction Fragment.
        if (imageUri != null){
            Bundle bundle = new Bundle();
            bundle.putParcelable("ImageUri", imageUri);
            NavHostFragment.findNavController(this)
                    .navigate(R.id.action_MainFragment_to_EdgeFragment, bundle);
//            NavHostFragment.findNavController(this)
//                    .navigate(R.id.action_MainFragment_to_ResultFragment, bundle);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageUri = null;

        // Button Click Event: Use Camera
        binding.buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Generate File Name with Current Time.
                String imageName = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());

                // Generate File Object.
                imageFile = new File(getActivity().getExternalCacheDir(), imageName+".jpg");

                // Write File Object into Cache.
                Objects.requireNonNull(imageFile.getParentFile()).mkdirs();

                // Avoid File with the Same Name.
                try
                {
                    if(imageFile.exists())
                    {
                        imageFile.delete();
                    }
                    boolean a = imageFile.createNewFile();
                    Log.i("New File Created", String.valueOf(a));
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }

                // Targeting Android Platforms with Different Version.
                if(Build.VERSION.SDK_INT>=24)
                {
                    imageUri = FileProvider.getUriForFile(getContext(),"com.example.csiro.fileprovider", imageFile);
                    Log.i("Android Version > 7:",imageUri.getPath());
                }
                else {
                    imageUri = Uri.fromFile(imageFile);
                    Log.i("Android Version < 7:",imageUri.getPath());
                }

                // Start Capture Activity.
                Intent capture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                capture.putExtra(MediaStore.EXTRA_OUTPUT,imageUri);
                startActivity(capture);
            }
        });

        // Button Click Event: Upload Photo from Album
        binding.buttonAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Start Selection Activity and Use Result Launcher to Receive Image Uri.
                Intent album = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                album.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
                albumActivityResultLauncher.launch(album);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}