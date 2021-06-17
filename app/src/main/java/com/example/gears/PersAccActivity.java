package com.example.gears;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.gears.events.SuccessEventGetPicture;
import com.example.gears.events.SuccessEventGetUser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class PersAccActivity extends AppCompatActivity {
    EventBus eventBus = EventBus.getDefault();
    TextView username, points, allGames, gamesWon, gamesLost;
    String userId, userToken;
    ImageView profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pers_acc);
        username = findViewById(R.id.space_for_username);
        points = findViewById(R.id.space_for_points);
        allGames = findViewById(R.id.space_for_all_games);
        gamesLost = findViewById(R.id.space_for_games_lost);
        gamesWon = findViewById(R.id.space_for_games_won);
        profile = findViewById(R.id.profile);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onStart() {
        super.onStart();
        eventBus.register(this);
        setUserPicture();
        boolean wasPlayedGame = SharedPrefManager.getInstance(this).wasPlayedGame();
        User user = SharedPrefManager.getInstance(this).getUser();
        userId = user.getId().toString();
        userToken = user.getToken();
        if (!wasPlayedGame) {
            username.setText(user.getUsername());
            points.setText(user.getPoints().toString());
            allGames.setText(user.getTotalNumberOfGames().toString());
            gamesWon.setText(user.getGamesWon().toString());
            gamesWon.setText(user.getGamesWon().toString());
        } else {
            getUserRequest();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(PersAccActivity.this, MainActivity2.class));
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        eventBus.unregister(this);
        super.onStop();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSuccessEventGetUser(SuccessEventGetUser event) {
        JSONObject obj = event.getResponse();
        try {
            User newUser = new User(
                    userToken,
                    obj.getString("username"),
                    obj.getString("password"),
                    obj.getLong("id"),
                    obj.getInt("points"),
                    obj.getInt("totalNumberOfGames"),
                    obj.getInt("numberOfGamesWon"),
                    obj.getInt("numberOfGamesLost"));
            SharedPrefManager.getInstance(getApplicationContext()).userLogin(newUser);
            SharedPrefManager.getInstance(getApplicationContext()).setWasPlayedGame(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setUserPicture() {
        boolean hasProfilePicture = SharedPrefManager.getInstance(getApplicationContext()).isPictureLoaded();
        if (hasProfilePicture) {
            Bitmap bitmap = BitmapFactory.decodeFile(SharedPrefManager.getInstance(getApplicationContext()).getPhotoDirectory());
            profile.setImageBitmap(bitmap);
        } else {
            profile.setImageDrawable(getResources().getDrawable(R.drawable.user_without_photo));
        }
    }
    private void getUserRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_GET_USER + "/" + userId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            System.out.println("\n\n\n" + response + "\n\n\n");
                            eventBus.post(new SuccessEventGetUser(obj));
                        } catch (JSONException e) {
                            System.out.print("ОШИБКА1: ");
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
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("token", userToken);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


}
