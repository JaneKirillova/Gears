package com.example.gears.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.gears.databinding.FragmentHomeBinding;


public class HomeFragment extends Fragment {
//    Button startGame
//    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
//        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textHome;
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
//        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
//
//        Bitmap bitmap = null;
//
//        if (requestCode == 1) {
//            if (resultCode == RESULT_OK) {
//                counter++;
//                Uri selectedImage = imageReturnedIntent.getData();
//                try {
//                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 640, 480, false);
//
//                imageView.setImageBitmap(scaledBitmap);
//                array = convertBitmapToByteArray(scaledBitmap);
////                loadPicture();
//            } else {
//                Toast.makeText(getApplicationContext(), "Something went wrong with loading thw picture", Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
}