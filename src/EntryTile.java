public class EntryTile extends Tile {

    public String getRoom() {
        return room;
    }

    public void setRoom(char c) {
        switch(c){
            case '!':room = "Kitchen";break;
            case '@':room = "BallRoom";break;
            case '#':room = "Conservatory";break;
            case '$':room = "Dining Room";break;
            case '%':room = "Billiard Room";break;
            case '^':room = "Lounge";break;
            case '&':room = "Hall";break;
            case '*':room = "Library";break;
            case '(':room = "Study";break;
        }
    }

    String room;
    public EntryTile(int aX, int aY) {
        super(aX, aY);
    }

    @Override
    public String toString() {
        return "EntryTile";
    }


}
