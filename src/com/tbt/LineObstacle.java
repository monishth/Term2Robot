package com.tbt;

public class LineObstacle extends Obstacle {

    /**
     * Constructor to find all obstacle nodes using equation of a line and limits from 2 points
     *
     * @param map RobotMap object
     * @param x1 x point 1
     * @param y1 y point 1
     * @param x2 x point 2
     * @param y2 y point 2
     * @param width width of wall on each y
     */
    public LineObstacle(RobotMap map, int x1, int y1, int x2, int y2, int width){
        //line is vertical
        if(x2-x1 == 0) {
            if(y1 > y2){
                for(int i = y2; i <=y1; i++){
                    this.addNodeWidth(map, x1, i, width);
                }
            }else{
                for(int i = y1; i <=y2; i++){
                    this.addNodeWidth(map, x1, i, width);
                }
            }
        }
        //any other line
        else{
            double gradient = (((double)y2)-y1)/(x2-x1);
            System.out.println(gradient);
            double c = y1 - gradient*x1;

            if(x1 > x2){
                int temp = x1;
                x1 = x2;
                x2 = temp;
            }

            for(int i = x1; i <=x2; i++){
                int y = (int) Math.round(gradient*i + c);
                System.out.println(y);
                this.addNodeWidth(map, i, y, width);
                if(Math.abs(gradient) > 1 && i > 0 && y > 0 && y+1 < map.grid.length){ //When the gradient is larger than one there may be y values without points that leave gaps in the obstacle
                    if(gradient > 0){
                        this.addNodeWidth(map, i-1,y-1, width);
                    }else{
                        this.addNodeWidth(map, i-1, y+1, width);
                    }
                }
            }

        }
    }

    public LineObstacle(RobotMap map, double x1cm, double y1cm, double x2cm, double y2cm, double width){
        this(map, RobotMap.cmToNodeValue(x1cm), RobotMap.cmToNodeValue(x1cm), RobotMap.cmToNodeValue(x1cm), RobotMap.cmToNodeValue(x1cm),RobotMap.cmToNodeValue(x1cm));
    }

    /**
     * adds node to obstacle with neighbouring nodes to a width
     *
     * @param map RobotMap object
     * @param x x coordinate of center
     * @param y y coordinate of center
     * @param width in x nodes on y
     */
    private void addNodeWidth(RobotMap map, int x, int y, int width){
        for(int i = 0; i < width; i++){
            if(x+i < map.grid.length) {

                obstacleNodes.add(map.grid[x+i][y]);
            }
        }
    }
}
