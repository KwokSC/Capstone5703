package com.csiro.capstone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.csiro.capstone.databinding.FragmentEdgeBinding;
import com.csiro.capstone.util.UriTransformer;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class EdgeFragment extends Fragment {

    private FragmentEdgeBinding binding;

    // Image Uri.
    private Uri imageUri;

    // Crop Uri.
    private Uri cropUri;

    // Contour rectangle.
    private Rect rect;

    // Bitmap for display.
    private Bitmap bitmap;

    // For pass bitmap.
    private Bundle bundle = new Bundle();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUri = getArguments().getParcelable("ImageUri");
            Log.i("Uri Authority", imageUri.getAuthority());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentEdgeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.nextButton.setOnClickListener(resultView -> NavHostFragment.findNavController(EdgeFragment.this)
                .navigate(R.id.action_EdgeFragment_to_ResultFragment, bundle));

        binding.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Please ensure remove background as much as possible.", Toast.LENGTH_SHORT).show();
                Intent crop = new Intent(Intent.ACTION_EDIT);
                crop.setDataAndType(imageUri, "image/*");
                crop.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
                List<ResolveInfo> resInfoList = getActivity().getPackageManager().queryIntentActivities(crop, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    getActivity().grantUriPermission(packageName, cropUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    getActivity().grantUriPermission(packageName, imageUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                startActivity(crop);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if (cropUri != null){
            try {
                bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(cropUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            try {
                bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }


        if (rect == null) {
            rect = findMaxRect(detectEdges(imageUri));
            bitmap = Bitmap.createBitmap(bitmap, rect.x, rect.y, rect.width, rect.height);
            createUriFromBitmap(bitmap);
        }

        bundle.putParcelable("ImageUri", cropUri);
        binding.imageViewCrop.setImageBitmap(bitmap);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        imageUri = null;
        bitmap = null;
        rect = null;
        bundle = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private Mat detectEdges(Uri uri) {

        Mat rgba = new Mat();

        Bitmap bitmap = null;

        try {
            bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Utils.bitmapToMat(bitmap, rgba);

        Mat edges = new Mat(rgba.size(), CvType.CV_8UC1);
        Imgproc.cvtColor(rgba, edges, Imgproc.COLOR_RGB2GRAY, 4);
        Imgproc.Canny(edges, edges, 80, 100);

        return edges;
    }

    public Rect findMaxRect(Mat cannyMat) {

        // Store the largest contour.
        Mat tmp = cannyMat.clone();

        // Store all contours inside the image.
        List<MatOfPoint> contours = new ArrayList();

        // Contour hierarchy information.
        Mat hierarchy = new Mat();

        // Find contours inside the image.
        Imgproc.findContours(cannyMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Index of the largest contour in the contour list.
        int index = 0;

        // The contour with the largest perimeter in the contour list.
        double perimeter = 0;

        // Find the maximum Contour
        for (int i = 0; i < contours.size(); i++) {

            // Largest Perimeter
            MatOfPoint2f source = new MatOfPoint2f();

            source.fromList(contours.get(i).toList());
            double length = Imgproc.arcLength(source, true);
            if (length > perimeter) {
                perimeter = length;
                index = i;
            }
        }

        Imgproc.drawContours(
                tmp,
                contours,
                index,
                new Scalar(0.0, 0.0, 255.0),
                9,
                Imgproc.LINE_AA
        );

        Rect rect = Imgproc.boundingRect(contours.get(index));
        Log.i("Rect", rect.toString());
        return rect;
    }


    private void createUriFromBitmap(Bitmap bitmap){
        // Generate File Name with Current Time.
        String imageName = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());

        // Generate File Object.
        File imageFile = new File(getActivity().getExternalCacheDir(), "crop_" + imageName+".jpg");

        // Write File Object into Cache.
        Objects.requireNonNull(imageFile.getParentFile()).mkdirs();

        // Avoid File with the Same Name.
        try {
            if(imageFile.exists()) {
                imageFile.delete();
            }
            boolean a = imageFile.createNewFile();
            Log.i("New File Created", String.valueOf(a));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Write the bitmap to the file.
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Targeting Android Platforms with Different Version.
        if(Build.VERSION.SDK_INT>=24) {
            cropUri = FileProvider.getUriForFile(getContext(),"com.example.csiro.fileprovider", imageFile);
            Log.i("Android Version > 7:",cropUri.getPath());
        } else {
            cropUri = Uri.fromFile(imageFile);
            Log.i("Android Version < 7:",cropUri.getPath());
        }

        Log.i("Crop Uri Authority", cropUri.getAuthority());
    }

}