package com.example.gears;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPrefManager {
    private static final String SHARED_PREF_NAME = "simplifiedcodingsharedpref";
    private static final String KEY_USERNAME = "keyusername";
    private static final String KEY_TOKEN = "keytoken";
    private static final String KEY_PASSWORD = "keypassword";
    private static final String KEY_ID = "keyid";
    private static final String KEY_POINTS = "keypoints";
    private static final String KEY_ALL_GAMES = "keyallgames";
    private static final String KEY_GAMES_WON = "keygameswon";
    private static final String KEY_GAMES_LOST = "keygameslost";
    private static final String KEY_CURRENT_GAME_ID = "keycurrentgameid";
    private static final String KEY_CURRENT_GAME_PLAYER_NUM = "keycurrentplayerturnnum";
    private static final String KEY_PROFILE_PHOTO_DIRECTORY = "keyprofilephotodirectory";
    private static final String KEY_PICTURE_WAS_LOADED = "keypicturewasloaded";
    private static final String KEY_WAS_PLAYED_GAME = "keywasplayedgame";

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

    public void changePassword(String password) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }

    public void changeUsername(String username) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USERNAME, username);
        editor.apply();
    }

    public void savePhotoDirectory(String path) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_PROFILE_PHOTO_DIRECTORY, path);
        editor.apply();
    }

    public String getPhotoDirectory() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_PROFILE_PHOTO_DIRECTORY, null);
    }

    public boolean isPictureLoaded() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_PICTURE_WAS_LOADED, false);
    }

    public void setPictureIsLoaded(boolean pictureWasLoaded) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_PICTURE_WAS_LOADED, pictureWasLoaded);
        editor.apply();
    }

    public boolean wasPlayedGame() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_WAS_PLAYED_GAME, false);
    }

    public void setWasPlayedGame(boolean wasPlayedGame) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_WAS_PLAYED_GAME, wasPlayedGame);
        editor.apply();
    }

    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN, user.getToken());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_PASSWORD, user.getPassword());
        editor.putLong(KEY_ID, user.getId());
        editor.putInt(KEY_POINTS, user.getPoints());
        editor.putInt(KEY_ALL_GAMES, user.getTotalNumberOfGames());
        editor.putInt(KEY_GAMES_LOST, user.getGamesLost());
        editor.putInt(KEY_GAMES_WON, user.getGamesWon());
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
                sharedPreferences.getInt(KEY_POINTS, 0),
                sharedPreferences.getInt(KEY_ALL_GAMES, 0),
                sharedPreferences.getInt(KEY_GAMES_WON, 0),
                sharedPreferences.getInt(KEY_GAMES_LOST, 0)
        );
    }
}

