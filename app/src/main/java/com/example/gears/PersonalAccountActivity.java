package com.example.gears;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import com.android.volley.AuthFailureError;
import com.example.gears.events.SuccessEventFindOpponent;
import com.example.gears.events.SuccessEventGetPicture;
import com.example.gears.events.SuccessEventGetUser;
import com.example.gears.events.SuccessEventLoadPicture;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class PersonalAccountActivity extends AppCompatActivity {
    Button startGame, loadImage, tmpButton;
    EventBus eventBus = EventBus.getDefault();
    TextView userId, userLogin, userPassword;
    User user;
    int counter = 0;
    byte[] array;
    ImageView imageView;

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

    public static byte[] convertBitmapToByteArrayUncompressed(Bitmap bitmap){
        ByteBuffer byteBuffer = ByteBuffer.allocate(bitmap.getByteCount());
        bitmap.copyPixelsToBuffer(byteBuffer);
        byteBuffer.rewind();
        return byteBuffer.array();
    }

    public static Bitmap convertCompressedByteArrayToBitmap(byte[] src){
        return BitmapFactory.decodeByteArray(src, 0, src.length);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                counter++;
                Uri selectedImage = imageReturnedIntent.getData();
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 640, 480, false);

                imageView.setImageBitmap(scaledBitmap);
                array = convertBitmapToByteArray(scaledBitmap);
                loadPicture();
            } else {
                Toast.makeText(getApplicationContext(), "Something went wrong with loading thw picture", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getPicture() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_LOAD_IMAGE + "/" + user.getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            eventBus.post(new SuccessEventGetPicture(obj));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                }) { };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    private void loadPicture() {
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, URLs.URL_LOAD_IMAGE + "/" + user.getId(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        eventBus.post(new SuccessEventLoadPicture());
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

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_account);

        imageView = (ImageView) findViewById(R.id.imageView);
        userId = findViewById(R.id.userId);
        userLogin = findViewById(R.id.userLogin);
        userPassword = findViewById(R.id.userPassword);
        loadImage = findViewById(R.id.load_picture);

        loadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });

        tmpButton = findViewById(R.id.button);
        tmpButton.setOnClickListener(v -> {
            startActivity(new Intent(PersonalAccountActivity.this, MainActivity2.class));
        });

        final User oldUser = SharedPrefManager.getInstance(this).getUser();


//        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_GET_USER + "/" + oldUser.getId(),
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        try {
//                            JSONObject obj = new JSONObject(response);
//                            System.out.println("\n\n\n" + response + "\n\n\n");
//                            eventBus.post(new SuccessEventGetUser(obj));
//                        } catch (JSONException e) {
//                            System.out.print("ОШИБКА1: ");
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        System.out.print("ОШИБКА2: ");
//                        String s = new String(error.networkResponse.data, Charset.defaultCharset());
//                        System.out.println(s);
//                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
//
//                    }
//                }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//                params.put("Content-Type", "application/json");
//                params.put("token", oldUser.getToken());
//                return params;
//            }
//        };
//
//        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);



        user = SharedPrefManager.getInstance(this).getUser();

        userId.setText(String.valueOf(user.getToken()));
        userLogin.setText(user.getUsername());
        userPassword.setText(user.getPassword());

        startGame = findViewById(R.id.startGame);
        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                findOpponent();
                startActivity(new Intent(PersonalAccountActivity.this, GameActivity.class));
            }
        });
    }


    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSuccessEventGetUser(SuccessEventGetUser event) {
        JSONObject obj = event.getResponse();
        User oldUser = SharedPrefManager.getInstance(this).getUser();
        try {
            userId.setText(String.valueOf(obj.getInt("points")));
            userLogin.setText(obj.getString("password"));
            userPassword.setText(obj.getString("username"));

//            User newUser = new User(
//                    oldUser.getToken(),
//                    obj.getString("username"),
//                    obj.getString("password"),
//                    obj.getLong("id"),
//                    obj.getInt("points"));
//            SharedPrefManager.getInstance(getApplicationContext()).userLogin(newUser);
            setPicture();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setPicture() {
        boolean wasLoaded = SharedPrefManager.getInstance(getApplicationContext()).isPictureLoaded();
        if (wasLoaded) {
            Bitmap bitmap = BitmapFactory.decodeFile(SharedPrefManager.getInstance(getApplicationContext()).getPhotoDirectory());
            imageView.setImageBitmap(bitmap);
        } else {
            getPicture();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSuccessEventGetPicture(SuccessEventGetPicture event) {
        String str = null;
        try {
            str = event.getResponse().getString("picture");
            if (!str.equals("null")) {
                byte[] picture = new byte[0];
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    picture = java.util.Base64.getDecoder().decode(str);
                }
                Bitmap bmp = convertCompressedByteArrayToBitmap(picture);
                imageView.setImageBitmap(bmp);
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                File directory = cw.getDir("profile", Context.MODE_PRIVATE);
                if (!directory.exists()) {
                    directory.mkdir();
                }
                File mypath = new File(directory, "picture.png");

                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(mypath);
                    fos.write(picture);
                    fos.close();
                } catch (Exception e) {
                    Log.e("SAVE_IMAGE", e.getMessage(), e);
                }
                SharedPrefManager.getInstance(getApplicationContext()).setPictureIsLoaded(true);
                SharedPrefManager.getInstance(getApplicationContext()).savePhotoDirectory(mypath.getPath());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSuccessEventLoadPicture(SuccessEventLoadPicture event) {
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
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSuccessEventFindOpponent(SuccessEventFindOpponent event) {
        try {
            JSONObject obj = new JSONObject(event.getResponse());
            System.out.println(event.getResponse());
            String gameId = (String) obj.names().get(0);
            boolean isFirstPlayer = obj.getBoolean(gameId);
            String playerNum;
            if (isFirstPlayer) {
                playerNum = "FIRSTPLAYER";
            } else {
                playerNum = "SECONDPLAYER";
            }
            SharedPrefManager.getInstance(getApplicationContext()).writeGame(gameId, playerNum);
            startActivity(new Intent(getApplicationContext(), GameActivity.class));

        } catch (JSONException e) {
            System.out.print("ОШИБКА1: ");
            e.printStackTrace();
        }
    }


    private void findOpponent() {
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!FIND OPPONENT!!!!!!!!!!!!!!!!!!!!!!!!!!1");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_FIND_OPPONENT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        eventBus.post(new SuccessEventFindOpponent(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.print("ОШИБКА2: ");
                        System.out.println(error.toString());
//                        String s = new String(error.networkResponse.data, Charset.defaultCharset());
//                        System.out.println(s);
//                        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("username", user.getUsername());
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
//                params.put("Content-Type", "application/json");
                params.put("token", user.getToken());
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
