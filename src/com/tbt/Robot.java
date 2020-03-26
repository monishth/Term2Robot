package com.tbt;

import lejos.hardware.Button;
import lejos.hardware.Sound;
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
        //Setup Sensors
        gyroSensor = new EV3GyroSensor(SensorPort.S1);
        touchSensor = new EV3TouchSensor(SensorPort.S3); //TODO check the sensor port, check all these parts initialise etc, do I need to initalise here?
        colourSensor = new EV3ColorSensor(SensorPort.S2);
        //Initiate Sample Arrays
        angleSample =new float[gyroSensor.sampleSize()];
        colourSample = new float[colourSensor.sampleSize()];
        touchSample = new float[touchSensor.sampleSize()];
        //Set base speed and directions
        motorLeft.setSpeed(90);
        motorRight.setSpeed(90);
        currentDirection = Node.Direction.NE;

    }


    /**
     * method to give robot a list of directions that it follows. Keeps track of the robots facing direction
     *
     * @param directions directions to follow
     */
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

    /**
     * Rotates robot to a direction with respect to the current direction. Calculates the quickest direction to turn
     *
     * @param directionToFace direction to face
     */
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

    /**
     * Rotates robot irrespective of direction to a specific angle (increments of 45)
     *
     * @param n number of 45 degree angles to turn
     */
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

    /**
     * routine to enter box and sense colour of marking
     *
     * @return colour found in box
     */
    public int enterBox(){
        rotateTo(Node.Direction.E);
        motorLeft.setSpeed(50);
        motorRight.setSpeed(50);
        motorRight.forward();
        motorLeft.forward();
        colourSensor.setCurrentMode("ColorID");
        colourSensor.fetchSample(colourSample, 0);
        touchSensor.fetchSample(touchSample, 0);
        while(touchSample[0] == 0){ //whatever the measured value for green is TODO
            touchSensor.fetchSample(touchSample, 0);
        }
        motorRight.stop(true);
        motorLeft.stop();
        colourSensor.fetchSample(colourSample, 0);
        Sound.twoBeeps();

        if(colourSample[0] == 0.0){
            return 0;
        }else if(colourSample[0] == 2.0){
            return 1;
        }else{
            return -1;
        }
    }

    /**
     * moves robot by distance
     *
     * @param distance distance to move forward in cm
     */
    public void moveForward(double distance) {
        int angleToRotate = (int) (distance * 360/17.28);
        motorLeft.setSpeed(90);
        motorRight.setSpeed(90);
        motorRight.rotate(angleToRotate, true);
        motorLeft.rotate(angleToRotate);
    }

    /**
     * Creates plan for task 1 from localisation and predetermined path
     *
     * @param startingLocation index of location on bayesian strip with respect to grid
     * @param obstacle index of predetermined obstacle
     * @return path to box by list of directions
     */
    private ArrayList<Node.Direction> planTask1(double startingLocation, int obstacle) {
        RobotMap map = new RobotMap(RobotMap.BOARD_LENTH, RobotMap.NODE_LENGTH);
        map.addObstacle(new DiagonalLineObstacle(map, 38.0, 85.0, 120.0, 0.0)); //TODO start using new LineObstacle

        switch(obstacle){
            case 0:
                //map.addObstacle(new CylinderObstacle(map, 0,0,0));
                break;
            case 1:
                //map.addObstacle(new CylinderObstacle(map, 0,0,0));
                break;
            case 2:
                //map.addObstacle(new CylinderObstacle(map, 0,0,0));
                break;
        }
        //Node endNode = AStarSearch(map.grid[RobotMap.cmToNodeValue(startingLocation)][RobotMap.cmToNodeValue(startingLocation)], map.grid[RobotMap.cmToNodeValue(125-55)][RobotMap.cmToNodeValue(125-5)]);
        Node endNode = AStarSearch(map.grid[RobotMap.cmToNodeValue(30)][RobotMap.cmToNodeValue(32)], map.grid[RobotMap.cmToNodeValue(125-55)][RobotMap.cmToNodeValue(122.5)]);
        ArrayList<Node.Direction> endPath = directionsFromPath(pathFromLastNode(endNode));
        System.out.println(Arrays.toString(endPath.toArray()));
        return endPath;
    }

    /**
     * Creates plan for pathing from box to starting corner task 4
     *
     * @param colourSensed index of colour sensed inside box
     * @return path from box to starting corner
     */
    private ArrayList<Node.Direction> planTask4(int colourSensed){
        RobotMap map = new RobotMap(RobotMap.BOARD_LENTH, RobotMap.NODE_LENGTH);
        System.out.println("Map created");
        map.addObstacle(new DiagonalLineObstacle(map, 38.0, 85.0, 120.0, 0.0)); //TODO start using new LineObstacle
       // map.printBoard();
        System.out.println("wall created");

        if(colourSensed == 0){
            //map.addObstacle(new CylinderObstacle(map, 0,0,0));
        }else{
            //map.addObstacle(new CylinderObstacle(map, 0,0,0));
        }
        Node endNode = AStarSearch(map.grid[RobotMap.cmToNodeValue(52.5)][RobotMap.cmToNodeValue(2.5)], map.grid[RobotMap.cmToNodeValue(120)][RobotMap.cmToNodeValue(120)]);
        System.out.println("Search Done");

        ArrayList<Node.Direction> endPath = directionsFromPath(pathFromLastNode(endNode));
        System.out.println("Path created");

        System.out.println(Arrays.toString(endPath.toArray()));
        return endPath;
    }

    /**
     * main method which executes all the tasks in order
     */
    public static void main(String[] args) {
        Robot robot = new Robot();
        double startingLocation = ((BayesianLocalisation.findLocation(robot))*(1.75)-6);
        System.out.println("Task 1 Started");
        System.out.println("Robot Localised: (" + startingLocation + "cm, " + startingLocation + "cm)");
        System.out.println("Task 1 Complete");
        System.out.println("Task 2 Started");
        ArrayList<Node.Direction> task1Path = robot.planTask1(startingLocation, 1);
        System.out.println("Path to tunnel planned avoiding obstacle 0");
        System.out.println("Press to start");
        Button.waitForAnyPress();
        robot.followDirectionList(task1Path); //Go to tunnel
        System.out.println("Tunnel Reached");
        System.out.println("Task 2 Complete");
        System.out.println("Task 3 Started");
        int colourSensed = robot.enterBox();
        if(colourSensed == -1){
            System.out.println("Colour Sensing Failed");
            Button.waitForAnyPress();
            return;
        }
        Button.waitForAnyPress();
        robot.moveForward(-19);
        System.out.println("Task 3 Complete");
        System.out.println("Task 4 Started");
        robot.currentDirection = Node.Direction.W;
        ArrayList<Node.Direction> task4Path = robot.planTask4(0);
        System.out.println("Path to finish planned avoiding obstacle " + 0);
        System.out.println("Press to start");
        Button.waitForAnyPress();
        robot.followDirectionList(task4Path);
        System.out.println("Task 4 Complete");

    }
}
