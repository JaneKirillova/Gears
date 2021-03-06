package com.example.gears;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.gears.events.ErrorEvent;
import com.example.gears.events.ErrorEventGetMessage;
import com.example.gears.events.SuccessEventEndGame;
import com.example.gears.events.SuccessEventGetBoard;
import com.example.gears.events.SuccessEventGetGame;
import com.example.gears.events.SuccessEventGetMessage;
import com.example.gears.events.SuccessEventInitBoard;
import com.example.gears.events.SuccessEventUpdateGame;
import com.example.gears.gameObjects.Board;
import com.example.gears.gameObjects.GameState;
import com.example.gears.gameObjects.Gear;
import com.example.gears.gameObjects.Message;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameActivity extends AppCompatActivity {
    private static final String FIRSTPLAYER = "FIRSTPLAYER";

    private final ArrayList<GearImage> gears = new ArrayList<>();
    private ImageView ballInRightGutter, ballInLeftGutter;
    private ImageView sticker1, sticker2, sticker3, sticker4;
    public EventBus eventBus = EventBus.getDefault();
    private Button endTurn;
    private String currentPlayer;
    private String token;
    private GameState gameState;
    private Board currentPlayerBoard, otherPlayerBoard;
    private Gson gson = new Gson();
    private int activeGearNum = -2;
    private String gameId;
    private Boolean needToAddToTurn = true;
    private int numberOfDrownGears = 0;
    private EditText countDown;
    private CountDownTimer countDownTimer;
    private List<ImageView> stickers = new ArrayList<>();
    private List<StickerButton> stickerButtons = new ArrayList<>();
    boolean gameIsEnded = false;
    private TextView leftGutter, rightGutter;


    private void getMessage() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_SEND_MESSAGE + gameId + "/player/" + currentPlayer,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
//                            eventBus.post(new SuccessEventGetMessage(obj));
                            gson = new Gson();
                            Message message = gson.fromJson(obj.toString(), Message.class);
                            for(ImageView stiker: stickers) {
                                stiker.setVisibility(View.INVISIBLE);
                            }
                            if (message.getMessage() == Message.MessageType.FIRSTTYPE) {
                                stickers.get(0).setVisibility(View.VISIBLE);
                            }
                            if (message.getMessage() == Message.MessageType.SECONDTYPE) {
                                stickers.get(1).setVisibility(View.VISIBLE);
                            }
                            if (message.getMessage() == Message.MessageType.THIRDTYPE) {
                                stickers.get(2).setVisibility(View.VISIBLE);
                            }
                            if (message.getMessage() == Message.MessageType.FOURTHTYPE) {
                                stickers.get(3).setVisibility(View.VISIBLE);
                            }
                            if (gameState == null || gameState.getCurrentGameState() == GameState.CurrentGameState.CONTINUE) {
                                getMessage();
                            } else if (gameState.getCurrentGameState() != GameState.CurrentGameState.CONTINUE) {
                                eventBus.post(new SuccessEventEndGame());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        for(ImageView sticker: stickers) {
                            sticker.setVisibility(View.INVISIBLE);
                        }
                        if (gameState == null || gameState.getCurrentGameState() == GameState.CurrentGameState.CONTINUE) {
                            getMessage();
                        } else if (gameState.getCurrentGameState() != GameState.CurrentGameState.CONTINUE) {
                            eventBus.post(new SuccessEventEndGame());
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("token", token);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void sendSticker(int stikerNum) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_SEND_MESSAGE + gameId + "/player/" + currentPlayer,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) { }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        eventBus.post(new ErrorEvent(error));
                    }
                }) {

            @Override
            public byte[] getBody() throws AuthFailureError {
                JSONObject toReturn = null;
                try {
                    if (stikerNum == 0) {
                        toReturn = new JSONObject(gson.toJson(new Message(Message.MessageType.FIRSTTYPE)));
                    }
                    if (stikerNum == 1) {
                        toReturn = new JSONObject(gson.toJson(new Message(Message.MessageType.SECONDTYPE)));
                    }
                    if (stikerNum == 2) {
                        toReturn = new JSONObject(gson.toJson(new Message(Message.MessageType.THIRDTYPE)));
                    }
                    if (stikerNum == 3) {
                        toReturn = new JSONObject(gson.toJson(new Message(Message.MessageType.FOURTHTYPE)));
                    }
                    if (toReturn != null) {
                        return toReturn.toString().getBytes();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("token", token);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void getBoard() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_GET_BOARD
                + gameId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            eventBus.post(new SuccessEventGetBoard(obj));
                        } catch (JSONException e) {
                            System.out.print("????????????1: ");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        eventBus.post(new ErrorEvent(error));
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("token", token);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void makeTimer() {
        countDownTimer = new CountDownTimer(50000, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                countDown.setText((int) (millisUntilFinished / 1000) + " seconds");
            }

            @Override
            public void onFinish() {
                countDown.setText("done!");
                activeGearNum = -1;
                updateGame(gameState);
            }
        };
    }

    private void endGame() {
        gameIsEnded = true;
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, URLs.URL_END_GAME + gameId + "/player/" + currentPlayer,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        eventBus.post(new SuccessEventEndGame());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        eventBus.post(new ErrorEvent(error));
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("token", token);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    private void initBoard() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_INIT_BOARD + gameId + "/player/" + currentPlayer,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        eventBus.post(new SuccessEventInitBoard());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        eventBus.post(new ErrorEvent(error));
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("token", token);
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                JSONObject toReturn;
                try {
                    toReturn = new JSONObject(gson.toJson(currentPlayerBoard));
                    return toReturn.toString().getBytes("utf-8");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }

    private void getGame() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_GET_GAME
                + gameId + "/player/" + currentPlayer,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            eventBus.post(new SuccessEventGetGame(obj));
                        } catch (JSONException e) {
                            System.out.print("????????????1: ");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        eventBus.post(new ErrorEvent(error));
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("token", token);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void updateGame(GameState gameStateToSend) {
        activeGearNum = -2;
        if (currentPlayerBoard.isAllBallsInPot() || otherPlayerBoard.isAllBallsInPot()) {
            if (currentPlayerBoard.isAllBallsInPot() && otherPlayerBoard.isAllBallsInPot()) {
                gameStateToSend.setCurrentGameState(GameState.CurrentGameState.DRAW);
            } else if (currentPlayerBoard.isAllBallsInPot()) {
                if (currentPlayer.equals(FIRSTPLAYER)) {
                    gameStateToSend.setCurrentGameState(GameState.CurrentGameState.FIRSTPLAYER);
                } else {
                    gameStateToSend.setCurrentGameState(GameState.CurrentGameState.SECONDPLAYER);
                }
            } else {
                if (currentPlayer.equals(FIRSTPLAYER)) {
                    gameStateToSend.setCurrentGameState(GameState.CurrentGameState.SECONDPLAYER);
                } else {
                    gameStateToSend.setCurrentGameState(GameState.CurrentGameState.FIRSTPLAYER);
                }
            }
        }
        if (currentPlayer.equals(FIRSTPLAYER)) {
            gameStateToSend.setCurrentPlayer(GameState.CurrentPlayer.SECONDPLAYER);
        } else {
            gameStateToSend.setCurrentPlayer(GameState.CurrentPlayer.FIRSTPLAYER);
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_UPDATE_GAME + "/" + gameId + "/player/" + currentPlayer,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        eventBus.post(new SuccessEventUpdateGame());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        eventBus.post(new ErrorEvent(error));

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("token", token);
                return params;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("currentPlayer", currentPlayer);
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                JSONObject toReturn;
                try {
                    toReturn = new JSONObject(gson.toJson(gameStateToSend));
                    return toReturn.toString().getBytes("utf-8");
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return null;
            }

        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        currentPlayerBoard = new Board();
        if (currentPlayer.equals(FIRSTPLAYER)) {
            currentPlayerBoard.getPot().setDegree(240);
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        initGameState();
        setListeners();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onBackPressed() {
        endGame();
        super.onBackPressed();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSuccessEventEndGame(SuccessEventEndGame event) {
        finish();
        startActivity(new Intent(getApplicationContext(), NavigationActivity.class));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSuccessEventGetMessage(SuccessEventGetMessage event) {
        if (gameIsEnded) {
            eventBus.post(new SuccessEventEndGame());
        }
        gson = new Gson();
        Message message = gson.fromJson(event.getResponse().toString(), Message.class);
        for(ImageView stiker: stickers) {
            stiker.setVisibility(View.INVISIBLE);
        }
        if (message.getMessage() == Message.MessageType.FIRSTTYPE) {
            stickers.get(0).setVisibility(View.VISIBLE);
        }
        if (message.getMessage() == Message.MessageType.SECONDTYPE) {
            stickers.get(1).setVisibility(View.VISIBLE);
        }
        if (message.getMessage() == Message.MessageType.THIRDTYPE) {
            stickers.get(2).setVisibility(View.VISIBLE);
        }
        if (message.getMessage() == Message.MessageType.FOURTHTYPE) {
            stickers.get(3).setVisibility(View.VISIBLE);
        }
        if (gameState == null || gameState.getCurrentGameState() == GameState.CurrentGameState.CONTINUE) {
            getMessage();
        } else if (gameState.getCurrentGameState() != GameState.CurrentGameState.CONTINUE) {
            eventBus.post(new SuccessEventEndGame());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErrorEventGetMessage(ErrorEventGetMessage event) {
        if (gameIsEnded) {
            finish();
            startActivity(new Intent(getApplicationContext(), PersonalAccountActivity.class));
        }
        for(ImageView stiker: stickers) {
            stiker.setVisibility(View.INVISIBLE);
        }
        if (gameState == null || gameState.getCurrentGameState() == GameState.CurrentGameState.CONTINUE) {
            getMessage();
        } else if (gameState.getCurrentGameState() != GameState.CurrentGameState.CONTINUE) {
            finish();
            startActivity(new Intent(getApplicationContext(), PersonalAccountActivity.class));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSuccessEventGetBoard(SuccessEventGetBoard event) {
        gson = new Gson();
        int diff = 90;
        GameState tmpGameState = gson.fromJson(event.getResponse().toString(), GameState.class);
        for (int i = 0; i < 5; i++) {
            int angle = tmpGameState.getFirstPlayerBoard().getGears().get(i).getDegree() + diff;
            rotateDialer(gears.get(i), angle);
            gears.get(i).gear.setDegree(angle);
        }
        initBoard();
        Toast.makeText(getApplicationContext(), "Waiting for another player turn", Toast.LENGTH_SHORT).show();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSuccessEventInitBoard(SuccessEventInitBoard event) {
        rightGutter.setText(String.valueOf(currentPlayerBoard.getRightGutter().getHowManyBalls()));
        leftGutter.setText(String.valueOf(currentPlayerBoard.getLeftGutter().getHowManyBalls()));
        if (currentPlayer.equals(FIRSTPLAYER)) {
            getGame();
            getMessage();
        } else {
            getMessage();
            getGame();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSuccessEventGetGame(SuccessEventGetGame event) {
        gson = new Gson();
        gameState = gson.fromJson(event.getResponse().toString(), GameState.class);
        if (currentPlayer.equals(FIRSTPLAYER)) {
            currentPlayerBoard = gameState.getFirstPlayerBoard();
            otherPlayerBoard = new Board(gameState.getSecondPlayerBoard());
        } else {
            currentPlayerBoard = gameState.getSecondPlayerBoard();
            otherPlayerBoard = new Board(gameState.getFirstPlayerBoard());
        }
        for (int i = 0; i < currentPlayerBoard.getGears().size(); i++) {
            gears.get(i).setGearWithHoles(currentPlayerBoard.getGears().get(i));
        }

        List<Integer> degreesFromTurn = gameState.getTurn().getDegree();
        if (degreesFromTurn != null) {
            needToAddToTurn = false;
            activeGearNum = gameState.getTurn().getNumberOfActiveGear();
            for (Integer degree : degreesFromTurn) {
                rotateWithMonitoringBalls(degree);
            }
        }

        if (gameState.getCurrentGameState() != GameState.CurrentGameState.CONTINUE) {
            if (gameState.getCurrentGameState() == GameState.CurrentGameState.DRAW) {
                Toast.makeText(getApplicationContext(), "Draw!", Toast.LENGTH_SHORT).show();
                endGame();
                return;
            }
            if (gameState.getCurrentGameState() == GameState.CurrentGameState.FIRSTPLAYER) {
                if (currentPlayer.equals(FIRSTPLAYER)) {
                    Toast.makeText(getApplicationContext(), "You won!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "You lose", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (currentPlayer.equals(FIRSTPLAYER)) {
                    Toast.makeText(getApplicationContext(), "You lose", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "You won", Toast.LENGTH_SHORT).show();
                }
            }
            endGame();
            return;
        }
        needToAddToTurn = true;
        activeGearNum = -1;
        gameState.setTurn(gameState.new Turn());
        Toast.makeText(getApplicationContext(), "Now it's your turn, you have 50 seconds", Toast.LENGTH_SHORT).show();
        countDownTimer.start();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSuccessEventUpdateGame(SuccessEventUpdateGame event) {
        if (gameState.getCurrentGameState() != GameState.CurrentGameState.CONTINUE) {
            if (gameState.getCurrentGameState() == GameState.CurrentGameState.DRAW) {
                Toast.makeText(getApplicationContext(), "Draw!", Toast.LENGTH_SHORT).show();
                endGame();
                return;
            }
            if (gameState.getCurrentGameState() == GameState.CurrentGameState.FIRSTPLAYER) {
                if (currentPlayer.equals(FIRSTPLAYER)) {
                    Toast.makeText(getApplicationContext(), "You won!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "You lose", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (currentPlayer.equals(FIRSTPLAYER)) {
                    Toast.makeText(getApplicationContext(), "You lose", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "You won", Toast.LENGTH_SHORT).show();
                }
            }
            endGame();
            return;
        }
        Toast.makeText(getApplicationContext(), "Waiting for another player turn", Toast.LENGTH_SHORT).show();
        getGame();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErrorEvent(ErrorEvent error) {
        System.out.println(error.getError().toString());
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentPlayer = SharedPrefManager.getInstance(this).getCurrentPlayerNum();
        token = SharedPrefManager.getInstance(this).getToken();

        if (currentPlayer.equals(FIRSTPLAYER)) {
            setContentView(R.layout.activity_game_field1);
        } else {
            setContentView(R.layout.activity_game_field2);
        }
        gameId = SharedPrefManager.getInstance(this).getGameId();

        gears.add(new GearImage(1, 1));
        gears.add(new GearImage(3, 2));
        gears.add(new GearImage(2, 3));
        gears.add(new GearImage(4, 4));
        gears.add(new GearImage(5, 5));



        for (GearImage gearImage: gears) {
            if (gearImage.chosenGearImage == null) {
                gearImage.chosenGearImage = BitmapFactory.decodeResource(getResources(), R.drawable.gear);
                for (HoleImage holeIm : gearImage.holes) {
                    holeIm.image = BitmapFactory.decodeResource(getResources(), R.drawable.hole);
                    holeIm.ball.image = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
                }
            }


            if (gearImage.matrix == null) {
                gearImage.matrix = new Matrix();
                for (HoleImage holeIm : gearImage.holes) {
                    holeIm.matrix = new Matrix();
                    holeIm.ball.matrix = new Matrix();
                }
            } else {
                gearImage.matrix.reset();
                 for (HoleImage holeIm : gearImage.holes) {
                     holeIm.ball.matrix.reset();
                 }
            }
        }

        initPlayerScreen();

        for (int i = 0; i < gears.size(); i++) {
            int finalI = i;
            gears.get(i).selectingButton.setOnClickListener(v -> {
                if (activeGearNum != -1) {
                    return;
                }
                countDownTimer.start();
                activeGearNum = gears.get(finalI).gearNumber - 1;
                gears.get(activeGearNum).notChosenGear.setVisibility(View.INVISIBLE);
                gameState.getTurn().setNumberOfActiveGear(activeGearNum);
            });
        }
        endTurn = findViewById(R.id.end_turn);
        endTurn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (activeGearNum < 0) {
                    return;
                }
                gears.get(activeGearNum).notChosenGear.setVisibility(View.VISIBLE);
                activeGearNum = -1;
                updateGame(gameState);
                countDownTimer.cancel();
                countDown.setText("done!");
            }
        });

        makeTimer();

        for (int i = 0; i < stickerButtons.size(); i++) {
            final int finalI = i;
            stickerButtons.get(finalI).button.setOnTouchListener((v, event) -> {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    sendSticker(finalI);
                    stickerButtons.get(finalI).bigImage.setVisibility(View.VISIBLE);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    stickerButtons.get(finalI).bigImage.setVisibility(View.INVISIBLE);
                }

                return true;
            });
        }
    }

    private void initPlayerScreen() {
        gears.get(0).dialer = findViewById(R.id.imageView_gear1);
        gears.get(1).dialer = findViewById(R.id.imageView_gear2);
        gears.get(2).dialer = findViewById(R.id.imageView_gear3);
        gears.get(3).dialer = findViewById(R.id.imageView_gear4);
        gears.get(4).dialer = findViewById(R.id.imageView_gear5);

        gears.get(0).holes.get(0).dialer = findViewById(R.id.imageView_gear1_hole1);
        gears.get(1).holes.get(0).dialer = findViewById(R.id.imageView_gear2_hole1);
        gears.get(1).holes.get(1).dialer = findViewById(R.id.imageView_gear2_hole2);
        gears.get(1).holes.get(2).dialer = findViewById(R.id.imageView_gear2_hole3);


        gears.get(2).holes.get(0).dialer = findViewById(R.id.imageView_gear3_hole1);
        gears.get(2).holes.get(1).dialer = findViewById(R.id.imageView_gear3_hole2);

        gears.get(3).holes.get(0).dialer = findViewById(R.id.imageView_gear4_hole1);
        gears.get(3).holes.get(1).dialer = findViewById(R.id.imageView_gear4_hole2);
        gears.get(3).holes.get(2).dialer = findViewById(R.id.imageView_gear4_hole3);
        gears.get(3).holes.get(3).dialer = findViewById(R.id.imageView_gear4_hole4);

        gears.get(4).holes.get(0).dialer = findViewById(R.id.imageView_gear5_hole1);
        gears.get(4).holes.get(1).dialer = findViewById(R.id.imageView_gear5_hole2);
        gears.get(4).holes.get(2).dialer = findViewById(R.id.imageView_gear5_hole3);
        gears.get(4).holes.get(3).dialer = findViewById(R.id.imageView_gear5_hole4);
        gears.get(4).holes.get(4).dialer = findViewById(R.id.imageView_gear5_hole5);

        gears.get(0).holes.get(0).ball.dialer = findViewById(R.id.imageView_gear1_ball1);
        gears.get(0).holes.get(0).ball.dialer.setVisibility(View.INVISIBLE);


        gears.get(1).holes.get(0).ball.dialer = findViewById(R.id.imageView_gear2_ball1);
        gears.get(1).holes.get(0).ball.dialer.setVisibility(View.INVISIBLE);
        gears.get(1).holes.get(1).ball.dialer = findViewById(R.id.imageView_gear2_ball2);
        gears.get(1).holes.get(1).ball.dialer.setVisibility(View.INVISIBLE);
        gears.get(1).holes.get(2).ball.dialer = findViewById(R.id.imageView_gear2_ball3);
        gears.get(1).holes.get(2).ball.dialer.setVisibility(View.INVISIBLE);

        gears.get(2).holes.get(0).ball.dialer = findViewById(R.id.imageView_gear3_ball1);
        gears.get(2).holes.get(0).ball.dialer.setVisibility(View.INVISIBLE);
        gears.get(2).holes.get(1).ball.dialer = findViewById(R.id.imageView_gear3_ball2);
        gears.get(2).holes.get(1).ball.dialer.setVisibility(View.INVISIBLE);


        gears.get(3).holes.get(0).ball.dialer = findViewById(R.id.imageView_gear4_ball1);
        gears.get(3).holes.get(0).ball.dialer.setVisibility(View.INVISIBLE);
        gears.get(3).holes.get(1).ball.dialer = findViewById(R.id.imageView_gear4_ball2);
        gears.get(3).holes.get(1).ball.dialer.setVisibility(View.INVISIBLE);
        gears.get(3).holes.get(2).ball.dialer = findViewById(R.id.imageView_gear4_ball3);
        gears.get(3).holes.get(2).ball.dialer.setVisibility(View.INVISIBLE);
        gears.get(3).holes.get(3).ball.dialer = findViewById(R.id.imageView_gear4_ball4);
        gears.get(3).holes.get(3).ball.dialer.setVisibility(View.INVISIBLE);

        gears.get(4).holes.get(0).ball.dialer = findViewById(R.id.imageView_gear5_ball1);
        gears.get(4).holes.get(0).ball.dialer.setVisibility(View.INVISIBLE);
        gears.get(4).holes.get(1).ball.dialer = findViewById(R.id.imageView_gear5_ball2);
        gears.get(4).holes.get(1).ball.dialer.setVisibility(View.INVISIBLE);
        gears.get(4).holes.get(2).ball.dialer = findViewById(R.id.imageView_gear5_ball3);
        gears.get(4).holes.get(2).ball.dialer.setVisibility(View.INVISIBLE);
        gears.get(4).holes.get(3).ball.dialer = findViewById(R.id.imageView_gear5_ball4);
        gears.get(4).holes.get(3).ball.dialer.setVisibility(View.INVISIBLE);
        gears.get(4).holes.get(4).ball.dialer = findViewById(R.id.imageView_gear5_ball5);
        gears.get(4).holes.get(4).ball.dialer.setVisibility(View.INVISIBLE);

        gears.get(0).selectingButton = findViewById(R.id.button1);
        gears.get(1).selectingButton = findViewById(R.id.button2);
        gears.get(2).selectingButton = findViewById(R.id.button3);
        gears.get(3).selectingButton = findViewById(R.id.button4);
        gears.get(4).selectingButton = findViewById(R.id.button5);

        ballInLeftGutter = findViewById(R.id.imageView_ball_left_gutter);
        ballInRightGutter = findViewById(R.id.imageView_ball_right_gutter);
        gears.get(0).notChosenGear = findViewById(R.id.imageView_gear1_chosen);
        gears.get(1).notChosenGear = findViewById(R.id.imageView_gear2_chosen);
        gears.get(2).notChosenGear = findViewById(R.id.imageView_gear3_chosen);
        gears.get(3).notChosenGear = findViewById(R.id.imageView_gear4_chosen);
        gears.get(4).notChosenGear = findViewById(R.id.imageView_gear5_chosen);

        countDown = findViewById(R.id.countdown);

        sticker1 = findViewById(R.id.stiker1);
        sticker1.setVisibility(View.INVISIBLE);
        stickers.add(sticker1);
        sticker2 = findViewById(R.id.stiker2);
        sticker2.setVisibility(View.INVISIBLE);
        stickers.add(sticker2);
        sticker3 = findViewById(R.id.stiker3);
        sticker3.setVisibility(View.INVISIBLE);
        stickers.add(sticker3);
        sticker4 = findViewById(R.id.stiker4);
        sticker4.setVisibility(View.INVISIBLE);
        stickers.add(sticker4);

        StickerButton stickerButton1 = new StickerButton();
        stickerButton1.button = findViewById(R.id.stiker_button1);
        stickerButton1.smallImage = findViewById(R.id.stiker1_small);
        stickerButton1.bigImage = findViewById(R.id.stiker1_big);
        stickerButton1.bigImage.setVisibility(View.INVISIBLE);
        stickerButtons.add(stickerButton1);

        StickerButton stickerButton2 = new StickerButton();
        stickerButton2.button = findViewById(R.id.stiker_button2);
        stickerButton2.smallImage = findViewById(R.id.stiker2_small);
        stickerButton2.bigImage = findViewById(R.id.stiker2_big);
        stickerButton2.bigImage.setVisibility(View.INVISIBLE);
        stickerButtons.add(stickerButton2);


        StickerButton stickerButton3 = new StickerButton();
        stickerButton3.button = findViewById(R.id.stiker_button3);
        stickerButton3.smallImage = findViewById(R.id.stiker3_small);
        stickerButton3.bigImage = findViewById(R.id.stiker3_big);
        stickerButton3.bigImage.setVisibility(View.INVISIBLE);
        stickerButtons.add(stickerButton3);


        StickerButton stickerButton4 = new StickerButton();
        stickerButton4.button = findViewById(R.id.stiker_button4);
        stickerButton4.smallImage = findViewById(R.id.stiker4_small);
        stickerButton4.bigImage = findViewById(R.id.stiker4_big);
        stickerButton4.bigImage.setVisibility(View.INVISIBLE);
        stickerButtons.add(stickerButton4);

        rightGutter = findViewById(R.id.right_gutter_text);
        leftGutter = findViewById(R.id.left_gutter_text);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        int gearNumber = 0;
        for (GearImage gear: gears) {
            gear.dialer.setOnTouchListener(new MyOnTouchListener(gear, gearNumber));
            gearNumber++;

            gear.dialer.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                if (gear.dialerHeight == 0 || gear.dialerWidth == 0) {
                    numberOfDrownGears++;
                    gear.dialerHeight = gear.dialer.getHeight();
                    gear.dialerWidth = gear.dialer.getWidth();
                    gear.radius = gear.dialerHeight / 2;
                    gear.gear.setRadius(gear.radius);
                    gear.gear.setXY((int)gear.dialer.getX(), (int)gear.dialer.getY());

                    System.out.println("!!!!!! " + gear.dialer.getLeft() + " " + gear.dialer.getTop() + " !!!!!!!!");


                    Matrix resize = new Matrix();
                    resize.postScale((float) Math.min(gear.dialerWidth, gear.dialerHeight) / (float) gear.chosenGearImage.getWidth(),
                            (float) Math.min(gear.dialerWidth, gear.dialerHeight) / (float) gear.chosenGearImage.getHeight());

                    gear.chosenGearImage = Bitmap.createBitmap(gear.chosenGearImage, 0, 0, gear.chosenGearImage.getWidth(), gear.chosenGearImage.getHeight(), resize, false);

                    for (HoleImage holeIm : gear.holes) {
                        holeIm.image = Bitmap.createScaledBitmap(holeIm.image, 150, 75, false);
                        holeIm.ball.image = Bitmap.createScaledBitmap(holeIm.ball.image, 75, 75, false);
                    }

                    gear.dialer.setImageBitmap(gear.chosenGearImage);
                    gear.dialer.setImageMatrix(gear.matrix);

                    for (HoleImage holeIm : gear.holes) {
                        holeIm.dialer.setImageBitmap(holeIm.image);
                        holeIm.dialer.setImageMatrix(holeIm.matrix);
                        holeIm.ball.dialer.setImageBitmap(holeIm.ball.image);
                        holeIm.ball.dialer.setImageMatrix(holeIm.ball.matrix);
                    }

                    for (HoleImage holeIm : gear.holes) {
                        Map.Entry<Double, Double> entry = getDxDyForHoles(gear, holeIm);
                        holeIm.matrix.postRotate(holeIm.degree, 150 / 2, 0);
                        holeIm.dialer.setImageMatrix(holeIm.matrix);
                        holeIm.matrix.postTranslate(entry.getKey().floatValue(), entry.getValue().floatValue());
                        holeIm.dialer.setImageMatrix(holeIm.matrix);
                        entry = getDxDyForBalls(gear, holeIm);

                        holeIm.ball.matrix.setTranslate(entry.getKey().floatValue(), entry.getValue().floatValue());
                        holeIm.ball.dialer.setImageMatrix(holeIm.ball.matrix);
                    }
                    if (numberOfDrownGears == 5) {
                        if (currentPlayer.equals(FIRSTPLAYER)) {
                            makeUniqueBoard();
                        } else {
                            getBoard();
                        }
                    }
                }
            });
        }

    }


    private Map.Entry<Double, Double> getDxDyForBalls(GearImage gear, HoleImage hole) {
        int degree = hole.degree;
        if (degree == 0) {
            return new AbstractMap.SimpleEntry<>(gear.radius + gear.radius* Math.sin(Math.toRadians(degree)) - hole.ball.image.getWidth() / 2,
                    gear.radius - (gear.radius) * Math.cos(Math.toRadians(degree)));
        }

        if (degree == 180) {
            return new AbstractMap.SimpleEntry<>(gear.radius + gear.radius* Math.sin(Math.toRadians(degree)) - hole.ball.image.getWidth() / 2,
                    gear.radius - (gear.radius) * Math.cos(Math.toRadians(degree)) - hole.ball.image.getWidth());
        }

        if (degree <= 90) {
            Double dx = gear.radius + gear.radius* Math.sin(Math.toRadians(degree)) - hole.ball.image.getWidth();
            Double dy = gear.radius - (gear.radius) * Math.cos(Math.toRadians(degree)) - hole.ball.image.getWidth() / 2;
            return new AbstractMap.SimpleEntry<>(dx, dy);
        }

        if (degree <= 180) {
            degree -= 90;
            Double dx = gear.radius + gear.radius * Math.cos(Math.toRadians(degree)) - hole.ball.image.getWidth();
            Double dy = gear.radius + gear.radius * Math.sin(Math.toRadians(degree)) - hole.ball.image.getWidth();
            return new AbstractMap.SimpleEntry<>(dx, dy);
        }
        if (degree < 270) {
            degree -= 180;
            Double dx = gear.radius - gear.radius * Math.sin(Math.toRadians(degree));
            Double dy = gear.radius + gear.radius * Math.cos(Math.toRadians(degree)) - hole.ball.image.getWidth();
            return new AbstractMap.SimpleEntry<>(dx, dy);
        }

        degree -= 270;
        Double dx = gear.radius - gear.radius * Math.cos(Math.toRadians(degree));
        Double dy = gear.radius - gear.radius * Math.sin(Math.toRadians(degree)) - hole.ball.image.getWidth() / 2;
        return new AbstractMap.SimpleEntry<>(dx, dy);
    }

    private Map.Entry<Double, Double> getDxDyForHoles(GearImage gear, HoleImage hole) {
        int degree = hole.degree;
        if (degree <= 90) {
            Double dx = gear.radius + (gear.radius) * Math.sin(Math.toRadians(degree)) - hole.image.getWidth() / 2;
            Double dy = gear.radius - (gear.radius) * Math.cos(Math.toRadians(degree));
            return new AbstractMap.SimpleEntry<>(dx, dy);
        }

        if (degree <= 180) {
            degree -= 90;
            Double dx = gear.radius + (gear.radius + 3) * Math.cos(Math.toRadians(degree)) - hole.image.getWidth() / 2;
            Double dy = gear.radius + (gear.radius + 3) * Math.sin(Math.toRadians(degree));
            return new AbstractMap.SimpleEntry<>(dx, dy);
        }
        if (degree <= 270) {
            degree -= 180;
            Double dx = gear.radius - (gear.radius) * Math.sin(Math.toRadians(degree))- hole.image.getWidth() / 2;
            Double dy = gear.radius + (gear.radius) * Math.cos(Math.toRadians(degree));
            return new AbstractMap.SimpleEntry<>(dx, dy);
        }

        degree -= 270;
        Double dx = gear.radius - (gear.radius) * Math.cos(Math.toRadians(degree)) - hole.image.getWidth() / 2;
        Double dy = gear.radius - (gear.radius) * Math.sin(Math.toRadians(degree));
        return new AbstractMap.SimpleEntry<>(dx, dy);
    }

    private void initGameState() {
        List<Gear> gearsToAddToBoard = new ArrayList<>();

        int gearNum = 1;
        for (GearImage gearImage: gears) {
            Gear newGear = new Gear(gearImage.getHolesNumber(), gearNum == 5, gearNum == 1,
                    GearImage.getDownNeighborsList(gearNum), GearImage.getUpperNeighborsList(gearNum));
            newGear.setRadius(gearImage.dialer.getLayoutParams().height / 2);
            gearsToAddToBoard.add(newGear);
            gearImage.setGear(newGear);
            gearImage.setNeighbours(newGear.getUpperNeighbours(), newGear.getDownNeighbours());
            gearImage.setHoles();
            gearNum++;
        }

        currentPlayerBoard.setGears(gearsToAddToBoard);
    }





    public class MyOnTouchListener implements View.OnTouchListener {
        private final int gearNum;
        private double startAngle;
        private final GearImage gearImage;

        public MyOnTouchListener(GearImage gearImage, int gearNum) {
            this.gearImage = gearImage;
            this.gearNum = gearNum;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (gearNum != activeGearNum) {
                return false;
            }

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN: // ???????????? ????????????????
                    startAngle = getAngle(event.getX(), event.getY());
                    break;

                case MotionEvent.ACTION_MOVE: // ?????????????????? ?????????????????? ?? ?????????????? ????????????????
                    double currentAngle = getAngle(event.getX(), event.getY());
                    if (Math.abs(currentAngle - startAngle) < 3) {
                        return true;
                    }

                    double angleToRotate = startAngle - currentAngle;
                    if (0 <= startAngle && startAngle <= 90 && 270 <= currentAngle && currentAngle <= 360) {
                        angleToRotate += 360;
                    }

                    if (0 <= currentAngle && currentAngle <= 90 && 270 <= startAngle && startAngle <= 360) {
                        angleToRotate -= 360;
                    }
                    rotateWithMonitoringBalls((float) angleToRotate);
                    startAngle = currentAngle;
                    break;


                case MotionEvent.ACTION_UP: // ???????????????? ??????????????????????

                    break;
            }

            return true;
        }

        private double getAngle(double xTouch, double yTouch) {
            double x = xTouch - (gearImage.dialerWidth / 2d);
            double y = gearImage.dialerHeight - yTouch - (gearImage.dialerHeight / 2d);

            switch (getQuadrant(x, y)) {
                case 1:
                    return Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
                case 2:
                    return 180 - Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
                case 3:
                    return 180 + (-1 * Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI);
                case 4:
                    return 360 + Math.asin(y / Math.hypot(x, y)) * 180 / Math.PI;
                default:
                    return 0;
            }
        }

        private int getQuadrant(double x, double y) {
            if (x >= 0) {
                return y >= 0 ? 1 : 4;
            } else {
                return y >= 0 ? 2 : 3;
            }
        }
    }



    private void rotateDialer(GearImage gearImage, float degrees) {
        gearImage.matrix.postRotate(degrees, gearImage.gear.getRadius(), gearImage.gear.getRadius());
        gearImage.dialer.setImageMatrix(gearImage.matrix);

        for (HoleImage holeIm : gearImage.holes) {
            holeIm.matrix.postRotate(degrees, gearImage.gear.getRadius(), gearImage.gear.getRadius());
            holeIm.dialer.setImageMatrix(holeIm.matrix);
            holeIm.ball.matrix.postRotate(degrees, gearImage.gear.getRadius(), gearImage.gear.getRadius());
            holeIm.ball.dialer.setImageMatrix(holeIm.ball.matrix);
        }
    }


    private void rotateWithMonitoringBalls(float degree) {
        System.out.println("!!!!!!! " + gears.get(0).gear.getDegree() + " " + gears.get(1).gear.getDegree() + " "
                + gears.get(2).gear.getDegree() + " " + gears.get(3).gear.getDegree() + " " + gears.get(4).gear.getDegree() + " !!!!!!!!!!!!!!!!!!!");
        double step = 2.5;
        if (degree < 0) {
            step *= -1;
        }
        for (int i = 0; i < (int) (Math.abs(degree) / Math.abs(step)); i++) {
            gears.get(activeGearNum).accumulatedAngle = gears.get(activeGearNum).accumulatedAngle + step;
            rotateDialer(gears.get(activeGearNum), (float) step);
            if (step > 0 && (gears.get(activeGearNum).accumulatedAngle == 10)) {
                redraw(10);
                gears.get(activeGearNum).accumulatedAngle = 0;
            }
            if (step < 0 && (gears.get(activeGearNum).accumulatedAngle == -10)) {
                redraw(-10);
                gears.get(activeGearNum).accumulatedAngle = 0;
            }
        }
    }

    private void redraw(int degree) {
        if (needToAddToTurn) {
            gameState.getTurn().addDegreeToArrayDegree(-degree);
        }
        currentPlayerBoard.rebuild(degree, activeGearNum);

        List<Integer> allGearsToRedraw = new ArrayList<>();
        allGearsToRedraw.add(activeGearNum);
        allGearsToRedraw.addAll(gears.get(activeGearNum).getUpperNeighbours());
        allGearsToRedraw.addAll(gears.get(activeGearNum).getDownNeighbours());
        for (Integer gearNumber : allGearsToRedraw) {
            GearImage gearImage = gears.get(gearNumber);
            for (HoleImage holeImage : gearImage.getHoles()) {
                if (holeImage.hole.isFree()) {
                    holeImage.ball.dialer.setVisibility(View.INVISIBLE);
                } else {
                    holeImage.ball.dialer.setVisibility(View.VISIBLE);
                }
            }
        }
        rightGutter.setText(String.valueOf(currentPlayerBoard.getRightGutter().getHowManyBalls()));
        leftGutter.setText(String.valueOf(currentPlayerBoard.getLeftGutter().getHowManyBalls()));
        if (currentPlayerBoard.getLeftGutter().getHowManyBalls() == 0) {
            ballInLeftGutter.setVisibility(View.INVISIBLE);
        }

        if (currentPlayerBoard.getRightGutter().getHowManyBalls() == 0) {
            ballInRightGutter.setVisibility(View.INVISIBLE);
        }

        if (needToAddToTurn) {
            otherPlayerBoard.rebuild(-degree, activeGearNum);
        }
    }

    public void makeUniqueBoard() {
        int angle;
        for (int i = 0; i < 5; i++) {
            angle = (int) (Math.random() * 360);
            angle = (angle / 10) * 10;
            rotateDialer(gears.get(i), angle);
            gears.get(i).gear.setDegree(angle);
        }
        initBoard();
    }

}
