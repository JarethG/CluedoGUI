public abstract class Tile {
    private int x;
    private int y;
    private boolean visited;

    public Tile(int aX,int aY){
        x=aX;y=aY;visited=false;
    }

    public void setVisited(boolean visit){
        visited = visit;
    }

    public boolean isVisited(){
        return visited;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }


}
