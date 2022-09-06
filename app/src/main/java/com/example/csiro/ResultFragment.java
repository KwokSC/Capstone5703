package com.example.csiro;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.csiro.databinding.FragmentResultBinding;
import com.example.csiro.entity.Result;

public class ResultFragment extends Fragment {

    private FragmentResultBinding binding;

    public static ResultFragment newInstance(Result result, Bitmap bitmap){

        ResultFragment resultFragment = new ResultFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("Result", result);
        bundle.putParcelable("Bitmap", bitmap);
        resultFragment.setArguments(bundle);
        return resultFragment;

    }

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
                .navigate(R.id.action_ResultFragment_to_PredictionFragment));

        if (getArguments() != null){
            binding.imageViewPhoto.setImageBitmap(getArguments().getParcelable("Bitmap"));
            Result result = (Result) getArguments().getSerializable("Result");
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