package com.example.csiro;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.csiro.databinding.FragmentResultBinding;
import com.example.csiro.entity.Result;
import com.example.csiro.ml.Inceptionv3;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ResultFragment extends Fragment {

    private FragmentResultBinding binding;

    // Bitmap Object for Displaying in Result UI.
    private Bitmap bitmap;

    // Result Object Received from Prediction Calculation.
    private Result result = new Result();

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentResultBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // If the Fragment Receives Bundle Containing Result and Input Image,
        // Then Display them.
        if (getArguments() != null){
//            result = (Result) getArguments().getSerializable("Result");
//            try {
//                bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(getArguments().getParcelable("ImageUri")));
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
            predict(getArguments().getParcelable("ImageUri"));
            binding.imageViewPhoto.setImageBitmap(bitmap);
            binding.textViewDescription.setText(result.getDescription());
            binding.textViewPrediction.setText(result.getBoxBrand());
        }

        binding.confirmButton.setOnClickListener(predictionView -> NavHostFragment.findNavController(ResultFragment.this)
                .navigate(R.id.action_ResultFragment_to_MainFragment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void predict(Uri imageUri){

        List<Category> probability = null;

        try {
            // Instantiate A InceptionV3 Model.
            Inceptionv3 model = Inceptionv3.newInstance(getContext());

            bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(imageUri));

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
        for (Category category : probability) {
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
    }
}