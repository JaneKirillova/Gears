package com.example.gears;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class RatingActivity extends AppCompatActivity {
    Button updateRating;
    List<UserForRating> users = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        updateRating = findViewById(R.id.update_rating);
        updateRating.setOnClickListener(v -> ratingRequest());
        initTextViews();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        startActivity(new Intent(getApplicationContext(), NavigationActivity.class));
    }

    private void ratingRequest() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_GET_RATING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        try {
                            JSONArray array = new JSONArray(response);
                            setUsers(array);
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
                }) {};
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void setUsers(JSONArray userArray) {
        for (int i = 0; i < 10; i++) {
            try {
                JSONObject obj = userArray.getJSONObject(i);
                users.get(i).username.setText(obj.getString("username"));
                users.get(i).points.setText(obj.getString("points"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initTextViews() {
        UserForRating user1 = new UserForRating();
        user1.username = findViewById(R.id.username1);
        user1.points = findViewById(R.id.points1);
        user1.gamesWon = findViewById(R.id.games_won1);
        user1.gamesLost = findViewById(R.id.games_lost1);
        users.add(user1);

        UserForRating user2 = new UserForRating();
        user2.username = findViewById(R.id.username2);
        user2.points = findViewById(R.id.points2);
        user2.gamesWon = findViewById(R.id.games_won2);
        user2.gamesLost = findViewById(R.id.games_lost2);
        users.add(user2);

        UserForRating user3 = new UserForRating();
        user3.username = findViewById(R.id.username3);
        user3.points = findViewById(R.id.points3);
        user3.gamesWon = findViewById(R.id.games_won3);
        user3.gamesLost = findViewById(R.id.games_lost3);
        users.add(user3);

        UserForRating user4 = new UserForRating();
        user4.username = findViewById(R.id.username4);
        user4.points = findViewById(R.id.points4);
        user4.gamesWon = findViewById(R.id.games_won4);
        user4.gamesLost = findViewById(R.id.games_lost4);
        users.add(user4);

        UserForRating user5 = new UserForRating();
        user5.username = findViewById(R.id.username5);
        user5.points = findViewById(R.id.points5);
        user5.gamesWon = findViewById(R.id.games_won5);
        user5.gamesLost = findViewById(R.id.games_lost5);
        users.add(user5);

        UserForRating user6 = new UserForRating();
        user6.username = findViewById(R.id.username6);
        user6.points = findViewById(R.id.points6);
        user6.gamesWon = findViewById(R.id.games_won6);
        user6.gamesLost = findViewById(R.id.games_lost6);
        users.add(user6);

        UserForRating user7 = new UserForRating();
        user7.username = findViewById(R.id.username7);
        user7.points = findViewById(R.id.points7);
        user7.gamesWon = findViewById(R.id.games_won7);
        user7.gamesLost = findViewById(R.id.games_lost7);
        users.add(user7);

        UserForRating user8 = new UserForRating();
        user8.username = findViewById(R.id.username8);
        user8.points = findViewById(R.id.points8);
        user8.gamesWon = findViewById(R.id.games_won8);
        user8.gamesLost = findViewById(R.id.games_lost8);
        users.add(user8);

        UserForRating user9 = new UserForRating();
        user9.username = findViewById(R.id.username9);
        user9.points = findViewById(R.id.points9);
        user9.gamesWon = findViewById(R.id.games_won9);
        user9.gamesLost = findViewById(R.id.games_lost9);
        users.add(user9);

        UserForRating user10 = new UserForRating();
        user10.username = findViewById(R.id.username10);
        user10.points = findViewById(R.id.points10);
        user10.gamesWon = findViewById(R.id.games_won10);
        user10.gamesLost = findViewById(R.id.games_lost10);
        users.add(user10);
    }

}
