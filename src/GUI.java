import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public abstract class GUI {

    private static final int DEFAULT_FRAME_WIDTH = 1000;
    private static final int DEFAULT_FRAME_HEIGHT = 1000;
    JFrame game = new JFrame("Welcome to hell");
    public static String[] characterString = new String[]{"Miss Scarlett",
            "Colonel Mustard", "Mrs. White", "Mr. Green", "Mrs. Peacock", "Professor Plum"};
    public static String[] roomString = new String[]{"Kitchen",
            "BallRoom", "Conservatory", "Dining Room","Billiard Room", "Lounge",
            "Hall", "Library","Study",};
    public static String[] weaponString = new String[] {"Candlestick",
            "Dagger", "Lead Pipe", "Revolver", "Rope", "Spanner"};


    boolean showCards = true;


    public static ArrayList<String>possibleChars = new ArrayList<>();

    public static String[] murder;
    public static BufferedImage board = null;
    public static ArrayList<Player> players = new ArrayList<>();
    public static ArrayList<Weapon> weapons = new ArrayList<>();
    public static String dir = "C:/Users/jaret/IdeaProjects/CluedoGUI/";


    /**
     * GUI constructer
     */
    public GUI() {
        int SCALE = 4;
        try {
            InputStream input = GUI.class.getResourceAsStream("/namedBoard.png");
            BufferedImage img = ImageIO.read(input);

            board = new BufferedImage(SCALE * img.getWidth(null), SCALE * img.getHeight(null), BufferedImage.TYPE_INT_ARGB);
            Graphics2D grph = (Graphics2D) board.getGraphics();
            grph.scale(SCALE, SCALE);
            grph.drawImage(img, 0, 0, null);
            grph.dispose();
        } catch (IOException e) {
            System.out.println("no such image");
           // System.exit(0);
        }
        initialise();
        setupCanvas();
        // choosePlayers();
        //redraw();
    }

    protected abstract void redraw(Graphics g);
    protected abstract void redrawHand(Graphics g);

    protected abstract void onClick(MouseEvent e);

    protected abstract void roll();

    protected abstract void endTurn();

    public void redraw() {
        canvas.repaint();
    }

    public void redrawHand(){
        handCanvas.repaint();
    }

    protected abstract void newGame(int i);

    private JComponent canvas;
    private JComponent handCanvas;

    /**
     * set up the two canvases with respective listeners
     */
    public void setupCanvas() {
        canvas = new JComponent() {
            protected void paintComponent(Graphics g) {
                redraw(g);
            }
        };
        canvas.setPreferredSize(new Dimension(800, 800));
        canvas.setVisible(true);
        game.add(canvas);

        handCanvas = new JComponent() {
                    protected void paintComponent(Graphics g) {
                        redrawHand(g);
                    }
                };
        handCanvas.setPreferredSize(new Dimension(200, 800));
        handCanvas.setVisible(true);
        game.add(handCanvas,BorderLayout.EAST);


        canvas.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                onClick(e);
                redraw();
            }
        });

        canvas.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyReleased(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("Pressed " + e.getKeyChar());
            }
        });
    }

    /**
     * creates a new board of fixed layout.
     * @return
     */
    public Tile[][] loadBoard() {
        Tile[][] tiles = new Tile[24][25];
        String in =
                        "NNNNNNNNNONNNNONNNNNNNNN" +
                        "111111NOOO2222OOON333333" +
                        "111111OO22222222OO333333" +
                        "111111OO22222222OO333333" +
                        "111111OO22222222OO333333" +
                        "111111O@22222222@O#3333N" +
                        "N11111OO22222222OOOOOOOO" +
                        "OOOO!OOO22222222OOOOOOON" +
                        "NOOOOOOOO@OOOO@OOO555555" +
                        "44444OOOOOOOOOOOO%555555" +
                        "44444444OONNNNNOOO555555" +
                        "44444444OONNNNNOOO555555" +
                        "44444444$ONNNNNOOO555555" +
                        "44444444OONNNNNOOOOO*O%N" +
                        "44444444OONNNNNOOO888888" +
                        "44444444OONNNNNOO8888888" +
                        "NOOOOO$OOONNNNNO*8888888" +
                        "OOOOOOOOOOO&&OOOO8888888" +
                        "NOOOOO^OO777777OOO88888N" +
                        "6666666OO777777OOOOOOOOO" +
                        "6666666OO777777OO(OOOOON" +
                        "6666666OO777777OO9999999" +
                        "6666666OO777777OO9999999" +
                        "6666666OO777777OO9999999" +
                        "666666NON777777NON999999";
        int count = 0;

        for (int j = 0; j < 25; j++) {
            for (int i = 0; i < 24; i++) {
                if (count <= in.length()) {
                    char next = in.charAt(count);
                    if (next == 'N') {
                        tiles[i][j] = new HallTile(i, j);
                        tiles[i][j].setVisited(true);
                    } else if (next == 'O') {
                        tiles[i][j] = new HallTile(i, j);
                    } else if (isInt(next)) {
                        RoomTile RT = new RoomTile(i, j);
                        RT.setRoom(roomString[Integer.parseInt(String.valueOf(next))-1]);
                        tiles[i][j] = RT;
                    } else {
                        EntryTile ET = new EntryTile(i, j);
                        ET.setRoom(next);
                        tiles[i][j] = ET;
                    }
                    count++;
                }
            }
        }
        return tiles;
    }

    /**
     * select three cards at random for murder scenario
     */
    public void setMurderScenario() {
        String murderer = characterString[getRandom(0,5)];
        String murderWeapon = weaponString[getRandom(0,5)];
        String murderRoom = roomString[getRandom(0,8)];
        murder = new String[]{murderer,murderWeapon,murderRoom};
    }

    /**
     * evenly distributes all non-murder cards to players
     * @param players
     */
    public void dealCards(ArrayList<Player> players){
        weapons = new ArrayList<Weapon>();
        ArrayList<Card> undealtCards = new ArrayList<>();
        for(String s : weaponString) {
            Card card = new Card(s);
            weapons.add(new Weapon(card.getGraphic(),s));
            if (s == murder[1]){continue;}
            undealtCards.add(card);
        }


        for(String s : characterString) {
            if (s == murder[0]){continue;}
            undealtCards.add(new Card(s));
        }
        for(String s : roomString) {
            if (s == murder[2]){continue;}
            undealtCards.add(new Card(s));
        }
        Collections.shuffle(undealtCards);
        while(!undealtCards.isEmpty()) {
            for(Player p : players) {
                if(!undealtCards.isEmpty()) {
                    p.addCard(undealtCards.remove(0));
                }
            }
        }
    }


    /**
     * creates a player with a name and a character
     * @return
     */
    protected Player choosePlayers() {
        String playerName = "";
        String character = null;


          while(true) {
             String s = JOptionPane.showInputDialog(game, "type your name", "who are you",
             JOptionPane.PLAIN_MESSAGE);

             if ((s != null) && (s.length() > 0)) {
             playerName = s;
             break;
             }
          }

          String[]characters = new String[characterString.length-players.size()];
          ArrayList<String>ffs = new ArrayList<>();
          for(int i = 0; i < characterString.length;i++){
              if(!possibleChars.contains(characterString[i])){
                  ffs.add(characterString[i]);
              }
          }
          for(int i = 0; i < ffs.size(); i++){
              characters[i]=ffs.get(i);
          }

        character = getRadioSelection(characters,"Choose your player" );
          possibleChars.add(character);

        BufferedImage graphic = null;
        try {
            InputStream input = GUI.class.getResourceAsStream("/" + character + ".png");
            graphic = ImageIO.read(input);
        } catch (IOException e) {
            System.out.println("no such player");
        }
        Player player = new Player(playerName, character, graphic);
        return player;
    }


    /**
     * Creates a JPanel with Radio buttons and returns the string of the selected button
     * @param strings
     * @param message
     * @return
     */
    public String getRadioSelection(String[] strings,String message) {
        JRadioButton[] radioButtons = new JRadioButton[strings.length];
        final ButtonGroup group = new ButtonGroup();

        for (int i = 0; i < strings.length; i++) {
            radioButtons[i] = new JRadioButton(strings[i]);
            radioButtons[i].setActionCommand(strings[i]);
            group.add(radioButtons[i]);
        }

        radioButtons[0].setSelected(true);

        JPanel radioPanel = new JPanel(new GridLayout(0, 1));
        for (JRadioButton jb : radioButtons) {
            radioPanel.add(jb);
        }
        JOptionPane.showMessageDialog(game, radioPanel, message, 1);

        return group.getSelection().getActionCommand();

    }



    /**
    sets up all swing components for the game.
     */
    private void initialise() {
        SwingUtilities.invokeLater(() -> {


            //add roll button
            JButton roll = new JButton("Roll");
            roll.addActionListener(ev -> roll());

            JButton showHand = new JButton("Show hand");
            showHand.addActionListener(ev -> redrawHand());

            JButton endTurn = new JButton("End Turn");
            endTurn.addActionListener(ev -> endTurn());

            //create menu

            JMenuBar bar = new JMenuBar();
            JMenuItem i1, i2, i3;
            JMenu menu, newGame;

            menu = new JMenu("Menu");
            newGame = new JMenu("New Game");

            //menu.setPreferredSize(new Dimension(100,50));

            i2 = new JMenuItem("does nothing");
            i3 = new JMenuItem("also nothing");

            JMenuItem newSub;
            for (int i = 2; i <= 6; i++) {
                newSub = new JMenuItem("" + i + " player");
                int finalI = i;
                newSub.addActionListener(e -> newGame(finalI));
                newGame.add(newSub);
            }

            menu.add(newGame);
            menu.add(i2);
            menu.add(i3);
            bar.add(menu);
            bar.add(roll);
            bar.add(showHand);
            bar.add(endTurn);
            game.setJMenuBar(bar);


            game.setVisible(true);
            game.setSize(DEFAULT_FRAME_WIDTH, DEFAULT_FRAME_HEIGHT);
            game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            game.setLocationRelativeTo(null);
        });
    }

    public static void main(String[] args) {
        new Board();
    }


    //helper methods//

    /**
     * returns true if imputed char is an int
     */
    private static boolean isInt(char in) {
        if (Character.isDigit(in)) {
            return true;
        }
        return false;
    }

    /**
     * returns a random number between min and max
     */
    private static int getRandom(int min, int max) {
        return (int)(Math.random()*((max-min)+min));
    }
}
