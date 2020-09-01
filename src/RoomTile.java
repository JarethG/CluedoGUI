public class RoomTile extends Tile {

    String room;

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public RoomTile(int aX, int aY) {
        super(aX, aY);
    }

    @Override
    public String toString() {
        return "RoomTile";
    }


}
