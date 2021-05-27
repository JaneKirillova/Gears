package com.example.gears.GameObjects;

import java.util.ArrayList;
import java.util.List;

public class Gear {
    private int degree;
    private boolean isLast;
    private boolean isFirst;
    List<Hole> holes = new ArrayList<>();
    private int numberOfHoles;

    public Gear() {}

    public void setIsLast(boolean bool) {
        this.isLast = bool;
    }

    public void setIsFirst(boolean bool) {
        this.isFirst = bool;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getRadius() {
        return radius;
    }
    public void setRadius(float radius) {
        this.radius = radius;
    }
    public void setXY(int x, int y) {
        this.x = x + radius / 2;
        this.y = y + radius / 2;
    }

    private  float x;
    private  float y;
    private float radius;
    public List<Integer> getDownNeighbours() {
        return downNeighbours;
    }

    public void setDownNeighbours(ArrayList<Integer> downNeighbours) {
        this.downNeighbours = downNeighbours;
    }

    public List<Integer> getUpperNeighbours() {
        return upperNeighbours;
    }

    public void setUpperNeighbours(ArrayList<Integer> upperNeighbours) {
        this.upperNeighbours = upperNeighbours;
    }

    List<Integer> downNeighbours;
    List<Integer> upperNeighbours;
    public Gear(int numberOfHoles, boolean isLast, boolean isFirst,
                List<Integer> downNeighbours, List<Integer> upperNeighbours) {
        this.numberOfHoles = numberOfHoles;
        this.isLast = isLast;
        this.isFirst = isFirst;
        this.downNeighbours = downNeighbours;
        this.upperNeighbours = upperNeighbours;
        int currentDegreeToAdd = 0;
        int step = 360 / numberOfHoles;
        for (int i = 0; i < numberOfHoles; i++) {
            Hole bufferHole = new Hole(i);
            bufferHole.setDegree(currentDegreeToAdd);
            holes.add(i, bufferHole);
            currentDegreeToAdd += step;
        }
    }

    public int getNumberOfHoles() {
        return numberOfHoles;
    }

    public int getDegree() {
        return degree;
    }

    public void setDegree(int degree) {
        this.degree += (degree + 360) % 360;
        this.degree %= 360;
        for (Hole hole : holes) {
            hole.setDegree((hole.getDegree() + degree) % 360);
        }
    }

    public List<Hole> getHoles() {
        return holes;
    }

    public boolean isLast() {
        return isLast;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public static class Hole {
        private int capacity;
        private int degree;
        private boolean isFree = true;
        private int numberOfBall;
        private int numberOfHole;

        public Hole() {

        }

        public Hole(int numberOfHole) {
            capacity = 1;
            degree = 0;
            this.numberOfHole = numberOfHole;
        }

        public int getCapacity() {
            return capacity;
        }

        public int getDegree() {
            return degree;
        }

        public void setDegree(int degree) {
            this.degree = (360 + degree) % 360;
        }

        public boolean isFree() {
            return isFree;
        }

        public void setFree(boolean free) {
            isFree = free;
        }

        public int getNumberOfBall() {
            return numberOfBall;
        }

        public void setNumberOfBall(int numberOfBall) {
            this.numberOfBall = numberOfBall;
        }

        public int getNumberOfHole() {
            return numberOfHole;
        }
    }
}