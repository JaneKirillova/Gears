package com.example.gears;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.widget.Button;
import android.widget.ImageView;

import com.example.gears.GameObjects.Gear;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class GearImage {
    public static List<Integer> getNeighbors(int gearNum) {
        switch (gearNum) {
            case (1):
                return Arrays.asList(-1, 2);
            case (2):
                return Arrays.asList(1, 3, 4);
            case(3):
                return Arrays.asList(2, 4, 5);
            case (4):
                return Arrays.asList(2, 3, 5);
            case (5):
                return Arrays.asList(3, 4, -1);
        }
        return null;
    }

    public Bitmap image;
    public Matrix matrix;
    public ImageView dialer;
    public int dialerHeight, dialerWidth;
    public int radius;
    int holesNumber;
    Gear gear = null;
    List<HoleImage> holes = new ArrayList<>();
    List<Integer> neighbours;
    public double accumulatedAngle = 0;
    public Button selectingButton;
    public int gearNumber;

    public GearImage(int holesNumber, int gearNumber) {
        this.gearNumber = gearNumber;
        this.holesNumber = holesNumber;
        int currentAngle = 0;
        int step = 360 / holesNumber;
        for (int i = 0; i < holesNumber; i++) {
            holes.add(new HoleImage(currentAngle));
            currentAngle += step;
        }
    }

    public int getHolesNumber() {
        return holesNumber;
    }

    public List<HoleImage> getHoles() {
        return holes;
    }

    public void setGear(Gear gear) {
        this.gear = gear;
    }

    public void setNeighbours(List<Integer> neighbours) {
        this.neighbours = neighbours;
    }

    public List<Integer> getNeighbours() {
        return neighbours;
    }

    public void setHoles() {
        ListIterator<HoleImage> iterator = holes.listIterator();
        for (Gear.Hole hole: gear.getHoles()) {
            if (!iterator.hasNext()) {
                break;
            }
            HoleImage holeImage = iterator.next();
            holeImage.setHole(hole);
        }
    }
}
