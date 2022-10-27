package com.csiro.capstone;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csiro.capstone.databinding.FragmentEdgeBinding;
import com.csiro.capstone.databinding.FragmentResultBinding;

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

    // Image file.
    private File imageFile;

    // Result Receiver Object.
    ActivityResultLauncher<Intent> cropActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        imageUri = result.getData().getData();
                    }
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            // Generate File Name with Current Time.
            String imageName = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());

            // Generate File Object.
            File outputImage = new File(getActivity().getExternalCacheDir(), imageName+"_cropped.jpg");

            // Write File Object into Cache.
            Objects.requireNonNull(outputImage.getParentFile()).mkdirs();

            imageFile = (File) getArguments().getSerializable("ImageFile");
            imageUri = FileProvider.getUriForFile(getContext(),"com.example.csiro.fileprovider", imageFile);
            Rect rect = findMaxRect(detectEdges(imageUri));

            Intent crop = new Intent("com.android.camera.action.CROP");
            crop.setDataAndType(imageUri, "image/*");
            crop.putExtra("outputX", 300);
            crop.putExtra("outputY", 300);
            crop.putExtra("aspectX", 1);
            crop.putExtra("aspectY", 1);
            crop.putExtra("scale", true);
            crop.putExtra("return-data", true);
            crop.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            crop.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            crop.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outputImage));
            cropActivityResultLauncher.launch(crop);
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
                .navigate(R.id.action_EdgeFragment_to_ResultFragment));
    }

    @Override
    public void onResume() {
        super.onResume();

        if (imageUri != null){
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));
                binding.imageViewCrop.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
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

            // Largest Size
            // double area = Imgproc.contourArea(contours.get(i));

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

        Rect rect = Imgproc.boundingRect((Mat) contours.get(index));

        // Imgproc.rectangle(tmp, rect, new Scalar(0.0, 0.0, 255.0), 4, Imgproc.LINE_8);

        return rect;
    }

}