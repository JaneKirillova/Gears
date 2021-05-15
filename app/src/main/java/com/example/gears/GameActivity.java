package com.example.gears;

import android.app.DownloadManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GameActivity extends AppCompatActivity {
    List<GearImage> gears = new LinkedList<>();
    GameState gameState = new GameState();
    Gson gson = new Gson();

    private void getGame() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URLs.URL_GET_GAME,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            System.out.println("\n\n\n" + response + "\n\n\n");
                            gson = new Gson();
                            gameState = gson.fromJson(obj.toString(), GameState.class);


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
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id", "7942873054587529260");
                params.put("token", "hello");
                params.put("currentPlayer", "FIRSTPLAYER");
                return params;
            }
        };

        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void sendGame() {
        JsonObjectRequest request_json = null;
        try {

            request_json = new JsonObjectRequest(Request.Method.POST, URLs.URL_UPDATE_GAME + "/7942873054587529260", new JSONObject(gson.toJson(gameState)),
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            //Process os success response

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.print("ОШИБКА2: ");
                    System.out.println(error.toString());
//                    String s = new String(error.networkResponse.data, Charset.defaultCharset());
//                    System.out.println(s);
//                    Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    params.put("Content-Type", "application/json");
                    params.put("userToken", "hello");
                    return params;
                }
            };
        }catch (Exception e) {
            e.printStackTrace();
        }

        VolleySingleton.getInstance(this).addToRequestQueue(request_json);
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        getGame();
        sendGame();

       /* gears.add(new GearImage(1));
        gears.add(new GearImage(3));
        gears.add(new GearImage(2));
        gears.add(new GearImage(4));
        gears.add(new GearImage(5));



        for (GearImage gearImage: gears) {
            if (gearImage.image == null) {
                gearImage.image = BitmapFactory.decodeResource(getResources(), R.drawable.gear);
                for (HoleImage holeIm : gearImage.holes) {
                    holeIm.image = BitmapFactory.decodeResource(getResources(), R.drawable.hole);
                    holeIm.ball.image = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
                }
            }


            // initialize the matrix only once
            if (gearImage.matrix == null) {
                gearImage.matrix = new Matrix();
                for (HoleImage holeIm : gearImage.holes) {
                    holeIm.matrix = new Matrix();
                    holeIm.ball.matrix = new Matrix();
                }
            } else {
                gearImage.matrix.reset();
            }
        }

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
//        gears.get(0).holes.get(0).ball.dialer.setVisibility(View.INVISIBLE);


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



        for (GearImage gear: gears) {
            gear.dialer.setOnTouchListener(new MyOnTouchListener(gear));
            gear.dialer.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
                if (gear.dialerHeight == 0 || gear.dialerWidth == 0) {
                    gear.dialerHeight = gear.dialer.getHeight();
                    gear.dialerWidth = gear.dialer.getWidth();
                    gear.radius = gear.dialerHeight / 2;

                    // resize
                    Matrix resize = new Matrix();
                    resize.postScale((float) Math.min(gear.dialerWidth, gear.dialerHeight) / (float) gear.image.getWidth(),
                            (float) Math.min(gear.dialerWidth, gear.dialerHeight) / (float) gear.image.getHeight());

                    gear.image = Bitmap.createBitmap(gear.image, 0, 0, gear.image.getWidth(), gear.image.getHeight(), resize, false);

                    for (HoleImage holeIm : gear.holes) {
                        holeIm.image = Bitmap.createScaledBitmap(holeIm.image, 150, 150, false);
                        holeIm.ball.image = Bitmap.createScaledBitmap(holeIm.ball.image, 75, 75, false);
                    }
                    int i = 0;
                    for (HoleImage holeIm : gear.holes) {
                        if (i == 1) {
                            System.out.println(10);
                        }
                        Map.Entry<Double, Double> entry = getDxDy(gear, holeIm);
                        holeIm.matrix.setTranslate(entry.getKey().floatValue(), entry.getValue().floatValue());
                        holeIm.ball.matrix.setTranslate(entry.getKey().floatValue() + 75 / 2, entry.getValue().floatValue() + 75);
                        i++;
                    }

                    gear.dialer.setImageBitmap(gear.image);
                    gear.dialer.setImageMatrix(gear.matrix);

                    for (HoleImage holeIm : gear.holes) {
                        holeIm.dialer.setImageBitmap(holeIm.image);
                        holeIm.dialer.setImageMatrix(holeIm.matrix);
                        holeIm.ball.dialer.setImageBitmap(holeIm.ball.image);
                        holeIm.ball.dialer.setImageMatrix(holeIm.ball.matrix);
                    }
                }
            });
        }*/

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


    private class MyOnTouchListener implements View.OnTouchListener {

        private double startAngle;
        private GearImage gearImage;

        public MyOnTouchListener(GearImage gearImage) {
            this.gearImage = gearImage;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            switch (event.getAction()) {

                case MotionEvent.ACTION_DOWN: // начало действия
                    startAngle = getAngle(event.getX(), event.getY());
                    break;

                case MotionEvent.ACTION_MOVE: // произошло изменение в активом действии
                    double currentAngle = getAngle(event.getX(), event.getY());
                    rotateDialer((float) (startAngle - currentAngle));
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
    }


}
