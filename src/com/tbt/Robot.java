package com.tbt;

import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.SampleProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.tbt.AStar.*;
import static com.tbt.RobotMap.NODE_LENGTH;

public class Robot {
    public SensorMode colourSensor;
    public EV3LargeRegulatedMotor motorRight;
    public EV3LargeRegulatedMotor motorLeft;
    private EV3GyroSensor gyroSensor;
    private float[] angleSample;
    private double[] bayesianProbabilties;


    private RobotMap map;

    public Robot(){
        //Setup Motors
        motorRight = new EV3LargeRegulatedMotor(MotorPort.A);
        motorLeft = new EV3LargeRegulatedMotor(MotorPort.B);
        gyroSensor = new EV3GyroSensor(SensorPort.S1);
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
                currentDirection=nextMovement;
                if (directionsAsList.indexOf(currentDirection) % 2 == 0) {
                    moveForward(NODE_LENGTH);
                }else{
                    moveForward(NODE_LENGTH*1.4142136);
                }
            }

        }
    }

    private void rotate45(int n) {
        SampleProvider gyroSampleProvider = gyroSensor.getAngleMode();
        gyroSampleProvider.fetchSample(angleSample, 0);
        float goalAngle = n*45+angleSample[0];
        float kp =  0.8f;
        System.out.println("Error before reset: " + angleSample[0]);
        //gyroSensor.reset();

        System.out.println("Error after reset: " + angleSample[0]);
        float error = goalAngle - angleSample[0];
        System.out.println("Error: " + error);
        while(Math.abs(error) > 0.5){

            motorLeft.setSpeed(40 + Math.abs(error*kp));
            motorRight.setSpeed(40 + Math.abs(error*kp));

            if(error < 0){
                motorRight.backward();
                motorLeft.forward();
            }else{
                motorLeft.backward();
                motorRight.forward();
            }
            gyroSampleProvider.fetchSample(angleSample, 0);
            error = goalAngle - angleSample[0];
            System.out.println("Angle: " + angleSample[0]+ " Error: " + error);
        }
        motorRight.stop(true);
        motorLeft.stop();
    }

    public void moveForward(double distance) {
        int angleToRotate = (int) (distance * 360/17.28);
        motorRight.rotate(angleToRotate, true);
        motorLeft.rotate(angleToRotate);
    }

    public static void main(String[] args) {
        Robot robot = new Robot();
        System.out.println("Turn Right 90 Degrees");
        /*robot.rotate45(2);
        Button.waitForAnyPress();
        robot.rotate45(-4);*/
        //int bayesionStripLocation = BayesianLocalisation.localise(robot);
        RobotMap map = new RobotMap(125, NODE_LENGTH);
        //map.addRectangleObstacle(1,0,4,5);
        Node endNode = AStarSearch(map.grid[0][0], map.grid[15][4]);
        Button.waitForAnyPress();
        ArrayList<Node.Direction> endPath = directionsFromPath(pathFromLastNode(endNode));
        robot.followDirectionList(endPath, Node.Direction.S);

    }
}
