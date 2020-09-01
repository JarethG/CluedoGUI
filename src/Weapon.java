import java.awt.*;
import java.awt.image.BufferedImage;

public class Weapon {

    public int x;
    public int y;
    private String cardName;
    private BufferedImage graphic;

    /**
     * weapon constructer
     * @param aGraphic
     * @param aName
     */
    public Weapon (BufferedImage aGraphic,String aName){
        cardName=aName;
        graphic=aGraphic;
    }

    /**
     * sets the weapons position
     * @param ax
     * @param ay
     */
    public void setPos(int ax,int ay){
        x=ax;y=ay;
    }

    /**
     * returns weapon position as a point
     * @return
     */
    public Point getPos(){
        return new Point(x,y);
    }

    /**
     * draws the weapon at its current location
     * @param g
     */
    public void draw(Graphics g){
        g.drawImage(graphic,x*32,y*32,null);
    }

    /**
     * returns the name as a string
     * @return
     */
    public String getCardName(){
        return cardName;
    }
}
