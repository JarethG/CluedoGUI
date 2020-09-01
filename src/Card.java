import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Card {

    private String cardName;
    private BufferedImage graphic;

    /**
     * card constructer
     * @param name
     */
   Card(String name){
       BufferedImage img = null;
       try {
           InputStream input = GUI.class.getResourceAsStream("/"+name+".png");
           img = ImageIO.read(input);
       }catch(IOException e){
           System.out.println(name + " does not exist");
       }
        cardName = name;
       graphic=img;
    }

    /**
     * returns card name
     * @return
     */
    public String getName() {
        return cardName;
    }

    /**
     * retruns cards graphic
     * @return
     */
    public BufferedImage getGraphic(){
       return graphic;
    }

    @Override
    public String toString(){
       return cardName;
    }

}
