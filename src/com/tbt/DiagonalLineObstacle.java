package com.tbt;


/**
 * Old diagonal line method of creating obstacle.
 * @deprecated  replaced by {@link LineObstacle}
 */
public class DiagonalLineObstacle extends Obstacle {

    public DiagonalLineObstacle(RobotMap map, int x1, int y1, int x2, int y2){
        for(int i = x1; i <=x2; i++){
            obstacleNodes.add(map.grid[i][y1]);
            obstacleNodes.add(map.grid[i+1][y1]);
            y1--;
        }
    }

    public DiagonalLineObstacle(RobotMap map, double x1cm, double y1cm, double x2cm, double y2cm){
        this(map, RobotMap.cmToNodeValue(x1cm), RobotMap.cmToNodeValue(y1cm), RobotMap.cmToNodeValue(x2cm), RobotMap.cmToNodeValue(y2cm));
    }
}
