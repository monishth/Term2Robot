package com.tbt;

import java.util.*;

public class AStar {


    /**
     * static method given a start node and an end node performs the A* algorithm with the manhatten heuristic
     *
     * @param start start node
     * @param goal goal node
     * @return goal node. All parents will be set to take the shortest path back
     */
    public static Node AStarSearch(Node start, Node goal){
        PriorityQueue<Node> openList = new PriorityQueue<>();
        ArrayList<Node> closedList = new ArrayList<>();

        openList.add(start);
        Node searchNode = null;
        while(!openList.isEmpty() && !closedList.contains(goal)){
            searchNode = openList.poll();
            openList.remove(searchNode);
            closedList.add(searchNode);
            System.out.println(searchNode);
            for(Node neighbour : searchNode.neighbours){
                if (!closedList.contains(neighbour)) {
                    if(openList.contains(neighbour)){
                        double g = searchNode.g + (neighbour.x-searchNode.x == 0 || neighbour.y-searchNode.y==0 ? 1 : 1.4142136);
                        double h = heuristic(neighbour, goal);
                        double f = g+h;
                        if(f < neighbour.f){
                            neighbour.f = f;
                            neighbour.g = g;
                            neighbour.h = h;
                            neighbour.parent = searchNode;
                            openList.remove(neighbour);
                        }
                    }else{
                        neighbour.g = searchNode.g + (neighbour.x-searchNode.x == 0 || neighbour.y-searchNode.y==0 ? 1 : 1.4142136);
                        neighbour.h = heuristic(neighbour, goal);
                        neighbour.f = neighbour.h + neighbour.g;
                        neighbour.parent = searchNode;
                    }
                    openList.add(neighbour);
                }

            }
        }

        if(closedList.contains(goal)){
            return goal;
        }else{
            return null;
        }

    }

    /**
     * create list from goal node
     *
     * @param lastNode goal node or last node in a path calculated by a*
     * @return list of nodes in the path from the first node to the last
     */
    public static ArrayList<Node> pathFromLastNode(Node lastNode){
        ArrayList<Node> backwardsPath = new ArrayList<>();
        Node currentNode = lastNode;
        while(currentNode.parent != null){
            backwardsPath.add(currentNode);
            currentNode = currentNode.parent;
        }
        backwardsPath.add(currentNode);
        Collections.reverse(backwardsPath);
        return backwardsPath;
    }

    /**
     * Takes deltas between node vectors to find list of directions
     *
     * @param nodePath list of nodes in path in order
     * @return list of directions
     */
    public static ArrayList<Node.Direction> directionsFromPath(ArrayList<Node> nodePath){
        ArrayList<Node.Direction> directions = new ArrayList<>();
        for(int i = 0; i < nodePath.size()-1;i++){
            Node startNode = nodePath.get(i);
            Node endNode = nodePath.get(i+1);
            int x = endNode.x-startNode.x;
            int y = endNode.y-startNode.y;
            for(Map.Entry<Node.Direction, int[]> direction : Node.directionVectors.entrySet()){
                if(direction.getValue()[0] == x && direction.getValue()[1] == y){
                    directions.add(direction.getKey());
                    break;
                }
            }

        }
        return directions;
    }

    /**
     *
     * manhatten heuristic
     *
     * @param node first node
     * @param goal second node
     * @return manhatten distance
     */
    public static double heuristic(Node node, Node goal){
        return Math.abs(node.x - goal.x) + Math.abs(node.y - goal.y); //manhattan
    }

}
