package com.example.gears;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {
    private Button changePassword, changeUsername, loadPicture;
    private EditText newPassword, confirmPassword, newUsername;
    private byte[] array;
    private ImageView profilePicture;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        changePassword = findViewById(R.id.change_password_button);
        changeUsername = findViewById(R.id.change_username_button);
        loadPicture = findViewById(R.id.load_image);
        newPassword = findViewById(R.id.change_password);
        confirmPassword = findViewById(R.id.confirm_password);
        newUsername = findViewById(R.id.change_username);

        profilePicture = findViewById(R.id.profile_photo);

        Boolean hasProfilePicture = SharedPrefManager.getInstance(getApplicationContext()).isPictureLoaded();
        if (hasProfilePicture) {
            Bitmap bitmap = BitmapFactory.decodeFile(SharedPrefManager.getInstance(getApplicationContext()).getPhotoDirectory());
            profilePicture.setImageBitmap(bitmap);
        } else {
            profilePicture.setImageDrawable(getResources().getDrawable(R.drawable.user_without_photo));
        }

        changePassword.setOnClickListener(v -> {
            String newPasswordString = newPassword.getText().toString().trim();
            String confirmPasswordString = confirmPassword.getText().toString().trim();
            if (newPasswordString.isEmpty() || confirmPasswordString.isEmpty()) {
                Toast.makeText(SettingsActivity.this, "Enter new password to change it", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPasswordString.equals(confirmPasswordString)) {
                Toast.makeText(SettingsActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            changePasswordRequest(newPasswordString);
        });

        changeUsername.setOnClickListener(v -> {
            String usernameString = newUsername.getText().toString().trim();
            if (usernameString.isEmpty()) {
                Toast.makeText(SettingsActivity.this, "Enter new username to change it", Toast.LENGTH_SHORT).show();
                return;
            }
            changeUsernameRequest(usernameString);
        });

        loadPicture.setOnClickListener(v -> {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity2.class));
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Uri selectedImage = imageReturnedIntent.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 640, 480, false);

                profilePicture.setImageBitmap(scaledBitmap);
                array = convertBitmapToByteArray(scaledBitmap);
                loadPictureRequest();
            } else {
                Toast.makeText(getApplicationContext(), "Something went wrong with loading thw picture", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void changePasswordRequest(String password) {
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, URLs.URL_CHANGE_PASSWORD + "/" + user.getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SharedPrefManager.getInstance(getApplicationContext()).changePassword(password);
                        newPassword.getText().clear();
                        confirmPassword.getText().clear();
                        Toast.makeText(SettingsActivity.this, "Passwords was successfully changed", Toast.LENGTH_SHORT).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.print("ОШИБКА2: ");
                        String s = new String(error.networkResponse.data, Charset.defaultCharset());
                        System.out.println(s);
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return password.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("token", user.getToken());
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void changeUsernameRequest(String username) {
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, URLs.URL_CHANGE_USERNAME + "/" + user.getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        SharedPrefManager.getInstance(getApplicationContext()).changeUsername(username);
                        newUsername.getText().clear();
                        Toast.makeText(SettingsActivity.this, "Username was successfully changed", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.print("ОШИБКА2: ");
                        String s = new String(error.networkResponse.data, Charset.defaultCharset());
                        System.out.println(s);
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return username.getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("token", user.getToken());
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void loadPictureRequest() {
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, URLs.URL_LOAD_IMAGE + "/" + user.getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ContextWrapper cw = new ContextWrapper(getApplicationContext());
                        File directory = cw.getDir("profile", Context.MODE_PRIVATE);
                        if (!directory.exists()) {
                            directory.mkdir();
                        }
                        File mypath = new File(directory, "thumbnail.png");

                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(mypath);
                            fos.write(array);
                            fos.close();
                        } catch (Exception e) {
                            Log.e("SAVE_IMAGE", e.getMessage(), e);
                        }
                        SharedPrefManager.getInstance(getApplicationContext()).savePhotoDirectory(mypath.getPath());
                        SharedPrefManager.getInstance(getApplicationContext()).setPictureIsLoaded(true);
                        Toast.makeText(SettingsActivity.this, "Picture was successfully loaded", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.print("ОШИБКА2: ");
                        String s = new String(error.networkResponse.data, Charset.defaultCharset());
                        System.out.println(s);
                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                return array;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("token", user.getToken());
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    public static byte[] convertBitmapToByteArray(Bitmap bitmap){
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return baos.toByteArray();
        }finally {
            if(baos != null){
                try {
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

