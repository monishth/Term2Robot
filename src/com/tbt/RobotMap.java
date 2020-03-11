package com.tbt;

import java.util.ArrayList;

public class RobotMap {
    public static final double NODE_LENGTH = 2.5;
    public static final double BOARD_LENTH = 125;

    public Node[][] grid;
    public RobotMap(int nodes_per_edge){

        grid = new Node[nodes_per_edge][nodes_per_edge];

        for(int i = 0; i < nodes_per_edge; i++){
            for (int j = 0; j <nodes_per_edge;j++){
                Node newNode = new Node(i, j);
                grid[i][j] = newNode;
            }
        }


        for(int i = 0; i < nodes_per_edge; i++){
            for (int j = 0; j <nodes_per_edge;j++){
                Node currentNode = grid[i][j];

                for(Node.Direction direction : Node.Direction.values()){
                    int[] directionVector = Node.directionVectors.get(direction);
                    int neighbourX = currentNode.x + directionVector[0];
                    int neighbourY = currentNode.y + directionVector[1];

                    if(neighbourX >= 0 && neighbourX<nodes_per_edge && neighbourY >= 0 && neighbourY<nodes_per_edge){
                        currentNode.neighbours.add(grid[neighbourX][neighbourY]);
                    }

                }

            }
        }

    }

    public RobotMap(double lengthBoard, double cmPerNode){
        this((int) Math.round(lengthBoard/cmPerNode));
    }

    public void addObstacle(Obstacle obstacle){
        for(Node obstacleNode : obstacle.obstacleNodes){
            for(Node neighbour : obstacleNode.neighbours){
                neighbour.neighbours.remove(obstacleNode); //Removes the obstacle nodes as possible neighbour around the obstacles
            }
            obstacleNode.neighbours.clear();
        }
    }

    public static int cmToNodeValue(double cm){
        return (int) Math.round(cm/NODE_LENGTH);
    }

    public void printBoard(){
        System.out.println();
        for(int j = grid.length-1; j >= 0; j--){
            for(int i = 0; i < grid.length; i++){

                System.out.print((grid[i][j].neighbours.isEmpty()) ? " X " : " O ");
            }
            System.out.println();
        }
    }



}
