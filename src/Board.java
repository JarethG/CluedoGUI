import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Board extends GUI {
    int tileSize = 32;
    int roll;
    int turn;
    Player player;
    Tile[][]tiles = new Tile[24][25];
    Set<Tile> visited = new HashSet<Tile>();
    Point[] roomPoints = new Point[]{new Point(4,3),new Point(12,5),new Point(21,3),new Point(4,13),new Point(20,10),
            new Point(3,22),new Point(12,22),new Point(20,17),new Point(20,23)};

    /**
     * Board constructer, calls GUI super
     */
    public Board(){
        super();
    }

    /**
     * moves player to new tile and updates steps left and tiles visited
     * @param x
     * @param y
     */
    public void move(int x, int y){
        tiles[x][y].setVisited(true);
        visited.add(tiles[player.getX()][player.getY()]);
        player.setTileType(tiles[x][y].toString());
        player.setPos(x,y);
        roll--;
    }

    /**
     * redraws board,players, weapons, and tiles visited
     * @param g
     */
    @Override
    protected void redraw(Graphics g) {
        g.drawImage(board,0,0,null);
        for(Player p : players){
            p.draw(g);
        }
        for(Weapon w : weapons){
            w.draw(g);
        }
        for(Tile t : visited){
            g.fillRect(t.getX()*32,t.getY()*32,32,32);
        }

    }

    /**
     * redraws the second canvas holding cards
     * @param g
     */
    @Override
    protected void redrawHand(Graphics g) {
        if(player==null){return;}
        if(showCards) {
            ArrayList<Card> hand = player.getMyHand();
            for (int i = 0; i < hand.size(); i++){
                g.drawImage(hand.get(i).getGraphic(), 0, i * 70, null);
            }
            ArrayList<Card> seen = player.getCardsSeen();
            for(int i = 0; i < seen.size(); i++){
                g.drawImage(seen.get(i).getGraphic(),100,i * 70,null);
            }
        }
        showCards=!showCards;
    }

    /**
     * handles all mouse clicks on main canvas
     * @param e
     */
    @Override
    protected void onClick(MouseEvent e) {
        if(roll == 0){
            JOptionPane.showMessageDialog(game,player + " hasnt rolled the dice.");
            return;
        }
        int x = e.getX()/tileSize;
        int y = e.getY()/tileSize;
        Tile next = tiles[x][y];
        if(next.isVisited())return;

        String tile = player.getTileType();
        switch(tile){
            case "HallTile" ://is on floor tile
                if(isAdjacent(x,y) && (isHallTile(tiles[x][y])||isDoor(tiles[x][y]))){
                    move(x,y);
                }break;
            case "RoomTile" ://is on room tile
                if(isDoor(tiles[x][y]) && isConnected()){
                    move(x,y);
                }break;
            case "EntryTile" ://is on a door
                if(isRoom(tiles[x][y]) && isConnected()) {
                    move(x, y);
                    int result = JOptionPane.showConfirmDialog(null,"Would you like to stay",
                            "You are in a room",JOptionPane.YES_NO_OPTION);
                    if(result == 0){
                        roll=0;
                    }
                }
                else if(isAdjacent(x,y)){
                    move(x,y);
                }break;
        }

        if(roll == 0) {
            redraw();
            if (player.getTileType() == "RoomTile" && player.isPlaying())
                makeSuggestion();
            else
                nextPlayersTurn();
        }
    }

    /**
     * handles dice rolling
     */
    @Override
    protected void roll() {
        if(roll!=0){
            JOptionPane.showMessageDialog(game,"you have already rolled, you have " + roll + " moves left",null, 2);
            return;
        }
        roll = (int)(Math.random()*6+Math.random()*6);
        JOptionPane.showMessageDialog(game,player + " rolled a "+ roll);
    }

    @Override
    protected void endTurn(){
        roll=0;
        if (player.getTileType() == "RoomTile" && player.isPlaying())
            makeSuggestion();
        else
            nextPlayersTurn();
    }

    /**
     * creates new game with "i" numbers of players
     * @param i
     */
    @Override
    protected void newGame(int i) {
        possibleChars.clear();
        players = new ArrayList<>();
        for(int j = 0; j < i; j++){
            players.add(choosePlayers());
        }
        player = players.get(turn);
        tiles=loadBoard();
        setMurderScenario();;
        dealCards(players);
        scatterWeapons();
        redraw();
        roll = 0;
        turn = 0;
    }

    /**
     * put each weapon in randomly chosen room at the start of the game
     */
    public void scatterWeapons(){
        ArrayList<Integer>nums = new ArrayList<Integer>();
        for(int i = 0;i<9;i++){
            nums.add(i);
        }
        for(Weapon c : weapons){
           Point p = roomPoints[nums.remove((int)(Math.random()*nums.size()))];
           tiles[p.x][p.y].setVisited(true);
            c.setPos(p.x,p.y);

        }
    }


    /**
     * if the selected tile is a room that can be reached, return true
     * @param tile
     * @return
     */
    public boolean isRoom(Tile tile){//tile is the tile you are moving to
        if(tile instanceof RoomTile
                && player.getTileType() == "EntryTile"
                && ((RoomTile) tile).getRoom()==
                ((EntryTile)tiles[player.getX()][player.getY()]).getRoom()
        )
        return true;
        return false;
    }

    /**
     * if the selected tile is a door that can be reached,return true
     * @param tile
     * @return
     */
    public boolean isDoor(Tile tile){
        if(player.getTileType()=="HallTile" && tile instanceof EntryTile){
            return true;
        }
        if(tile instanceof EntryTile
                && player.getTileType() == "RoomTile"
                &&((EntryTile) tile).getRoom()==((RoomTile)tiles[player.getX()][player.getY()]).getRoom()
        )
        return true;
        return false;
    }

    /**
     * if tile is a floor tile, return true
     * @param tile
     * @return
     */
    public boolean isHallTile(Tile tile){
        if(tile instanceof HallTile)
            return true;
        return false;
    }

    public boolean isConnected(){
        return true;
    }

    /**
     * if the clicked tile is adjacent to the player, return true
     * @param x
     * @param y
     * @return
     */
    public boolean isAdjacent(int x, int y){
       if((Math.abs(player.getX()-x)==1 && player.getY()-y==0) ||
               (Math.abs(player.getY()-y)==1 && player.getX()-x==0))
        return true;
       return false;
    }

    /**
     * give the option to make a suggestion
     */
    public void makeSuggestion(){
        int result = JOptionPane.showConfirmDialog(null,"Would you like to make a suggestion",
                "You have no more moves left",JOptionPane.YES_NO_OPTION);
        if(result == JOptionPane.YES_OPTION){
            String character = getRadioSelection(characterString,"who did it?");
            String weapon = getRadioSelection(weaponString,"what weapon did " +character+ " use?");
            String room = ((RoomTile)tiles[player.getX()][player.getY()]).getRoom();
            moveItems(character,weapon);
            for(Player p : players){
                if(p != player){
                    Object[] potential = p.contains(character,weapon,room).toArray();
                    if(potential.length!=0){
                        int revealed = JOptionPane.showOptionDialog(
                                game,p + ", which card would you like to show "+player+"?","",0,1,null,
                                potential, null);
                        player.addSeenCard((Card)potential[revealed]);
                    }
                }
            }
        }
      //  else if(result == JOptionPane.NO_OPTION){
            makeAccusation();
      //  }
    }

    /**
     * give the option to make an accusation
     */
    public void makeAccusation(){
        int result = JOptionPane.showConfirmDialog(null,"Would you like to make an accusation",
                "Hint: accusation can get you in trouble",JOptionPane.YES_NO_OPTION);
        if(result == JOptionPane.YES_OPTION){
            String character = getRadioSelection(characterString,"who do you think did it?");
            String weapon = getRadioSelection(weaponString,"what weapon did " +character+ " use?");
            String room = getRadioSelection(roomString,"Room did they do it in?");
            if(murder[0]== character && murder[1] == weapon && murder[2] == room){
                //you win
                JOptionPane.showMessageDialog(game,"you win",null, 2);
            }else {
                JOptionPane.showMessageDialog(game,"well, you failed. Better luck next time",null, 2);
                player.setPlaying(false);}
        }
        else if(result == JOptionPane.NO_OPTION){
            nextPlayersTurn();
        }
    }

    /**
     * change turn to the next player.
     */
    public void nextPlayersTurn(){
        for(Tile t : visited){
            t.setVisited(false);
        }
        visited.clear();
        turn = (turn+=1)%players.size();
        player = players.get(turn);
        JOptionPane.showMessageDialog(game,"it is "+ player + "'s turn");
        showCards=false;
        redrawHand();

    }

    public void moveItems(String character,String weapon){

        Point roomPoint = new Point(player.getX(),player.getY());

        for(Weapon w : weapons){
            if(w.getCardName() == weapon){
                Point newPos  = findSpace(roomPoint);
                if(newPos!=null) {
                    tiles[w.x][w.y].setVisited(false);
                    w.setPos(newPos.x, newPos.y);
                    tiles[newPos.x][newPos.y].setVisited(true);
                }
            }
        }


        for(Player p  : players){
            if(p.getCharacter() == character){
                Point newPos = findSpace(roomPoint);
                if(newPos!=null) {
                    tiles[p.getX()][p.getY()].setVisited(false);
                    p.setPos(newPos.x, newPos.y);
                    p.setTileType("Room Tile");
                    tiles[newPos.x][newPos.y].setVisited(true);
                }
            }
        }
    }

    public Point findSpace(Point point){

        Tile tile = tiles[point.x][point.y];
        Point space = null;
        if(tile != null && (tile instanceof RoomTile) && !tile.isVisited()) {

            if (!tile.isVisited()) {
                return point;
            }


            space = findSpace(new Point(point.x + 1, point.y));
            if (space == null) space = findSpace(new Point(point.x - 1, point.y));
            if (space == null) space = findSpace(new Point(point.x, point.y + 1));
            if (space == null) space = findSpace(new Point(point.x, point.y - 1));

        }
        return space;
    }

}
