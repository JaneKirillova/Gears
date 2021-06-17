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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.example.gears.gameObjects.Board;
import com.example.gears.gameObjects.Gear;
import com.google.gson.Gson;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrainingActivity extends AppCompatActivity {
    private final ArrayList<GearImage> gears = new ArrayList<>();
    ImageView ballInRightGutter, ballInLeftGutter;
    String currentPlayer;
    private Board currentPlayerBoard;
    private Gson gson = new Gson();
    private int activeGearNum = -1;
    int numberOfDrownGears = 0;
    TextView leftGutter, rightGutter;


    @Override
    protected void onStart() {
        super.onStart();
        currentPlayerBoard = new Board();
        if (currentPlayer.equals("FIRSTPLAYER")) {
            currentPlayerBoard.getPot().setDegree(240);
        }
        rightGutter.setText(String.valueOf(currentPlayerBoard.getRightGutter().getHowManyBalls()));
        leftGutter.setText(String.valueOf(currentPlayerBoard.getLeftGutter().getHowManyBalls()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        initGameState();
        setListeners();
    }


    @Override
    public void onBackPressed() {
        finish();
        startActivity(new Intent(getApplicationContext(), NavigationActivity.class));
        super.onBackPressed();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Math.random() * 10 % 2 == 0) {
            currentPlayer = "FIRSTPLAYER";
            setContentView(R.layout.activity_training1);
        } else {
            currentPlayer = "SECONDPLAYER";
            setContentView(R.layout.activity_training2);
        }

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

        initFirstPlayerScreen();

        for (int i = 0; i < gears.size(); i++) {
            int finalI = i;
            gears.get(i).selectingButton.setOnClickListener(v -> {
                if (activeGearNum >= 0) {
                    gears.get(activeGearNum).notChosenGear.setVisibility(View.VISIBLE);
                }
                activeGearNum = gears.get(finalI).gearNumber - 1;
                gears.get(activeGearNum).notChosenGear.setVisibility(View.INVISIBLE);
            });
        }
    }

    private void initFirstPlayerScreen() {
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

        leftGutter = findViewById(R.id.left_gutter_text);
        rightGutter = findViewById(R.id.right_gutter_text);
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
                        makeUniqueBoard();
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
    }

    public void makeUniqueBoard() {
        int angle;
        for (int i = 0; i < 5; i++) {
            angle = (int) (Math.random() * 360);
            angle = (angle / 10) * 10;
            rotateDialer(gears.get(i), angle);
            gears.get(i).gear.setDegree(angle);
        }
    }
}
