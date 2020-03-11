package com.tbt;

import lejos.hardware.Button;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.MotorPort;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3GyroSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.SampleProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.tbt.AStar.*;

public class Robot {
    public EV3ColorSensor colourSensor;
    public EV3LargeRegulatedMotor motorRight;
    public EV3LargeRegulatedMotor motorLeft;
    private EV3GyroSensor gyroSensor;
    private EV3TouchSensor touchSensor;
    private float[] angleSample;
    private float[] colourSample;
    private float[] touchSample;


    private RobotMap map;
    private Node.Direction currentDirection;

    public Robot(){
        //Setup Motors
        motorRight = new EV3LargeRegulatedMotor(MotorPort.A);
        motorLeft = new EV3LargeRegulatedMotor(MotorPort.B);
        gyroSensor = new EV3GyroSensor(SensorPort.S1);
        touchSensor = new EV3TouchSensor(SensorPort.S3); //TODO check the sensor port, check all these parts initialise etc, do I need to initalise here?
        colourSensor = new EV3ColorSensor(SensorPort.S2);
        angleSample =new float[gyroSensor.sampleSize()];
        colourSample = new float[colourSensor.sampleSize()];
        touchSample = new float[touchSensor.sampleSize()];
        motorLeft.setSpeed(90);
        motorRight.setSpeed(90);
        currentDirection = Node.Direction.NE;

    }



    public void followDirectionList(List<Node.Direction> directions){
        List<Node.Direction> directionsAsList = Arrays.asList(Node.Direction.values());
        for(Node.Direction nextMovement : directions){
            System.out.println(nextMovement);
            if(currentDirection ==nextMovement){
                moveForward(RobotMap.NODE_LENGTH);
            }else{
                rotateTo(nextMovement);
                if (directionsAsList.indexOf(currentDirection) % 2 == 0) {
                    moveForward(RobotMap.NODE_LENGTH);
                }else{
                    moveForward(RobotMap.NODE_LENGTH*1.4142136);
                }
            }

        }
    }

    private void rotateTo(Node.Direction directionToFace) {
        List<Node.Direction> directionsAsList = Arrays.asList(Node.Direction.values());
        System.out.print(currentDirection + "->" + directionToFace + ": ");
        int currentIndex = directionsAsList.indexOf(currentDirection);
        int requiredDirectionIndex = directionsAsList.indexOf(directionToFace);
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
        currentDirection = directionToFace;
    }

    private void rotate45(int n) {
        SampleProvider gyroSampleProvider = gyroSensor.getAngleMode();
        gyroSampleProvider.fetchSample(angleSample, 0);
        System.out.println(n);

        float goalAngle = -1*(n*45)+angleSample[0];
        float kp =  0.8f;
        //System.out.println("Error before reset: " + angleSample[0]);
        //gyroSensor.reset();

        //System.out.println("Error after reset: " + angleSample[0]);
        float error = goalAngle - angleSample[0];
       // System.out.println("Error: " + error);
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
            //System.out.println("Angle: " + angleSample[0]+ " Error: " + error);
        }
        motorRight.stop(true);
        motorLeft.stop();
    }

    public void enterBox(){ 
        rotateTo(Node.Direction.E);
        motorLeft.setSpeed(20);
        motorRight.setSpeed(20);
        motorRight.forward();
        motorLeft.forward();
        colourSensor.setCurrentMode("ColorID");
        colourSensor.fetchSample(colourSample, 0);
        touchSensor.fetchSample(touchSample, 0);
        while(colourSample[0] != 0 || colourSample[0] != 2 || touchSample[0] != 1){ //whatever the measured value for green is TODO
            colourSensor.fetchSample(colourSample, 0);
            touchSensor.fetchSample(touchSample, 0);
        }
        motorRight.stop(true);
        motorLeft.stop();
    }

    public void moveForward(double distance) {
        int angleToRotate = (int) (distance * 360/17.28);
        motorLeft.setSpeed(90);
        motorRight.setSpeed(90);
        motorRight.rotate(angleToRotate, true);
        motorLeft.rotate(angleToRotate);
    }

    public static void main(String[] args) {
        Robot robot = new Robot();

        RobotMap map = new RobotMap(RobotMap.BOARD_LENTH, RobotMap.NODE_LENGTH);
        map.addDiagonalLineObstacle(38.0, 85.0, 120.0, 0.0);
        Node endNode = AStarSearch(map.grid[RobotMap.cmToNodeValue(30)][RobotMap.cmToNodeValue(32)], map.grid[RobotMap.cmToNodeValue(125-55)][RobotMap.cmToNodeValue(125-5)]);
        ArrayList<Node.Direction> endPath = directionsFromPath(pathFromLastNode(endNode));
        System.out.println(Arrays.toString(endPath.toArray()));
        Button.waitForAnyPress();
        robot.followDirectionList(endPath);

    }
}
