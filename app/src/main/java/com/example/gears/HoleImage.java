package com.example.gears;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.widget.ImageView;

public class HoleImage {
    public Bitmap image;
    public Matrix matrix;
    public ImageView dialer;
    public int dialerHeight = 0, dialerWidth = 0;
    public int degree = 0;
    public BallImage ball = new BallImage();
    public Boolean isFree = true;

    public HoleImage(int degree) {
        this.degree = degree;
    }

}
