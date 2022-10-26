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
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csiro.capstone.databinding.FragmentEdgeBinding;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class EdgeFragment extends Fragment {

    private FragmentEdgeBinding binding;

    private Uri imageUri;

    // Result Receiver Object.
    ActivityResultLauncher<Intent> cropActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {

                    }
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            imageUri = getArguments().getParcelable("ImageUri");
            Rect rect = findMaxRect(detectEdges(imageUri));

            Intent crop = new Intent("com.android.camera.action.CROP");
            crop.setDataAndType(imageUri, "image/*");
            cropActivityResultLauncher.launch(crop);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edge, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.confirmButton.setOnClickListener(resultView -> NavHostFragment.findNavController(EdgeFragment.this)
                .navigate(R.id.action_WelcomeFragment_to_MainFragment));
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

        Mat tmp = cannyMat.clone();

        List<MatOfPoint> contours = new ArrayList();

        Mat hierarchy = new Mat();
        // Find Contours inside the Image
        Imgproc.findContours(cannyMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        int index = 0;
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