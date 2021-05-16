package com.example.gears.GameObjects;

import com.example.gears.GearImage;
import com.example.gears.HoleImage;

import java.util.ArrayList;
import java.util.List;

public class Gear {
    private int degree;
    private final boolean isLast;
    private final boolean isFirst;
    ArrayList<Hole> holes = new ArrayList<>();
    private final int numberOfHoles;

    public List<Integer> getDownNeighbours() {
        return downNeighbours;
    }

    public void setDownNeighbours(List<Integer> downNeighbours) {
        this.downNeighbours = downNeighbours;
    }

    public List<Integer> getUpperNeighbours() {
        return upperNeighbours;
    }

    public void setUpperNeighbours(List<Integer> upperNeighbours) {
        this.upperNeighbours = upperNeighbours;
    }

    //ArrayList<Integer> neighbours;
    List<Integer> downNeighbours;
    List<Integer> upperNeighbours;
    public Gear(int numberOfHoles, boolean isLast, boolean isFirst, List<Integer> downNeighbours, List<Integer> upperNeighbours) {
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

    public ArrayList<Hole> getHoles() {
        return holes;
    }

    public boolean isLast() {
        return isLast;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public class Hole {
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