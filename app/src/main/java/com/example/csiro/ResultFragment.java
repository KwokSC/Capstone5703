package com.example.csiro;

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

    public static Bitmap image;
    public static Result result;
//    private Display display;

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

//    @Override
//    public void onAttach(@NonNull Context context) {
//        super.onAttach(context);
//        try {
//            display = (Display) context;
//        }catch (ClassCastException e){
//            throw new ClassCastException("Class cast failed, Activity should implement Display interface");
//        }
//        display.resultDisplay(this.getArguments().getParcelable("Result"));
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

//    public interface Display{ void resultDisplay(Result result); }

}