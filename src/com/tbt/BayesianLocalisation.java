package com.tbt;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.SensorMode;
import lejos.utility.Delay;

public class BayesianLocalisation {

    public static final boolean[] blueMap = new boolean[]{
            false,false,true,true,true,false,true,true,false,false,true,true,true,false,true,true,false,false,true,true,
            true,false,false,true,true,true,false,true,true,false,false,true,true,true,false,true,true
    };

    public static final double sensorRight = 0.9;
    public static final double moveSuccess = 0.975;

    public static int localise(Robot robot){
        double[] bayesianProbabilties = new double[blueMap.length];;
        float[] colourSample = new float[robot.colourSensor.sampleSize()];
        double angle = 1.75 * 360 / 17.28;

        for (int i =0; i < 9; i++){
            bayesianProbabilties[i] = 0;
        }
        for(int i = 9; i < bayesianProbabilties.length; i++){
            bayesianProbabilties[i] = 1.0/blueMap.length-9;
        }
        robot.colourSensor.fetchSample(colourSample, 0);
        float previousSample = colourSample[0];
        int counter = 0;
        while(Math.abs(previousSample-colourSample[0]) < 0.07){
            robot.motorRight.rotate((int) (angle/8), true);
            robot.motorLeft.rotate((int) (angle/8));
            //if(counter > 1){
            previousSample = colourSample[0];
            robot.colourSensor.fetchSample(colourSample, 0);
            System.out.println(previousSample + "," + colourSample[0]);
            //counter = 0;
            //}
            Delay.msDelay(50);
        }
        robot.motorRight.stop(true);
        robot.motorLeft.stop();
        robot.motorLeft.rotate((int) (-angle*5/8), true);
        robot.motorRight.rotate((int) (-angle*5/8));

        while (getHighestProbability(bayesianProbabilties) < 0.85){
            robot.colourSensor.fetchSample(colourSample, 0);
            boolean isBlue = false;
            if (colourSample[0] < 0.2){
                isBlue = true;
            }

            Delay.msDelay(1000);
            robot.motorRight.rotate((int) angle, true);
            robot.motorLeft.rotate((int) angle);
            bayesFilter(isBlue, bayesianProbabilties);
            System.out.println(getPrediction(bayesianProbabilties)+", " + (Math.round(getHighestProbability(bayesianProbabilties)*100)) + ", " + colourSample[0]);
        }

        System.out.println(getPrediction(bayesianProbabilties));
        return getPrediction(bayesianProbabilties);
    }

    public static void bayesFilter(boolean isBlue, double[] bayesianProbabilties){
        //update based on sensor value
        double coefficient = 0;
        for(int i = 0; i <bayesianProbabilties.length; i++){
            if(blueMap[i] == isBlue){
                bayesianProbabilties[i] *= sensorRight;
            }else{
                bayesianProbabilties[i] *= 1-sensorRight;
            }

            coefficient += bayesianProbabilties[i];
        }

        for(int i = 0; i < bayesianProbabilties.length; i++){
            bayesianProbabilties[i] /= coefficient;
        }

        coefficient = 0;

        for(int i = bayesianProbabilties.length-1;i > 0 ; i--){
            bayesianProbabilties[i] = bayesianProbabilties[i-1] * moveSuccess + bayesianProbabilties[i]*(1-moveSuccess);
            coefficient += bayesianProbabilties[i];
        }

        for(int i = 0; i < bayesianProbabilties.length; i++){
            bayesianProbabilties[i] /= coefficient;
        }

    }

    public static int getPrediction(double[] bayesianProbabilties){
        double max = bayesianProbabilties[0];
        int index = 0;

        for(int i = 1; i < bayesianProbabilties.length; ++i) {
            if(bayesianProbabilties[i] > max) {
                max = bayesianProbabilties[i];
                index = i;
            }
        }

        return index;
    }

    public static double getHighestProbability(double[] bayesianProbabilties){
        return bayesianProbabilties[getPrediction(bayesianProbabilties)];
    }


}
