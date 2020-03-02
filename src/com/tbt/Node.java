package com.tbt;

public class Node implements Comparable<Node>{
    public int x;
    public int y;

    public double f;
    public double g;
    public double h;

    public Node parent;
    public Node[] neighbours;

    public Node(int x, int y, double f, double g, double h){
        this.x = x;
        this.y = y;
        this.f = f;
        this.g = g;
        this.h = h;
    }

    public int compareTo(Node other){
        return (this.f < other.f) ? -1 : (this.f > other.f) ? 1:0;
    }


}
