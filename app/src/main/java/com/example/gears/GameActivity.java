package com.example.gears;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {
    private static Bitmap image1, image2;
    private static Matrix matrix, matrix2;

    private ImageView dialer, dialer2;
    private int dialerHeight, dialerWidth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // load the image only once
        if (image1 == null) {
            image1 = BitmapFactory.decodeResource(getResources(), R.drawable.graphic_ring2);
            image2 = BitmapFactory.decodeResource(getResources(), R.drawable.graphic_ring);
        }

        // initialize the matrix only once
        if (matrix == null) {
            matrix = new Matrix();
            matrix2 = new Matrix();
        } else {
            matrix.reset();
        }

        dialer = (ImageView) findViewById(R.id.imageView_ring);
        dialer2 = (ImageView) findViewById(R.id.imageView_square);
        dialer.setOnTouchListener(new MyOnTouchListener());
        dialer.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (dialerHeight == 0 || dialerWidth == 0) {
                dialerHeight = dialer.getHeight();
                dialerWidth = dialer.getWidth();

                // resize
                Matrix resize = new Matrix();
                resize.postScale((float)Math.min(dialerWidth, dialerHeight) / (float) image1.getWidth(), (float)Math.min(dialerWidth, dialerHeight) / (float) image1.getHeight());
                image1 = Bitmap.createBitmap(image1, 0, 0, image1.getWidth(), image1.getHeight(), resize, false);
                image2 = Bitmap.createScaledBitmap(image2,  200, 200,  false);

                matrix2.setTranslate( dialer2.getWidth() / 2 - image2.getWidth() / 2, 40);

                dialer.setImageBitmap(image1);
                dialer.setImageMatrix(matrix);

                dialer2.setImageBitmap(image2);
                dialer2.setImageMatrix(matrix2);
            }
        });

    }

    private class MyOnTouchListener implements View.OnTouchListener {

        private double startAngle;

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
            double x = xTouch - (dialerWidth / 2d);
            double y = dialerHeight - yTouch - (dialerHeight / 2d);

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

    private void rotateDialer(float degrees) {
        matrix.postRotate(degrees, dialerWidth / 2, dialerHeight / 2);
        dialer.setImageMatrix(matrix);

        matrix2.postRotate(degrees, dialerWidth / 2, dialerHeight / 2);
        dialer2.setImageMatrix(matrix2);
    }
}
