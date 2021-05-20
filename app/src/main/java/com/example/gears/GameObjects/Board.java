package com.example.gears.GameObjects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Board {
    private List<Gear> gears;
    private Gutter rightGutter = new Gutter(60);
    private Gutter leftGutter = new Gutter(300);
    private Pot pot = new Pot();
    final private int step = 10;

    public Gutter getLeftGutter() {
        return leftGutter;
    }

    public void setLeftGutter(Gutter leftGutter) {
        this.leftGutter = leftGutter;
    }

    public Gutter getRightGutter() {
        return rightGutter;
    }

    public void setRightGutter(Gutter rightGutter) {
        this.rightGutter = rightGutter;
    }

    public Pot getPot() {
        return pot;
    }

    public void setPot(Pot pot) {
        this.pot = pot;
    }


    public List<Gear> getGears() {
        return gears;
    }

    public void setGears(List<Gear> gears) {
        this.gears = gears;
    }

    public void rebuild(int degree, int activeGear) {
        if (activeGear == 1 && getGears().get(1).getDegree() == 40) {
            System.out.println("p");
        }
        for (int i = 0; i < Math.abs(degree) / step; i++) {
            Gear changingGear = getGears().get(activeGear);
            if (degree >= 0) {
                changingGear.setDegree(step);
            } else {
                changingGear.setDegree(360 - step);
            }

            extractBallsFromLastGear(activeGear, changingGear);
            putBallsInFirstGear(activeGear, changingGear);

            for (Integer indexDownNeighbour : changingGear.getDownNeighbours()) {
                connectionHoles(changingGear, this.gears.get(indexDownNeighbour));
            }
            for (Integer indexUpperNeighbour : changingGear.getUpperNeighbours()) {
                connectionHoles(this.gears.get(indexUpperNeighbour), changingGear);
            }

        }

    }


    private void putBallsInFirstGear(int activeGear, Gear changingGear) {
        if (changingGear.isFirst()) {
            for (Gear.Hole holeOfChangingGear : changingGear.getHoles()) {
                if (holeOfChangingGear.isFree() && holeOfChangingGear.getDegree() == this.getLeftGutter().getDegree() ||
                        holeOfChangingGear.getDegree() == this.getRightGutter().getDegree()) {

                    if (holeOfChangingGear.getDegree() == this.getLeftGutter().getDegree()) {
                        this.getLeftGutter().setHowManyBalls(getLeftGutter().getHowManyBalls() - holeOfChangingGear.getCapacity());
                    }

                    if (holeOfChangingGear.getDegree() == this.getRightGutter().getDegree()) {
                        this.getRightGutter().setHowManyBalls(getRightGutter().getHowManyBalls() - holeOfChangingGear.getCapacity());
                    }
                    holeOfChangingGear.setFree(false);
//                    changingGear.getHoles().set(holeOfChangingGear.getNumberOfHole(), holeOfChangingGear);
                }
            }
//            List<Gear> arrayOfGears = this.getGears();
//            arrayOfGears.set(activeGear, changingGear);
        }
    }

    private void extractBallsFromLastGear(int activeGear, Gear changingGear) {
        if (changingGear.isLast()) {
            for (Gear.Hole holeOfChangingGear : changingGear.getHoles()) {
                if (!holeOfChangingGear.isFree() && holeOfChangingGear.getDegree() == this.getPot().getDegree() && !holeOfChangingGear.isFree()) {
                    this.getPot().setHowManyBalls(getPot().getHowManyBalls() + holeOfChangingGear.getCapacity());
                    holeOfChangingGear.setFree(true);

                    changingGear.getHoles().set(holeOfChangingGear.getNumberOfHole(), holeOfChangingGear);
                }
            }
            List<Gear> arrayOfGears = this.getGears();
            arrayOfGears.set(activeGear, changingGear);
        }
    }

    private boolean connectionHoleWithGearsCenter(Gear upperGear, Gear downGear, Gear.Hole upperHole) {
        if (upperGear.getDegree() == 60 && downGear.getDegree() == 60) {
            System.out.println("yy");
        }

        int upperRadius = upperGear.getRadius();
        int downRadius = downGear.getRadius();
        int sumRadius = upperRadius + downRadius + 20;
        double deg = 90 - upperHole.getDegree();
        double x = sumRadius * Math.cos(Math.toRadians(deg));
        double y = sumRadius * Math.sin(Math.toRadians(deg));
        int mistake = 60;
        x += upperGear.getX();
        y = upperGear.getY() - y;
        if (y < 0) {
            System.out.println("vvvvv");
        }
        double dist = Math.sqrt(Math.pow((x - downGear.getX()), 2) + Math.pow((y - downGear.getY()), 2));
        if (dist <= mistake) {
            System.out.println("ppppp");
        }
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! x: " +
                x + " y: " + y + " DOWNGEAR_X: " +  downGear.getX() + " DOWNGEAR_Y: " + downGear.getY() + " dist: " + dist
                + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        return dist <= mistake;
    }

    private void connectionHoles(Gear upperGear, Gear downGear) {
        for (Gear.Hole holeUpperGear : upperGear.getHoles()) {
            if (!holeUpperGear.isFree() && connectionHoleWithGearsCenter(upperGear, downGear, holeUpperGear)) {
                for (Gear.Hole holeDownGear : downGear.getHoles()) {
                    if (holeDownGear.isFree() ) {
                        if (checkDegreeEquals(holeUpperGear, holeDownGear, upperGear.getX(), downGear.getX())) {
                            holeDownGear.setFree(false);
                            holeUpperGear.setFree(true);
//                            break;
                        }
                    }
                }
//                break;
            }
        }
    }

    private boolean checkDegreeEquals(Gear.Hole upperGearHole, Gear.Hole downGearHole, double xUpperGear, double xDownGear) {
        if (xUpperGear - xDownGear < 0) {
            return upperGearHole.getDegree() < 180 && downGearHole.getDegree() > 180 &&
                    upperGearHole.getDegree() == downGearHole.getDegree() - 180;
        }
        boolean flag = upperGearHole.getDegree() > 180 && downGearHole.getDegree() < 180 &&
                downGearHole.getDegree() == upperGearHole.getDegree() - 180;

        if (flag) {
            System.out.println("lll");
        }
        return flag;
    }

    public int getStep() {
        return step;
    }

    private class Gutter {
        public Gutter() {
        }

        private int degree = 60;
        private int howManyBalls = 6;

        public Gutter(int degree) {
            this.degree = degree;
        }

        public int getDegree() {
            return degree;
        }

        public void setDegree(int degree) {
            this.degree = degree;
        }

        public int getHowManyBalls() {
            return howManyBalls;
        }

        public void setHowManyBalls(int howManyBalls) {
            this.howManyBalls = howManyBalls;
        }
    }

    private class Pot {
        public Pot() {
        }

        private int degree = 120;
        private int howManyBalls = 0;

        public int getDegree() {
            return degree;
        }

        public void setDegree(int degree) {
            this.degree = degree;
        }

        public int getHowManyBalls() {
            return howManyBalls;
        }

        public void setHowManyBalls(int howManyBalls) {
            this.howManyBalls = howManyBalls;
        }
    }

    public static void main(String[] args) {
        Gear leftGear = new Gear(1, false, false, 2, 2, 2,
                Collections.singletonList(1), Collections.emptyList());
        Gear rightGear = new Gear(1, false, false, 3, 2, 1, Collections.emptyList(),
                Collections.singletonList(0));
        leftGear.getHoles().get(0).setFree(true);
        rightGear.getHoles().get(0).setFree(false);
        Board b = new Board();
        b.setGears(Arrays.asList(leftGear, rightGear));
        b.rebuild(90, 0);
        b.rebuild(-90, 1);
        if (!b.getGears().get(1).getHoles().get(0).isFree()) {
            System.out.println("URA");
        } else {
            System.out.println("PIZDA");
        }

    }
}
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//public class Board {
//    private List<Gear> gears;
//    private Gutter rightGutter = new Gutter(60);
//    private Gutter leftGutter = new Gutter(300);
//    private Pot pot = new Pot();
//    final private int step = 10;
//
//    int buildDegree, conDegree;
//
//    public Gutter getLeftGutter() {
//        return leftGutter;
//    }
//
//    public void setLeftGutter(Gutter leftGutter) {
//        this.leftGutter = leftGutter;
//    }
//
//    public Gutter getRightGutter() {
//        return rightGutter;
//    }
//
//    public void setRightGutter(Gutter rightGutter) {
//        this.rightGutter = rightGutter;
//    }
//
//    public Pot getPot() {
//        return pot;
//    }
//
//    public void setPot(Pot pot) {
//        this.pot = pot;
//    }
//
//
//    public List<Gear> getGears() {
//        return gears;
//    }
//
//    public void setGears(List<Gear> gears) {
//        this.gears = gears;
//    }
//
//    public void rebuild(int degree, int activeGear) {
//
//        for (int i = 0; i < Math.abs(degree) / step; i++) {
//            Gear changingGear = getGears().get(activeGear);
//            if (degree >= 0) {
//                changingGear.setDegree(step);
//            } else {
//                changingGear.setDegree(360 - step);
//            }
//
//
//            buildDegree = changingGear.getDegree();
//            System.out.println("!!!!!!!!!! degree rebuild: " + changingGear.getDegree() + " !!!!!!!");
//
//            extractBallsFromLastGear(activeGear, changingGear);
//            putBallsInFirstGear(activeGear, changingGear);
//
//            for (Integer indexDownNeighbour : changingGear.getDownNeighbours()) {
//                connectionHoles(changingGear, this.gears.get(indexDownNeighbour));
//            }
//            for (Integer indexUpperNeighbour : changingGear.getUpperNeighbours()) {
//                connectionHoles(this.gears.get(indexUpperNeighbour), changingGear);
//            }
//
//        }
//
//    }
//
//
//    private void putBallsInFirstGear(int activeGear, Gear changingGear) {
//        if (changingGear.isFirst()) {
//            for (Gear.Hole holeOfChangingGear : changingGear.getHoles()) {
//                if (holeOfChangingGear.isFree() && holeOfChangingGear.getDegree() == this.getLeftGutter().getDegree() ||
//                        holeOfChangingGear.getDegree() == this.getRightGutter().getDegree()) {
//
//                    if (holeOfChangingGear.getDegree() == this.getLeftGutter().getDegree()) {
//                        this.getLeftGutter().setHowManyBalls(getLeftGutter().getHowManyBalls() - holeOfChangingGear.getCapacity());
//                    }
//
//                    if (holeOfChangingGear.getDegree() == this.getRightGutter().getDegree()) {
//                        this.getRightGutter().setHowManyBalls(getRightGutter().getHowManyBalls() - holeOfChangingGear.getCapacity());
//                    }
//                    holeOfChangingGear.setFree(false);
//                    changingGear.getHoles().set(holeOfChangingGear.getNumberOfHole(), holeOfChangingGear);
//                }
//            }
//            List<Gear> arrayOfGears = this.getGears();
//            arrayOfGears.set(activeGear, changingGear);
//        }
//    }
//
//    private void extractBallsFromLastGear(int activeGear, Gear changingGear) {
//        if (changingGear.isLast()) {
//            for (Gear.Hole holeOfChangingGear : changingGear.getHoles()) {
//                if (!holeOfChangingGear.isFree() && holeOfChangingGear.getDegree() == this.getPot().getDegree() && !holeOfChangingGear.isFree()) {
//                    this.getPot().setHowManyBalls(getPot().getHowManyBalls() + holeOfChangingGear.getCapacity());
//                    holeOfChangingGear.setFree(true);
//
//                    changingGear.getHoles().set(holeOfChangingGear.getNumberOfHole(), holeOfChangingGear);
//                }
//            }
//            List<Gear> arrayOfGears = this.getGears();
//            arrayOfGears.set(activeGear, changingGear);
//        }
//    }
//
//    private boolean connectionHoleWithGearsCenter(Gear upperGear, Gear downGear, Gear.Hole upperHole) {
//        System.out.println("!!!!! degInconnection: " + upperGear.getDegree() + " !!!!!!!");
//        conDegree = upperGear.getDegree();
//        if (conDegree != buildDegree) {
//            System.out.println("gggg");
//        }
////        if (upperHole.getDegree() == 60) {
////
////        }
//        int upperRadius = upperGear.getRadius();
//        int downRadius = downGear.getRadius();
//        int sumRadius = upperRadius + downRadius + 70;
//        double deg = 90 - upperHole.getDegree();
//        double x = sumRadius * Math.cos(Math.toRadians(deg));
//        double y = sumRadius * Math.sin(Math.toRadians(deg));
//        int mistake = 60;
//        x += upperGear.getX();
//        y = upperGear.getY() - y;
//        if (y < 0) {
//            System.out.println("vvvvv");
//        }
//        double dist = Math.sqrt(Math.pow((x - downGear.getX()), 2) + Math.pow((y - downGear.getY()), 2));
//        if (dist <= mistake) {
//            System.out.println("ppppp");
//        }
//        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! x: " +
//                x + " y: " + y + " DOWNGEAR_X: " +  downGear.getX() + " DOWNGEAR_Y: " + downGear.getY() + " dist: " + dist
//                + "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//        return dist <= mistake;
//    }
//
//    private void connectionHoles(Gear upperGear, Gear downGear) {
//        for (Gear.Hole holeUpperGear : upperGear.getHoles()) {
//            if (!holeUpperGear.isFree() && connectionHoleWithGearsCenter(upperGear, downGear, holeUpperGear)) {
//                for (Gear.Hole holeDownGear : downGear.getHoles()) {
//                    if (holeDownGear.isFree()) {
//                        if (holeDownGear.getDegree() % 180 == (360 - holeUpperGear.getDegree()) % 180) {
//                            holeDownGear.setFree(false);
//                            holeUpperGear.setFree(true);
//                            break;
//                        }
//                    }
//
//                    break;
//                }
//            }
//        }
//    }
//
//    public int getStep() {
//        return step;
//    }
//
//    private class Gutter {
//        public Gutter() {
//        }
//
//        private int degree = 60;
//        private int howManyBalls = 1;
//
//        public Gutter(int degree) {
//            this.degree = degree;
//        }
//
//        public int getDegree() {
//            return degree;
//        }
//
//        public void setDegree(int degree) {
//            this.degree = degree;
//        }
//
//        public int getHowManyBalls() {
//            return howManyBalls;
//        }
//
//        public void setHowManyBalls(int howManyBalls) {
//            this.howManyBalls = howManyBalls;
//        }
//    }
//
//    private class Pot {
//        public Pot() {
//        }
//
//        private int degree = 120;
//        private int howManyBalls = 0;
//
//        public int getDegree() {
//            return degree;
//        }
//
//        public void setDegree(int degree) {
//            this.degree = degree;
//        }
//
//        public int getHowManyBalls() {
//            return howManyBalls;
//        }
//
//        public void setHowManyBalls(int howManyBalls) {
//            this.howManyBalls = howManyBalls;
//        }
//    }
//
//    public static void main(String[] args) {
//        Gear leftGear = new Gear(1, false, false, 2, 2, 2,
//                Collections.singletonList(1), Collections.emptyList());
//        Gear rightGear = new Gear(1, false, false, 3, 2, 1, Collections.emptyList(),
//                Collections.singletonList(0));
//        leftGear.getHoles().get(0).setFree(true);
//        rightGear.getHoles().get(0).setFree(false);
//        Board b = new Board();
//        b.setGears(Arrays.asList(leftGear, rightGear));
//        b.rebuild(90, 0);
//        b.rebuild(-90,1);
//        System.out.println();
//
//    }
//}