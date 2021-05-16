package com.example.gears;

import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.gears.GameObjects.Board;
import com.example.gears.GameObjects.GameState;
import com.example.gears.GameObjects.Gear;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameActivity extends AppCompatActivity {
    private ArrayList<GearImage> gears = new ArrayList<>();
    private GameState gameState;
    private Board board;
    private Gson gson = new Gson();
    private int activeGearNum = 0;

    private void getGame(boolean firstGetGame) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URLs.URL_GET_GAME
                + "?id=5327300496048938450&token=hello&currentPlayer=FIRSTPLAYER",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (firstGetGame) {
                                EventBus.getDefault().post(new SuccessEventGetGameFirstTime(obj));
                            } else {
                                EventBus.getDefault().post(new SuccessEventGetGame(obj));
                            }
                        } catch (JSONException e) {
                            System.out.print("ОШИБКА1: ");
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        EventBus.getDefault().post(new ErrorEvent(error));
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", "5327300496048938450");
                params.put("token", "hello");
                params.put("currentPlayer", "FIRSTPLAYER");
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void updateGame(GameState gameStateToSend) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_UPDATE_GAME + "/5327300496048938450",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println(response);
                        EventBus.getDefault().post(new SuccessEventUpdateGame());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        EventBus.getDefault().post(new ErrorEvent(error));

                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("userToken", "hello");
                return params;
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                JSONObject toReturn = null;
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
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSuccessEventGetGameFirstTime(SuccessEventGetGameFirstTime event) {
        gson = new Gson();
        gameState = gson.fromJson(event.getResponse().toString(), GameState.class);
        gameState.setCurrentPlayer(GameState.CurrentPlayer.SECONDPLAYER);
        updateGame(gameState);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSuccessEventGetGame(SuccessEventGetGame event) {
        gson = new Gson();
        gameState = gson.fromJson(event.getResponse().toString(), GameState.class);
        updateGame(gameState);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSuccessEventUpdateGame(SuccessEventUpdateGame ) {
        getGame(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onErrorEvent(ErrorEvent error) {
        System.out.println(error.getError().toString());
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
//        initGameState();
        getGame(true);

    //        gears.add(new GearImage(1, 1));
    //        gears.add(new GearImage(3, 2));
    //        gears.add(new GearImage(2, 3));
    //        gears.add(new GearImage(4, 4));
    //        gears.add(new GearImage(5, 5));
    //
    //
    //
    //        for (GearImage gearImage: gears) {
    //            if (gearImage.image == null) {
    //                gearImage.image = BitmapFactory.decodeResource(getResources(), R.drawable.gear);
    //                for (HoleImage holeIm : gearImage.holes) {
    //                    holeIm.image = BitmapFactory.decodeResource(getResources(), R.drawable.hole);
    //                    holeIm.ball.image = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
    //                }
    //            }
    //
    //
    //            // initialize the matrix only once
    //            if (gearImage.matrix == null) {
    //                gearImage.matrix = new Matrix();
    //                for (HoleImage holeIm : gearImage.holes) {
    //                    holeIm.matrix = new Matrix();
    //                    holeIm.ball.matrix = new Matrix();
    //                }
    //            } else {
    //                gearImage.matrix.reset();
    //            }
    //        }
    //
    //        gears.get(0).dialer = findViewById(R.id.imageView_gear1);
    //        gears.get(1).dialer = findViewById(R.id.imageView_gear2);
    //        gears.get(2).dialer = findViewById(R.id.imageView_gear3);
    //        gears.get(3).dialer = findViewById(R.id.imageView_gear4);
    //        gears.get(4).dialer = findViewById(R.id.imageView_gear5);
    //
    //
    //        gears.get(0).holes.get(0).dialer = findViewById(R.id.imageView_gear1_hole1);
    //
    //        gears.get(1).holes.get(0).dialer = findViewById(R.id.imageView_gear2_hole1);
    //        gears.get(1).holes.get(1).dialer = findViewById(R.id.imageView_gear2_hole2);
    //        gears.get(1).holes.get(2).dialer = findViewById(R.id.imageView_gear2_hole3);
    //
    //        gears.get(2).holes.get(0).dialer = findViewById(R.id.imageView_gear3_hole1);
    //        gears.get(2).holes.get(1).dialer = findViewById(R.id.imageView_gear3_hole2);
    //
    //        gears.get(3).holes.get(0).dialer = findViewById(R.id.imageView_gear4_hole1);
    //        gears.get(3).holes.get(1).dialer = findViewById(R.id.imageView_gear4_hole2);
    //        gears.get(3).holes.get(2).dialer = findViewById(R.id.imageView_gear4_hole3);
    //        gears.get(3).holes.get(3).dialer = findViewById(R.id.imageView_gear4_hole4);
    //
    //        gears.get(4).holes.get(0).dialer = findViewById(R.id.imageView_gear5_hole1);
    //        gears.get(4).holes.get(1).dialer = findViewById(R.id.imageView_gear5_hole2);
    //        gears.get(4).holes.get(2).dialer = findViewById(R.id.imageView_gear5_hole3);
    //        gears.get(4).holes.get(3).dialer = findViewById(R.id.imageView_gear5_hole4);
    //        gears.get(4).holes.get(4).dialer = findViewById(R.id.imageView_gear5_hole5);
    //
    //        gears.get(0).holes.get(0).ball.dialer = findViewById(R.id.imageView_gear1_ball1);
    //        gears.get(0).holes.get(0).ball.dialer.setVisibility(View.INVISIBLE);
    //
    //
    //        gears.get(1).holes.get(0).ball.dialer = findViewById(R.id.imageView_gear2_ball1);
    //        gears.get(1).holes.get(0).ball.dialer.setVisibility(View.INVISIBLE);
    //        gears.get(1).holes.get(1).ball.dialer = findViewById(R.id.imageView_gear2_ball2);
    //        gears.get(1).holes.get(1).ball.dialer.setVisibility(View.INVISIBLE);
    //        gears.get(1).holes.get(2).ball.dialer = findViewById(R.id.imageView_gear2_ball3);
    //        gears.get(1).holes.get(2).ball.dialer.setVisibility(View.INVISIBLE);
    //
    //        gears.get(2).holes.get(0).ball.dialer = findViewById(R.id.imageView_gear3_ball1);
    //        gears.get(2).holes.get(0).ball.dialer.setVisibility(View.INVISIBLE);
    //        gears.get(2).holes.get(1).ball.dialer = findViewById(R.id.imageView_gear3_ball2);
    //        gears.get(2).holes.get(1).ball.dialer.setVisibility(View.INVISIBLE);
    //
    //
    //        gears.get(3).holes.get(0).ball.dialer = findViewById(R.id.imageView_gear4_ball1);
    //        gears.get(3).holes.get(0).ball.dialer.setVisibility(View.INVISIBLE);
    //        gears.get(3).holes.get(1).ball.dialer = findViewById(R.id.imageView_gear4_ball2);
    //        gears.get(3).holes.get(1).ball.dialer.setVisibility(View.INVISIBLE);
    //        gears.get(3).holes.get(2).ball.dialer = findViewById(R.id.imageView_gear4_ball3);
    //        gears.get(3).holes.get(2).ball.dialer.setVisibility(View.INVISIBLE);
    //        gears.get(3).holes.get(3).ball.dialer = findViewById(R.id.imageView_gear4_ball4);
    //        gears.get(3).holes.get(3).ball.dialer.setVisibility(View.INVISIBLE);
    //
    //        gears.get(4).holes.get(0).ball.dialer = findViewById(R.id.imageView_gear5_ball1);
    //        gears.get(4).holes.get(0).ball.dialer.setVisibility(View.INVISIBLE);
    //        gears.get(4).holes.get(1).ball.dialer = findViewById(R.id.imageView_gear5_ball2);
    //        gears.get(4).holes.get(1).ball.dialer.setVisibility(View.INVISIBLE);
    //        gears.get(4).holes.get(2).ball.dialer = findViewById(R.id.imageView_gear5_ball3);
    //        gears.get(4).holes.get(2).ball.dialer.setVisibility(View.INVISIBLE);
    //        gears.get(4).holes.get(3).ball.dialer = findViewById(R.id.imageView_gear5_ball4);
    //        gears.get(4).holes.get(3).ball.dialer.setVisibility(View.INVISIBLE);
    //        gears.get(4).holes.get(4).ball.dialer = findViewById(R.id.imageView_gear5_ball5);
    //        gears.get(4).holes.get(4).ball.dialer.setVisibility(View.INVISIBLE);
    //
    //        gears.get(0).selectingButton = findViewById(R.id.button1);
    //        gears.get(1).selectingButton = findViewById(R.id.button2);
    //        gears.get(2).selectingButton = findViewById(R.id.button3);
    //        gears.get(3).selectingButton = findViewById(R.id.button4);
    //        gears.get(4).selectingButton = findViewById(R.id.button5);
    //
    //        for (int i = 0; i < gears.size(); i++) {
    //            int finalI = i;
    //            gears.get(i).selectingButton.setOnClickListener(new View.OnClickListener() {
    //
    //                @Override
    //                public void onClick(View v) {
    //                    activeGearNum = gears.get(finalI).gearNumber - 1;
    //                }
    //            });
    //        }
    //
    //
    //        initGameState();
    //
    //        int gearNumber = 0;
    //        for (GearImage gear: gears) {
    //            gear.dialer.setOnTouchListener(new MyOnTouchListener(gear, gearNumber));
    //            gearNumber++;
    //            gear.dialer.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
    //                if (gear.dialerHeight == 0 || gear.dialerWidth == 0) {
    //                    gear.dialerHeight = gear.dialer.getHeight();
    //                    gear.dialerWidth = gear.dialer.getWidth();
    //                    gear.radius = gear.dialerHeight / 2;
    //
    //                    // resize
    //                    Matrix resize = new Matrix();
    //                    resize.postScale((float) Math.min(gear.dialerWidth, gear.dialerHeight) / (float) gear.image.getWidth(),
    //                            (float) Math.min(gear.dialerWidth, gear.dialerHeight) / (float) gear.image.getHeight());
    //
    //                    gear.image = Bitmap.createBitmap(gear.image, 0, 0, gear.image.getWidth(), gear.image.getHeight(), resize, false);
    //
    //                    for (HoleImage holeIm : gear.holes) {
    //                        holeIm.image = Bitmap.createScaledBitmap(holeIm.image, 150, 150, false);
    //                        holeIm.ball.image = Bitmap.createScaledBitmap(holeIm.ball.image, 75, 75, false);
    //                    }
    //                    int i = 0;
    //                    for (HoleImage holeIm : gear.holes) {
    //                        if (i == 1) {
    //                            System.out.println(10);
    //                        }
    //                        Map.Entry<Double, Double> entry = getDxDy(gear, holeIm);
    //                        holeIm.matrix.setTranslate(entry.getKey().floatValue(), entry.getValue().floatValue());
    //                        holeIm.ball.matrix.setTranslate(entry.getKey().floatValue() + 75 / 2, entry.getValue().floatValue() + 75);
    //                        i++;
    //                    }
    //
    //                    gear.dialer.setImageBitmap(gear.image);
    //                    gear.dialer.setImageMatrix(gear.matrix);
    //
    //                    for (HoleImage holeIm : gear.holes) {
    //                        holeIm.dialer.setImageBitmap(holeIm.image);
    //                        holeIm.dialer.setImageMatrix(holeIm.matrix);
    //                        holeIm.ball.dialer.setImageBitmap(holeIm.ball.image);
    //                        holeIm.ball.dialer.setImageMatrix(holeIm.ball.matrix);
    //                    }
    //                }
    //            });
    //        }



    }

    private Map.Entry<Double, Double> getDxDy( GearImage gear, HoleImage hole) {
        Integer degree = hole.degree;
        if (degree <= 90) {
            Double dx = gear.radius + gear.radius * Math.sin(Math.toRadians(degree)) - hole.image.getWidth() / 2;
            Double dy = gear.radius - gear.radius * Math.cos(Math.toRadians(degree)) - hole.image.getWidth() / 2;
            return new AbstractMap.SimpleEntry<>(dx, dy);
        }

        if (degree <= 180) {
            degree -= 90;
            Double dx = gear.radius + gear.radius * Math.cos(Math.toRadians(degree)) - hole.image.getWidth() / 2;
            Double dy = gear.radius + gear.radius * Math.sin(Math.toRadians(degree)) - hole.image.getWidth() / 2;
            return new AbstractMap.SimpleEntry<>(dx, dy);
        }
        if (degree <= 270) {
            degree -= 180;
            Double dx = gear.radius - gear.radius * Math.sin(Math.toRadians(degree)) - hole.image.getWidth() / 2;
            Double dy = gear.radius + gear.radius * Math.cos(Math.toRadians(degree)) - hole.image.getWidth() / 2;
            return new AbstractMap.SimpleEntry<>(dx, dy);
        }

        degree -= 270;
        Double dx = gear.radius - gear.radius * Math.cos(Math.toRadians(degree)) - hole.image.getWidth() / 2;
        Double dy = gear.radius - gear.radius * Math.sin(Math.toRadians(degree)) - hole.image.getWidth() / 2;
        return new AbstractMap.SimpleEntry<>(dx, dy);
    }


    private void initGameState() {
        gameState = new GameState();
        ArrayList<Gear> gearsToAddToBoard = new ArrayList<>();
        int gearNum = 1;
        for (GearImage gearImage: gears) {
            Gear newGear = new Gear(gearImage.getHolesNumber(), gearNum == 5, gearNum == 1,
                    GearImage.getDownNeighborsList(gearNum), GearImage.getUpperNeighborsList(gearNum));
            gearsToAddToBoard.add(newGear);
            gearImage.setGear(newGear);
            gearImage.setNeighbours(newGear.getUpperNeighbours(), newGear.getDownNeighbours());
            gearImage.setHoles();
            gearNum++;
        }
        gameState.getFirstPlayerBoard().setGears(gearsToAddToBoard);
        board = gameState.getFirstPlayerBoard();
    }

    private class MyOnTouchListener implements View.OnTouchListener {
        private int gearNum;

        private double startAngle;
        private GearImage gearImage;

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
                    if (Math.abs(startAngle - currentAngle) < 5) {
                        return true;
                    }
                    rotateWithMonitoringBalls((float) (startAngle - currentAngle));
//                    rotateDialer((float) (startAngle - currentAngle));
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



        private void rotateDialer(float degrees) {
            gearImage.matrix.postRotate(degrees, gearImage.dialerWidth / 2, gearImage.dialerHeight / 2);
            gearImage.dialer.setImageMatrix(gearImage.matrix);

            for (HoleImage holeIm : gearImage.holes) {
                holeIm.matrix.postRotate(degrees, gearImage.dialerWidth / 2, gearImage.dialerHeight / 2);
                holeIm.dialer.setImageMatrix(holeIm.matrix);
                holeIm.ball.matrix.postRotate(degrees, gearImage.dialerWidth / 2, gearImage.dialerHeight / 2);
                holeIm.ball.dialer.setImageMatrix(holeIm.ball.matrix);
            }
        }


        private void rotateWithMonitoringBalls(float degree) {
//            if (degree > 0) {
//                rotateDialer(degree);
//                return;
//            }
            double step = 2.5;
            float degreesToRotateOneTime = degree / (float) step;
            if (degree < 0) {
                step *= -1;
            }
            for (int i = 0; i < (int) (Math.abs(degree) / Math.abs(step)); i++) {
                gears.get(activeGearNum).accumulatedAngle = gears.get(activeGearNum).accumulatedAngle + step;
                rotateDialer((float) step);
                if (step > 0 && (gears.get(activeGearNum).accumulatedAngle == 10 || gears.get(activeGearNum).accumulatedAngle == 0)) {
                    redraw(10);
                    gears.get(activeGearNum).accumulatedAngle = 0;
                }
                if (step < 0 && (gears.get(activeGearNum).accumulatedAngle == -10 || gears.get(activeGearNum).accumulatedAngle == 0)) {
                    redraw(-10);
                    gears.get(activeGearNum).accumulatedAngle = 0;
                }
            }
        }
    }

    private void redraw(int degree) {
        board.rebuild(degree, activeGearNum);
        gears.get(activeGearNum).setGear(board.getGears().get(activeGearNum));
        for(Integer neighbor: gears.get(activeGearNum).getUpperNeighbours()) {
            gears.get(neighbor).setGear(board.getGears().get(neighbor));
        }

        for(Integer neighbor: gears.get(activeGearNum).getDownNeighbours()) {
            gears.get(neighbor).setGear(board.getGears().get(neighbor));
        }
//
//        for (Integer neighbor: gears.get(activeGearNum).getNeighbours()) {
//            if (neighbor == -1) {
//                continue;
//            }
//            gears.get(neighbor).setGear(board.getGears().get(neighbor));
//        }
        List<Integer> allGearsToRedraw = new ArrayList<>();
        allGearsToRedraw.add(activeGearNum);
        allGearsToRedraw.addAll(gears.get(activeGearNum).getUpperNeighbours());
        allGearsToRedraw.addAll(gears.get(activeGearNum).getDownNeighbours());
        for (Integer gearNumber: allGearsToRedraw) {
//            if (gearNumber == -1) {
//                continue;
//            }
            GearImage gearImage = gears.get(gearNumber);
            for (HoleImage holeImage: gearImage.getHoles()) {
                if (holeImage.hole.isFree()) {
                    holeImage.ball.dialer.setVisibility(View.INVISIBLE);
                } else {
                    holeImage.ball.dialer.setVisibility(View.VISIBLE);
                }
            }
        }
//        for (HoleImage holeImage: gears.get(activeGearNum).getHoles()) {
//            if (holeImage.hole.isFree()) {
//                holeImage.ball.dialer.setVisibility(View.INVISIBLE);
//            } else {
//                holeImage.ball.dialer.setVisibility(View.VISIBLE);
//            }
//        }
    }
}
