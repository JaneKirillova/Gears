package com.example.gears;

import android.content.Context;
import android.content.SharedPreferences;

import com.android.volley.toolbox.StringRequest;


public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "simplifiedcodingsharedpref";
    private static final String KEY_USERNAME = "keyusername";
    private static final String KEY_TOKEN = "keytoken";
    private static final String KEY_PASSWORD = "keypassword";
    private static final String KEY_ID = "keyid";
    private static final String KEY_POINTS = "keypoints";
    private static final String KEY_CURRENT_GAME_ID = "keycurrentgameid";
    private static final String KEY_CURRENT_GAME_PLAYER_NUM = "keycurrentplayerturnnum";

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public String getToken() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, user.getToken());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_PASSWORD, user.getPassword());
        editor.putLong(KEY_ID, user.getId());
        editor.putInt(KEY_POINTS, user.getPoints());
        editor.apply();
    }

    public void writeGame(String gameId, String playerNum) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_CURRENT_GAME_ID, gameId);
        editor.putString(KEY_CURRENT_GAME_PLAYER_NUM, playerNum);
        editor.apply();
    }

    public String getGameId() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_CURRENT_GAME_ID, null);
    }

    public String getCurrentPlayerNum() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_CURRENT_GAME_PLAYER_NUM, null);
    }

    public void setUserPoint(int points) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(KEY_POINTS, points);
        editor.apply();
    }

    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getString(KEY_TOKEN, null),
                sharedPreferences.getString(KEY_USERNAME, null),
                sharedPreferences.getString(KEY_PASSWORD, null),
                sharedPreferences.getLong(KEY_ID, 0),
                sharedPreferences.getInt(KEY_POINTS, 0)
        );
    }
}

