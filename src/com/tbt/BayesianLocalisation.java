package com.tbt;

import lejos.utility.Delay;

public class BayesianLocalisation {

    public static final boolean[] blueMap = new boolean[]{
            false,false,true,true,true,false,true,true,false,false,true,true,true,false,true,true,false,false,true,true,
            true,false,false,true,true,true,false,true,true,false,false,true,true,true,false,true,true
    };

    public static final double sensorRight = 0.9;
    public static final double moveSuccess = 0.975;

    /**
     * routine to localise
     *
     * @param robot Robot object to find location from
     * @return index on bayesian strip
     */
    public static int findLocation(Robot robot){
        double[] bayesianProbabilities = new double[blueMap.length];;
        float[] colourSample = new float[robot.colourSensor.sampleSize()];
        double angle = 1.75 * 360 / 17.28;

        for (int i =0; i < 9; i++){
            bayesianProbabilities[i] = 0;
        }
        for(int i = 9; i < bayesianProbabilities.length; i++){
            bayesianProbabilities[i] = 1.0/blueMap.length-9;
        }
        robot.colourSensor.setCurrentMode("Red");
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

        while (getMaxProbability(bayesianProbabilities) < 0.85){
            robot.colourSensor.fetchSample(colourSample, 0);
            boolean isBlue = false;
            if (colourSample[0] < 0.2){
                isBlue = true;
            }

            Delay.msDelay(1000);
            robot.motorRight.rotate((int) angle, true);
            robot.motorLeft.rotate((int) angle);
            bayesFilter(isBlue, bayesianProbabilities);
            System.out.println(getPredictedLocation(bayesianProbabilities)+", " + (Math.round(getMaxProbability(bayesianProbabilities)*100)) + ", " + colourSample[0]);
        }

        System.out.println(getPredictedLocation(bayesianProbabilities));
        return getPredictedLocation(bayesianProbabilities);
    }

    /**
     * calculates bayesian probabilites
     *
     * @param blue is the current sensed colour blue?
     * @param bayesianProbabilities array of probabilities
     */
    public static void bayesFilter(boolean blue, double[] bayesianProbabilities){
        //update based on sensor value
        double coefficient = 0;
        for(int i = 0; i <bayesianProbabilities.length; i++){
            if(blueMap[i] == blue){
                bayesianProbabilities[i] *= sensorRight;
            }else{
                bayesianProbabilities[i] *= 1-sensorRight;
            }

            coefficient += bayesianProbabilities[i];
        }

        for(int i = 0; i < bayesianProbabilities.length; i++){
            bayesianProbabilities[i] /= coefficient;
        }
        //update based on move
        coefficient = 0;

        for(int i = bayesianProbabilities.length-1;i > 0 ; i--){
            bayesianProbabilities[i] = bayesianProbabilities[i-1] * moveSuccess + bayesianProbabilities[i]*(1-moveSuccess);
            coefficient += bayesianProbabilities[i];
        }

        for(int i = 0; i < bayesianProbabilities.length; i++){
            bayesianProbabilities[i] /= coefficient;
        }

    }

    /**
     * get the index with the highest probability
     *
     * @param bayesianProbabilities array of probabilities
     * @return index of position on strip
     */
    public static int getPredictedLocation(double[] bayesianProbabilities){
        double max = bayesianProbabilities[0];
        int index = 0;

        for(int i = 1; i < bayesianProbabilities.length; ++i) {
            if(bayesianProbabilities[i] > max) {
                max = bayesianProbabilities[i];
                index = i;
            }
        }

        return index;
    }

    /**
     * @param bayesianProbabilities array of probabilities
     * @return max probability in the array
     */
    public static double getMaxProbability(double[] bayesianProbabilities){
        return bayesianProbabilities[getPredictedLocation(bayesianProbabilities)];
    }


}
