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

    public void addObstacle(ArrayList<Node> obstacleNodes){
        for(Node obstacle : obstacleNodes){
            for(Node neighbour : obstacle.neighbours){
                neighbour.neighbours.remove(obstacle); //Removes the obstacle nodes as possible neighbour around the obstacles
            }
        }
    }

    public void addRectangleObstacle(int x1, int y1, int x2, int y2){
        ArrayList<Node> obstacleNodes = new ArrayList<>();
        if(x1 < x2){
            for(int i = x1; i <= x2; i++){
                if(y1 < y2){
                    for (int j = y1; j <= y2;j++){
                        obstacleNodes.add(grid[i][j]);
                    }
                }else{
                    for (int j = y2; j <= y1;j++){
                        obstacleNodes.add(grid[i][j]);
                    }
                }
            }
        }else{
            for(int i = x2; i <= x1; i++){
                if(y1 < y2){
                    for (int j = y1; j <= y2;j++){
                        obstacleNodes.add(grid[i][j]);
                    }
                }else{
                    for (int j = y2; j <= y1;j++){
                        obstacleNodes.add(grid[i][j]);
                    }
                }
            }
        }

        addObstacle(obstacleNodes);
    }

    public void addRectangleObstacle(double x1cm, double y1cm, double x2cm, double y2cm){
        addRectangleObstacle(cmToNodeCoordinate(x1cm), cmToNodeCoordinate(y1cm), cmToNodeCoordinate(x2cm), cmToNodeCoordinate(y2cm));
    }

    public void addDiagonalLineObstacle(int x1, int y1, int x2, int y2){
        ArrayList<Node> obstacleNodes = new ArrayList<>();

        for(int i = x1; i<=x2; i++){
            for(int j = y1;j<=y2;i--){
                obstacleNodes.add(grid[i][j]);
                obstacleNodes.add(grid[i+1][j]);
            }
        }
    addObstacle(obstacleNodes);
    }
    public void addDiagonalLineObstacle(double x1cm, double y1cm, double x2cm, double y2cm){
        addDiagonalLineObstacle(cmToNodeCoordinate(x1cm), cmToNodeCoordinate(y1cm), cmToNodeCoordinate(x2cm), cmToNodeCoordinate(y2cm));
    }

    public int cmToNodeCoordinate(double cm){
        return (int) Math.round(cm/NODE_LENGTH);
    }


}
