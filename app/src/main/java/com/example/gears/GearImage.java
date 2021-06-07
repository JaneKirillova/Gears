package com.example.gears;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.widget.Button;
import android.widget.ImageView;

import com.example.gears.gameObjects.Gear;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

public class GearImage {
    public static List<Integer> getUpperNeighborsList(int gearNum) {
        switch (gearNum) {
            case (1):
                return Collections.emptyList();
            case (2):
                return Collections.singletonList(0);
            case(3):
            case (4):
                return Collections.singletonList(1);
            case (5):
                return Arrays.asList(2, 3);
        }
        return null;
    }

    public List<Integer> getDownNeighbours() {
        return downNeighbours;
    }

    public static List<Integer> getDownNeighborsList(int gearNum) {
        switch (gearNum) {
            case (1):
                return Collections.singletonList(1);
            case (2):
                return Arrays.asList(2, 3);
            case(3):
            case (4):
                return Collections.singletonList(4);
            case (5):
                return Collections.emptyList();
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

    public List<Integer> getNeighboursList() {
        return neighbours;
    }

    public List<Integer> getUpperNeighbours() {
        return upperNeighbours;
    }

    List<Integer> upperNeighbours;
    List<Integer> downNeighbours;
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

    public void setNeighbours(List<Integer> upperNeighbours, List<Integer> downNeighbours) {
        this.upperNeighbours = upperNeighbours;
        this.downNeighbours = downNeighbours;
    }

    public void setGear(Gear gear) {
        this.gear = gear;
    }

    public void setGearWithHoles(Gear gear) {
        this.gear = gear;
        for (int i = 0; i < gear.getHoles().size(); i++) {
            holes.get(i).hole = gear.getHoles().get(i);
        }

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
