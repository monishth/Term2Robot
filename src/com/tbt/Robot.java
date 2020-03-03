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
    public SensorMode colourSensor;
    public EV3LargeRegulatedMotor motorRight;
    public EV3LargeRegulatedMotor motorLeft;
    private SampleProvider gyroSensor;
    private float[] angleSample;
    private double[] bayesianProbabilties;


    private RobotMap map;

    public Robot(){
        //Setup Motors
        motorRight = new EV3LargeRegulatedMotor(MotorPort.A);
        motorLeft = new EV3LargeRegulatedMotor(MotorPort.B);
        gyroSensor = new EV3GyroSensor(SensorPort.S1).getAngleMode();
        colourSensor = new EV3ColorSensor(SensorPort.S2).getRedMode();

        angleSample =new float[gyroSensor.sampleSize()];
        motorLeft.setSpeed(90);
        motorRight.setSpeed(90);

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

    public void moveForward(double distance) {
        int angleToRotate = (int) (distance * 360/17.28);
        motorRight.rotate(angleToRotate, true);
        motorLeft.rotate(angleToRotate);
    }

    public static void main(String[] args) {
        Robot robot = new Robot();
        int bayesionStripLocation = BayesianLocalisation.localise(robot);
        Button.waitForAnyPress();
        RobotMap map = new RobotMap(125, 4);

    }
}
