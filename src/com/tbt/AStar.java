package com.tbt;

import java.util.*;

public class AStar {


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

    public static double heuristic(Node node, Node goal){
        return Math.abs(node.x - goal.x) + Math.abs(node.y - goal.y); //manhattan
    }
    //astar test
    public static void main(String[] args) {
        RobotMap map = new RobotMap(RobotMap.BOARD_LENTH, RobotMap.NODE_LENGTH);
        System.out.println("Map created");
        map.addObstacle(new DiagonalLineObstacle(map, 38.0, 85.0, 120.0, 0.0)); //TODO start using new LineObstacle
        // map.printBoard();
        System.out.println("wall created");

        Node endNode = AStarSearch(map.grid[RobotMap.cmToNodeValue(52.5)][RobotMap.cmToNodeValue(2.5)], map.grid[RobotMap.cmToNodeValue(120)][RobotMap.cmToNodeValue(120)]);
        ArrayList<Node.Direction> endPath = directionsFromPath(pathFromLastNode(endNode));
        System.out.println(Arrays.toString(endPath.toArray()));

        /*RobotMap map = new RobotMap(RobotMap.BOARD_LENTH, RobotMap.NODE_LENGTH);
        map.addDiagonalLineObstacle(41.7, 81.3, 120, 0);
        map.printBoard();
        Node endNode = AStarSearch(map.grid[RobotMap.cmToNodeValue(30)][RobotMap.cmToNodeValue(32)], map.grid[RobotMap.cmToNodeValue(125-55)][RobotMap.cmToNodeValue(125-5)]);
        ArrayList<Node.Direction> endPath = directionsFromPath(pathFromLastNode(endNode));
        System.out.println("Start node: 0,0");
        for (Node.Direction dir : endPath){
            System.out.println(dir);
        }
        System.out.println("End Node: " + endNode);*/
    }
}
