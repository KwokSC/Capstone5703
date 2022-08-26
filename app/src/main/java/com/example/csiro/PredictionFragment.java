package com.example.csiro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentPredictionBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Button Click Event: Use Camera
        binding.buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent_capture = new Intent(getActivity(), CaptureActivity.class);
                startActivity(intent_capture);

                // Here to call Prediction Activity to obtain Predicted Result
                Intent intent_prediction = new Intent(getActivity(), PredictionActivity.class);

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
                    Intent intent_album = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivity(intent_album);
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