package com.example.gears;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

public class GearImage {
    public Bitmap image;
    public Matrix matrix;
    public ImageView dialer;
    public int dialerHeight, dialerWidth;
    public int radius;
    List<HoleImage> holes = new ArrayList<>();

    public GearImage(int holesNum) {
        int currentAngle = 0;
        int step = 360 / holesNum;
        for (int i = 0; i < holesNum; i++) {
            holes.add(new HoleImage(currentAngle));
            currentAngle += step;
        }
    }
}
