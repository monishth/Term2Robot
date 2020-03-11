package com.tbt;

import java.util.ArrayList;
import java.util.Arrays;

public class RectangleObstacle extends Obstacle {

    public RectangleObstacle(RobotMap map, int x1, int y1, int x2, int y2){

        if(x1 < x2){
            for(int i = x1; i <= x2; i++){
                if(y1 < y2){
                    obstacleNodes.addAll(Arrays.asList(map.grid[i]).subList(y1, y2 + 1));
                }else{
                    obstacleNodes.addAll(Arrays.asList(map.grid[i]).subList(y2, y1 + 1));
                }
            }
        }else{
            for(int i = x2; i <= x1; i++){
                if(y1 < y2){
                    obstacleNodes.addAll(Arrays.asList(map.grid[i]).subList(y1, y2 + 1));
                }else{
                    obstacleNodes.addAll(Arrays.asList(map.grid[i]).subList(y2, y1 + 1));
                }
            }
        }
    }

    public RectangleObstacle(RobotMap map,double x1cm, double y1cm, double x2cm, double y2cm){
        this(map, RobotMap.cmToNodeValue(x1cm), RobotMap.cmToNodeValue(y1cm), RobotMap.cmToNodeValue(x2cm), RobotMap.cmToNodeValue(y2cm));
    }
}
