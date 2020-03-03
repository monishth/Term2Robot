package com.tbt;

public class RobotMap {
    public static final double NODE_LENGTH = 1.25;

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
}
