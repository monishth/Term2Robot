package com.tbt;

import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.tbt.RobotMap.NODE_LENGTH;

public class Robot {
    private final SensorMode colourSensor;
    private final float[] colourSample;
    private EV3LargeRegulatedMotor motorRight;
    private EV3LargeRegulatedMotor motorLeft;
    private SampleProvider gyroSensor;
    private float[] angleSample;
    private double[] bayesianProbabilties;

    private final boolean[] blueMap = new boolean[]{
            false,false,true,true,true,false,true,true,false,false,true,true,true,false,true,true,false,false,true,true,
            true,false,false,true,true,true,false,true,true,false,false,true,true,true,false,true,true
    };

    private final double sensorRight = 0.9;
    private double moveSuccess = 0.975;
    private RobotMap map;

    public Robot(){
        //Setup Motors
        motorRight = new EV3LargeRegulatedMotor(MotorPort.A);
        motorLeft = new EV3LargeRegulatedMotor(MotorPort.B);
        gyroSensor = new EV3GyroSensor(SensorPort.S1).getAngleMode();
        colourSensor = new EV3ColorSensor(SensorPort.S2).getRedMode();
        colourSample = new float[colourSensor.sampleSize()];
        angleSample =new float[gyroSensor.sampleSize()];
        motorLeft.setSpeed(90);
        motorRight.setSpeed(90);
        bayesianProbabilties = new double[blueMap.length];


    }

    public void localise(){
        double angle = 1.75 * 360 / 17.28;

        for (int i =0; i < 9; i++){
            bayesianProbabilties[i] = 0;
        }
        for(int i = 9; i < bayesianProbabilties.length; i++){
            bayesianProbabilties[i] = 1.0/blueMap.length-9;
        }
        colourSensor.fetchSample(colourSample, 0);
        float previousSample = colourSample[0];
        int counter = 0;
        while(Math.abs(previousSample-colourSample[0]) < 0.07){
            motorRight.rotate((int) (angle/8), true);
            motorLeft.rotate((int) (angle/8));
            //if(counter > 1){
            previousSample = colourSample[0];
            colourSensor.fetchSample(colourSample, 0);
            System.out.println(previousSample + "," + colourSample[0]);
            //counter = 0;
            //}
            Delay.msDelay(50);
        }
        motorRight.stop(true);
        motorLeft.stop();
        motorLeft.rotate((int) (-angle*5/8), true);
        motorRight.rotate((int) (-angle*5/8));

        while (getHighestProbability() < 0.85){
            colourSensor.fetchSample(colourSample, 0);
            boolean isBlue = false;
            if (colourSample[0] < 0.2){
                isBlue = true;
            }

            Delay.msDelay(1000);
            motorRight.rotate((int) angle, true);
            motorLeft.rotate((int) angle);
            bayesFilter(isBlue);
            System.out.println(getPrediction()+", " + (Math.round(getHighestProbability()*100)) + ", " + colourSample[0]);
        }

        System.out.println(getPrediction());

    }

    public void bayesFilter(boolean isBlue){
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

    public int getPrediction(){
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

    public double getHighestProbability(){
        return bayesianProbabilties[getPrediction()];
    }

    public void createPathingMap(){
        map = new RobotMap(RobotMap.BOARD_LENTH, NODE_LENGTH);
        map.addRectangleObstacle(0,0,1,1);
    }

    public void followDirectionList(List<Node.Direction> directions, Node.Direction startingDirection){
        Node.Direction currentDirection = startingDirection;
        List<Node.Direction> directionsAsList = Arrays.asList(Node.Direction.values());
        for(Node.Direction nextMovement : directions){
            if(currentDirection==nextMovement){
                moveForward(NODE_LENGTH);
            }else{
                int currentIndex = directionsAsList.indexOf(currentDirection);
                int requiredDirectionIndex = directionsAsList.indexOf(nextMovement);
                if(currentIndex < requiredDirectionIndex){
                    if(requiredDirectionIndex-currentIndex < (currentIndex+(8-requiredDirectionIndex))){
                        rotate45(requiredDirectionIndex-currentIndex);
                    }else{
                        rotate45(-(currentIndex+(8-requiredDirectionIndex)));
                    }
                }else{
                    if(currentIndex-requiredDirectionIndex < (requiredDirectionIndex+(8-currentIndex))){
                        rotate45(requiredDirectionIndex-currentIndex);
                    }else{
                        rotate45((requiredDirectionIndex+(8-currentIndex)));
                    }
                }
                moveForward(NODE_LENGTH);
            }
            currentDirection=nextMovement;
        }
    }

    private void rotate45(int n) {
        int rotateAngle = n*45;
        //TODO: write rotation code
    }

    private void moveForward(double distance) {
        int angleToRotate = (int) (distance * 360/17.28);
        motorRight.rotate(angleToRotate, true);
        motorLeft.rotate(angleToRotate);
    }

    public static void main(String[] args) {
        Robot robot = new Robot();
        robot.localise();
        Button.waitForAnyPress();
        RobotMap map = new RobotMap(125, 4);

    }
}
