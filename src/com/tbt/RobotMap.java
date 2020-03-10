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
            obstacle.neighbours.clear();
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
        addRectangleObstacle(cmToNodeValue(x1cm), cmToNodeValue(y1cm), cmToNodeValue(x2cm), cmToNodeValue(y2cm));
    }

    public void addDiagonalLineObstacle(int x1, int y1, int x2, int y2){
        ArrayList<Node> obstacleNodes = new ArrayList<>();
        System.out.println("coordinates of obstacle: " + x1 + "," + y1 + "x" + x2 + "," + y2);
        for(int i = x1; i <=x2; i++){
                obstacleNodes.add(grid[i][y1]);
                obstacleNodes.add(grid[i+1][y1]);
                y1--;
        }
        System.out.println(obstacleNodes.size());
        addObstacle(obstacleNodes);
    }
    public void addDiagonalLineObstacle(double x1cm, double y1cm, double x2cm, double y2cm){
        addDiagonalLineObstacle(cmToNodeValue(x1cm), cmToNodeValue(y1cm), cmToNodeValue(x2cm), cmToNodeValue(y2cm));
    }

    public void addCylinderObstacle(int x, int y, int nodeRadius){
        ArrayList<Node> obstacleNodes = new ArrayList<>();
        obstacleNodes.add(grid[x][y]);
        for(int i = 1; i < nodeRadius; i++){
            for(Node node : obstacleNodes){
                for(Node neighbour : node.neighbours){
                    if(!obstacleNodes.contains(neighbour)) obstacleNodes.add(neighbour);
                }
            }
        }
    }

    public void addCylinderObstacle(double xcm, double ycm, double cmRadius){
        addCylinderObstacle(cmToNodeValue(xcm), cmToNodeValue(ycm), cmToNodeValue(cmRadius));
    }

    public static int cmToNodeValue(double cm){
        return (int) Math.round(cm/NODE_LENGTH);
    }

    public void printBoard(){
        System.out.println();
        for(int i = 0; i < grid.length; i++){
            for(int j = grid.length-1; j >= 0; j--){
                System.out.print((grid[i][j].neighbours.isEmpty()) ? "X" : "O");
            }
            System.out.println();
        }
    }


}
