package com.csiro.capstone;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.csiro.capstone.databinding.FragmentResultBinding;
import com.csiro.capstone.entity.Brand;
import com.csiro.capstone.entity.Result;
import com.csiro.capstone.ml.Inceptionv3;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ResultFragment extends Fragment {

    private FragmentResultBinding binding;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentResultBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Obtain all information of brands.
        readDescription();

        // Attributes description.
        String attributeDescription = "What they mean:" + "\n"
                + "Clinical Sensitivity: Sensitivity to antigen content" + "\n"
                + "PRA: Positive percent agreement." + "\n"
                + "Source: https://www.tga.gov.au/products/covid-19/covid-19-tests/covid-19-rapid-antigen-self-tests-home-use/covid-19-rapid-antigen-self-tests-are-approved-australia";
        binding.textViewAttributes.setText(attributeDescription);

        // If the Fragment Receives Bundle Containing Result and Input Image,
        // Then Display them.
        if (getArguments() != null){
            Result result = predict(getArguments().getParcelable("ImageUri"));
            if (result != null){
                binding.imageViewPhoto.setImageBitmap(result.getBitmap());
                binding.textViewDescription.setText(result.getDescription());
                binding.textViewPrediction.setText(result.getBoxBrand());
            }
        }

        binding.confirmButton.setOnClickListener(predictionView -> NavHostFragment.findNavController(ResultFragment.this)
                .navigate(R.id.action_ResultFragment_to_MainFragment));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private Result predict(Uri uri){

        // Bitmap Object for Displaying in Result UI.
        Bitmap bitmap = null;

        // Result Object Received from Prediction Calculation.
        Result result = new Result();

        // Store model outputs.
        List<Category> probability = null;

        try {
            // Instantiate A InceptionV3 Model.
            Inceptionv3 model = Inceptionv3.newInstance(getContext());

            bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uri));

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
        result.setBitmap(bitmap);
        result.setResultId(new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date()));
        result.setBoxBrand(brand);
        result.setUploadDate(new Date());
        result.setReliability(max);
        for (Brand b : brands){
            if (Objects.equals(result.getBoxBrand(), b.getBrand())){
                result.setDescription("Clinical Sensitivity: " + b.getSensitivity() + "\n"
                        + "PRA: " + b.getPra() + "\n"
                        + "Test Type: " + b.getType() + "\n"
                        + "Source Link: " + b.getSource());
                Log.i("Info", result.getDescription());
            } else {
                Log.i("Info", "No brand found.");
            }
        }

        Log.i("Result", result.toString());

        return result;
    }

    // Read brand information.
    private List<Brand> brands = new ArrayList<>();
    private void readDescription(){

        InputStream is = getResources().openRawResource(R.raw.description);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        String line;
        try {
            reader.readLine();
            while((line = reader.readLine()) != null){
                String[] tokens = line.split(",");
                Brand brand = new Brand();
                brand.setBrand(tokens[0]);
                brand.setSensitivity(tokens[1]);
                brand.setApproved(tokens[2]);
                brand.setPra(tokens[3]);
                brand.setType(tokens[4]);
                brand.setWaitingTime(tokens[5]);
                brand.setRating(tokens[6]);
                brand.setSource(tokens[7]);
                brands.add(brand);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        Log.i("Brands", brands.get(0).getBrand());
    }

}