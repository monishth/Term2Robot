package com.tbt;

import java.util.ArrayList;
import java.util.List;

public abstract class Obstacle {
    protected List<Node> obstacleNodes = new ArrayList<>();
    public List<Node> getObstacleNodes(){
        return obstacleNodes;
    }

}
