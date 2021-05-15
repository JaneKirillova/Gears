package com.example.gears;

import java.util.ArrayList;

public class Gear {
    private int degree;
    private final boolean isLast;
    private final boolean isFirst;
    ArrayList<Hole> holes = new ArrayList<>();
    private final int numberOfHoles;
    ArrayList<Integer> neighbours;

    public Gear(int numberOfHoles, boolean isLast, boolean isFirst, ArrayList<Integer> neighbours) {
        this.numberOfHoles = numberOfHoles;
        this.isLast = isLast;
        this.isFirst = isFirst;
        this.neighbours = neighbours;
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

    public ArrayList<Integer> getNeighbours() {
        return neighbours;
    }

    public void setDegree(int degree) {
        this.degree += (degree + 360) % 360;
        this.degree %= 360;
        for (Hole hole : holes) {
            hole.setDegree((hole.getDegree() + degree) % 360);
        }
    }

    public ArrayList<Hole> getHoles() {
        return holes;
    }

    public boolean isLast() {
        return isLast;
    }

    public boolean isFirst() {
        return isFirst;
    }

    protected class Hole {
        private final int capacity;
        private int degree;
        private boolean isFree = true;
        private int numberOfBall;
        private final int numberOfHole;

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
            this.degree = degree % 360;
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