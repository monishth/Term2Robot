package com.tbt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.PriorityQueue;

public class AStar {


    public static Node AStarSearch(Node start, Node goal){
        PriorityQueue<Node> openList = new PriorityQueue<>();
        ArrayList<Node> closedList = new ArrayList<>();

        closedList.add(start);
        Node searchNode = null;
        while(!openList.isEmpty() && closedList.contains(goal)){
            searchNode = openList.poll();

            for(Node neighbour : searchNode.neighbours){
                if(openList.contains(neighbour)){
                    double g = searchNode.g +1;
                    double h = heuristic(neighbour, goal);
                    double f = g+h;
                    if(f < neighbour.f){
                        neighbour.f = f;
                        neighbour.g = g;
                        neighbour.h = h;
                        neighbour.parent = searchNode;
                    }
                }else{
                    neighbour.g = searchNode.g +1;
                    neighbour.h = heuristic(neighbour, goal);
                    neighbour.f = neighbour.h + neighbour.g;
                    neighbour.parent = searchNode;
                }
            }
            openList.remove(searchNode);
            closedList.add(searchNode);
        }

        if(closedList.contains(goal)){
            return goal;
        }else{
            return null;
        }

    }

    public static ArrayList<Node> pathFromLastNode(Node lastNode){
        ArrayList<Node> backwardsPath = new ArrayList<>();
        Node currentNode = lastNode;
        while(currentNode.parent != null){
            backwardsPath.add(lastNode);
            currentNode = currentNode.parent;
        }
        backwardsPath.add(currentNode);
        Collections.reverse(backwardsPath);
        return backwardsPath;
    }

    public static ArrayList<Node.Direction> directionsFromPath(ArrayList<Node> nodePath){
        ArrayList<Node.Direction> directions = new ArrayList<>();
        for(int i = 0; i < nodePath.size()-1;i++){
            Node startNode = nodePath.get(i);
            Node endNode = nodePath.get(i+1);
            int x = endNode.x-startNode.x;
            int y = endNode.y-startNode.y;
            if(x == 0){
                if (y > 0) {
                    directions.add(Node.Direction.N);
                } else {
                    directions.add(Node.Direction.S);
                }
            }

        }
        return directions;
    }

    public static double heuristic(Node node, Node goal){
        return Math.abs(node.x - goal.x) + Math.abs(node.y - goal.y); //manhattan
    }
}
