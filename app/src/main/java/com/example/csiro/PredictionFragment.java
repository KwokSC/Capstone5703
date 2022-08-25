package com.example.csiro;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.csiro.databinding.FragmentPredictionBinding;
import com.example.csiro.entity.ResponseObject;
import com.example.csiro.entity.Result;
import com.example.csiro.request.ResultRequest;
import com.example.csiro.util.ClientConnection;

import java.io.IOException;

import retrofit2.Response;

public class PredictionFragment extends Fragment {

    private FragmentPredictionBinding binding;
    private Intent intent_capture;
    private Intent intent_prediction;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentPredictionBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Button Click Event: Use Camera
        binding.buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    intent_capture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent_prediction = new Intent(getActivity(), PredictionActivity.class);
                    startActivity(intent_capture);
                    startActivity(intent_prediction);
                }catch (Exception e){
                    e.printStackTrace();
                }
                ResultRequest request = ClientConnection.retrofit.create(ResultRequest.class);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Response<ResponseObject<Result>> response = request.getResult("").execute();
                            Result result = response.body().getData();
                            binding.textViewResult.setText(result.getPositive().toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        // Button Click Event: Upload Photo from Album
        binding.buttonAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}