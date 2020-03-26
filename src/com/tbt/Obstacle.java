package com.tbt;

import java.util.ArrayList;
import java.util.List;

/**
 * abstract class to superclass all obstacles as they just need a list of obstacles
 */
public abstract class Obstacle {
    protected List<Node> obstacleNodes = new ArrayList<>();

}
