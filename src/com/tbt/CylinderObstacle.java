package com.tbt;

public class CylinderObstacle extends Obstacle{

    /**
     * Constructor to find all obstacle nodes in a cylinder shape(square with radius)
     *
     * @param map RobotMap object
     * @param x x coordinate center
     * @param y y coordinate center
     * @param nodeRadius radius(number of nodes in one direction
     */
    public CylinderObstacle(RobotMap map, int x, int y, int nodeRadius){
        obstacleNodes.add(map.grid[x][y]);
        for(int i = 1; i < nodeRadius; i++){
            for(Node node : obstacleNodes){
                for(Node neighbour : node.neighbours){
                    if(!obstacleNodes.contains(neighbour)) obstacleNodes.add(neighbour);
                }
            }
        }
    }

    public CylinderObstacle(RobotMap map, double xcm, double ycm, double cmRadius){
        this(map, RobotMap.cmToNodeValue(xcm), RobotMap.cmToNodeValue(ycm), RobotMap.cmToNodeValue(cmRadius));
    }
}
