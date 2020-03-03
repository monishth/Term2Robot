package com.tbt;

import java.util.ArrayList;
import java.util.EnumMap;

public class Node implements Comparable<Node>{
    public enum Direction {
        N,NE,E,SE,S,SW,W,NW
    }

    public static final EnumMap<Direction, int[]> directionVectors;
    static {
        directionVectors = new EnumMap<>(Direction.class);
        directionVectors.put(Direction.N, new int[]{0, 1});
        directionVectors.put(Direction.NE, new int[]{1, 1});
        directionVectors.put(Direction.E, new int[]{1, 0});
        directionVectors.put(Direction.SE, new int[]{1, -1});
        directionVectors.put(Direction.S, new int[]{0, -1});
        directionVectors.put(Direction.SW, new int[]{-1, -1});
        directionVectors.put(Direction.W, new int[]{-1, 0});
        directionVectors.put(Direction.NW, new int[]{-1, 1});
    }

    public int x;
    public int y;

    public double f;
    public double g;
    public double h;

    public Node parent;
    public ArrayList<Node> neighbours;

    public Node(int x, int y, double f, double g, double h){
        this.x = x;
        this.y = y;
        this.f = f;
        this.g = g;
        this.h = h;
        this.neighbours = new ArrayList<>();
    }
    public Node(int x, int y){
        this(x, y, 0,0,0);
    }

    public int compareTo(Node other){
        return (this.f < other.f) ? -1 : (this.f > other.f) ? 1:0;
    }

    @Override
    public String toString() {
        return x + "," + y;
    }
}
