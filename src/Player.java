import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Player {

    private final String name;
    private final String character;
    private BufferedImage graphic;

    private String tileType = "HallTile";
    private int x;
    private int y;
    private int scale = 32;

    private ArrayList<Card> myHand = new ArrayList<Card>();
    private ArrayList<Card> cardsSeen = new ArrayList<Card>();

    private boolean isPlaying;

    Player(String aName,String aCharacter,BufferedImage aGraphic){
        name=aName;
        character=aCharacter;
        graphic = aGraphic;
        switch (character) {
            case "Miss Scarlett":
                x=7;y=24;
                break;
            case "Colonel Mustard":
                x=0;y=17;
                break;
            case "Mrs. White":
                x=9;y=0;
                break;
            case "Mr. Green":
                x=14;y=0;
                break;
            case "Mrs. Peacock":
                x=23;y=6;
                break;
            case "Professor Plum":
                x=23;y=19;
                break;
        }
        isPlaying=true;
    }

    public void addCard(Card aCard){
        myHand.add(aCard);
    }

    public void addSeenCard(Card aCard) {cardsSeen.add(aCard);}

    public String getName() {
        return name;
    }

    public String getCharacter() {
        return character;
    }

    public void draw(Graphics g){
        g.drawImage(graphic,x*scale,y*scale,null);
    }

    public void setPos(int setX, int setY){
        x=setX;y=setY;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }

    public String getTileType(){
        return tileType;
    }

    public void setTileType(String tileType) {
        this.tileType = tileType;
    }

    public void setPlaying(boolean b){
        isPlaying=b;
    }

    public boolean isPlaying(){
        return isPlaying;
    }

    public ArrayList<Card> getMyHand(){
        return myHand;
    }

    public ArrayList<Card> getCardsSeen(){
        return cardsSeen;
    }

    public ArrayList<Card> contains(String c,String w,String r){
        ArrayList<Card> returns = new ArrayList<>();
        for(Card card : myHand){
            if(card.getName() == c ||
                card.getName() == w ||
                    card.getName() == r){
                returns.add(card);
            }
        }
        return returns;
    }
    @Override
    public String toString() {
        return character;
    }
}
