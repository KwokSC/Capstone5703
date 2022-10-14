package com.example.csiro;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.csiro.databinding.FragmentResultBinding;
import com.example.csiro.entity.Result;

import java.io.FileNotFoundException;

public class ResultFragment extends Fragment {

    private FragmentResultBinding binding;

    // Bitmap Object for Displaying in Result UI.
    private Bitmap bitmap;

    // Result Object Received from Prediction Calculation.
    private Result result;

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

        binding.confirmButton.setOnClickListener(predictionView -> NavHostFragment.findNavController(ResultFragment.this)
                .navigate(R.id.action_ResultFragment_to_MainActivity));

        // If the Fragment Receives Bundle Containing Result and Input Image,
        // Then Display them.
        if (getArguments() != null){
            result = (Result) getArguments().getSerializable("Result");
            try {
                bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(getArguments().getParcelable("ImageUri")));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            binding.imageViewPhoto.setImageBitmap(bitmap);
            binding.textViewDescription.setText(result.getDescription());
            binding.textViewPrediction.setText(result.getBoxBrand());
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}