package com.example.gears;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.gears.events.SuccessEventGetBoard;
import com.example.gears.events.SuccessEventGetGame;
import com.example.gears.events.SuccessEventInitBoard;
import com.example.gears.events.SuccessEventUpdateGame;
import com.example.gears.gameObjects.Board;
import com.example.gears.gameObjects.GameState;
import com.example.gears.gameObjects.Gear;
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
//    Dialog dialog = new Dialog(GameActivity.this);
    private final ArrayList<GearImage> gears = new ArrayList<>();
    ImageView ballInRightGutter, ballInLeftGutter;
    EventBus eventBus = EventBus.getDefault();
    Button endTurn;
    String currentPlayer;
    String token;
    private GameState gameState;
    private Board currentPlayerBoard, otherPlayerBoard;
    private Gson gson = new Gson();
    private int activeGearNum = -2;
    String gameId;
    Boolean needToAddToTurn = true;
    int numberOfDrownGears = 0;


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
                            System.out.print("ОШИБКА1: ");
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
//                params.put("Content-Type", "application/json");
                params.put("token", token);
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void endGame() {
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, URLs.URL_END_GAME + gameId + "/player/" + currentPlayer,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        finish();
                        startActivity(new Intent(getApplicationContext(), PersonalAccountActivity.class));
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
                            System.out.print("ОШИБКА1: ");
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
//                params.put("Content-Type", "application/json");
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
                if (currentPlayer.equals("FIRSTPLAYER")) {
                    gameStateToSend.setCurrentGameState(GameState.CurrentGameState.FIRSTPLAYER);
                } else {
                    gameStateToSend.setCurrentGameState(GameState.CurrentGameState.SECONDPLAYER);
                }
            } else {
                if (currentPlayer.equals("FIRSTPLAYER")) {
                    gameStateToSend.setCurrentGameState(GameState.CurrentGameState.SECONDPLAYER);
                } else {
                    gameStateToSend.setCurrentGameState(GameState.CurrentGameState.FIRSTPLAYER);
                }
            }
        }
        if (currentPlayer.equals("FIRSTPLAYER")) {
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
        if (currentPlayer.equals("FIRSTPLAYER")) {
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
        getGame();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSuccessEventGetGame(SuccessEventGetGame event) {
        gson = new Gson();
        gameState = gson.fromJson(event.getResponse().toString(), GameState.class);
        if (currentPlayer.equals("FIRSTPLAYER")) {
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
            }
            if (gameState.getCurrentGameState() == GameState.CurrentGameState.FIRSTPLAYER) {
                if (currentPlayer.equals("FIRSTPLAYER")) {
                    Toast.makeText(getApplicationContext(), "You won!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "You lose", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (currentPlayer.equals("FIRSTPLAYER")) {
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
        Toast.makeText(getApplicationContext(), "Now it's your turn", Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSuccessEventUpdateGame(SuccessEventUpdateGame event) {
        if (gameState.getCurrentGameState() != GameState.CurrentGameState.CONTINUE) {
            if (gameState.getCurrentGameState() == GameState.CurrentGameState.DRAW) {
                Toast.makeText(getApplicationContext(), "Draw!", Toast.LENGTH_SHORT).show();
            }
            if (gameState.getCurrentGameState() == GameState.CurrentGameState.FIRSTPLAYER) {
                if (currentPlayer.equals("FIRSTPLAYER")) {
                    Toast.makeText(getApplicationContext(), "You won!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "You lose", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (currentPlayer.equals("FIRSTPLAYER")) {
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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        currentPlayer = "SECONDPLAYER";
//        currentPlayer = "FIRSTPLAYER";
        currentPlayer = SharedPrefManager.getInstance(this).getCurrentPlayerNum();
//        token = SharedPrefManager.getInstance(this).getToken();

        if (currentPlayer.equals("FIRSTPLAYER")) {
            setContentView(R.layout.activity_game_field1);
        } else {
            setContentView(R.layout.activity_game_field2);
        }
        gameId = SharedPrefManager.getInstance(this).getGameId();
//        gameId = "7594281976693506295";
//        currentPlayer = SharedPrefManager.getInstance(this).getCurrentPlayerNum();
//        currentPlayer = "FIRSTPLAYER";

        gears.add(new GearImage(1, 1));
        gears.add(new GearImage(3, 2));
        gears.add(new GearImage(2, 3));
        gears.add(new GearImage(4, 4));
        gears.add(new GearImage(5, 5));



        for (GearImage gearImage: gears) {
            if (gearImage.image == null) {
                gearImage.image = BitmapFactory.decodeResource(getResources(), R.drawable.gear);
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

        initFirstPlayerScreen();

        for (int i = 0; i < gears.size(); i++) {
            int finalI = i;
            gears.get(i).selectingButton.setOnClickListener(v -> {
//                if (activeGearNum != -1) {
//                    return;
//                }
                activeGearNum = gears.get(finalI).gearNumber - 1;
//                gameState.getTurn().setNumberOfActiveGear(activeGearNum);
            });
        }
        endTurn = findViewById(R.id.end_turn);
        endTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), PersonalAccountActivity.class));
//                if (activeGearNum < 0) {
//                    return;
//                }
//                activeGearNum = -1;
//                updateGame(gameState);
            }
        });
    }

    private void initFirstPlayerScreen() {
        gears.get(0).dialer = (ImageView) findViewById(R.id.imageView_gear1);
        gears.get(1).dialer = (ImageView) findViewById(R.id.imageView_gear2);
        gears.get(2).dialer = (ImageView) findViewById(R.id.imageView_gear3);
        gears.get(3).dialer = (ImageView) findViewById(R.id.imageView_gear4);
        gears.get(4).dialer = (ImageView) findViewById(R.id.imageView_gear5);


        gears.get(0).holes.get(0).dialer = findViewById(R.id.imageView_gear1_hole1);
        gears.get(0).dialer.setVisibility(View.VISIBLE);


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
                    resize.postScale((float) Math.min(gear.dialerWidth, gear.dialerHeight) / (float) gear.image.getWidth(),
                            (float) Math.min(gear.dialerWidth, gear.dialerHeight) / (float) gear.image.getHeight());

                    gear.image = Bitmap.createBitmap(gear.image, 0, 0, gear.image.getWidth(), gear.image.getHeight(), resize, false);

                    for (HoleImage holeIm : gear.holes) {
                        holeIm.image = Bitmap.createScaledBitmap(holeIm.image, 150, 75, false);
                        holeIm.ball.image = Bitmap.createScaledBitmap(holeIm.ball.image, 75, 75, false);
                    }

                    gear.dialer.setImageBitmap(gear.image);
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
                        makeUniqueBoard();
//                        if (currentPlayer.equals("FIRSTPLAYER")) {
//                            makeUniqueBoard();
//                        } else {
//                            getBoard();
//                        }
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
            Double dx = gear.radius + (gear.radius - 5) * Math.sin(Math.toRadians(degree)) - hole.image.getWidth() / 2;
            Double dy = gear.radius - (gear.radius - 5) * Math.cos(Math.toRadians(degree));
            return new AbstractMap.SimpleEntry<>(dx, dy);
        }

        if (degree <= 180) {
            degree -= 90;
            Double dx = gear.radius + (gear.radius - 5) * Math.cos(Math.toRadians(degree)) - hole.image.getWidth() / 2;
            Double dy = gear.radius + (gear.radius - 5) * Math.sin(Math.toRadians(degree));
            return new AbstractMap.SimpleEntry<>(dx, dy);
        }
        if (degree <= 270) {
            degree -= 180;
            Double dx = gear.radius - (gear.radius - 5) * Math.sin(Math.toRadians(degree))- hole.image.getWidth() / 2;
            Double dy = gear.radius + (gear.radius - 5) * Math.cos(Math.toRadians(degree));
            return new AbstractMap.SimpleEntry<>(dx, dy);
        }

        degree -= 270;
        Double dx = gear.radius - (gear.radius - 5) * Math.cos(Math.toRadians(degree)) - hole.image.getWidth() / 2;
        Double dy = gear.radius - (gear.radius - 5) * Math.sin(Math.toRadians(degree));
        return new AbstractMap.SimpleEntry<>(dx, dy);
    }

    private void initGameState() {
        List<Gear> gearsToAddToBoard = new ArrayList<>();

        int gearNum = 1;
        for (GearImage gearImage: gears) {
            Gear newGear = new Gear(gearImage.getHolesNumber(), gearNum == 5, gearNum == 1,
                    GearImage.getDownNeighborsList(gearNum), GearImage.getUpperNeighborsList(gearNum));
            newGear.setRadius((float) gearImage.dialer.getLayoutParams().height / 2);
            gearsToAddToBoard.add(newGear);
            gearImage.setGear(newGear);
            gearImage.setNeighbours(newGear.getUpperNeighbours(), newGear.getDownNeighbours());
            gearImage.setHoles();
            gearNum++;
        }

        currentPlayerBoard.setGears(gearsToAddToBoard);
    }





    private class MyOnTouchListener implements View.OnTouchListener {
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

                case MotionEvent.ACTION_DOWN: // начало действия
                    startAngle = getAngle(event.getX(), event.getY());
                    break;

                case MotionEvent.ACTION_MOVE: // произошло изменение в активом действии
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


                case MotionEvent.ACTION_UP: // действие закончилось

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
//        if (needToAddToTurn) {
//            gameState.getTurn().addDegreeToArrayDegree(-degree);
//        }
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
        if (currentPlayerBoard.getLeftGutter().getHowManyBalls() == 0) {
            ballInLeftGutter.setVisibility(View.INVISIBLE);
        }

        if (currentPlayerBoard.getRightGutter().getHowManyBalls() == 0) {
            ballInRightGutter.setVisibility(View.INVISIBLE);
        }

//        if (needToAddToTurn) {
//            otherPlayerBoard.rebuild(-degree, activeGearNum);
//        }
    }

    public void makeUniqueBoard() {
        int angle;
        for (int i = 0; i < 5; i++) {
            angle = (int) (Math.random() * 360);
            angle = (angle / 10) * 10;
            rotateDialer(gears.get(i), angle);
            gears.get(i).gear.setDegree(angle);
        }
//        initBoard();
    }

}
