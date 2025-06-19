package src;
public class Ship {
    private int size;
    private int hits = 0;  // Tracks the number of time the ship is hit

    public Ship(int size) {
        this.size = size;
    }

    public void hit() {
        if (!isSunk()) {  // Doesn't count extra hits if the ship is already sunk
            hits++;
        }
    }

    public boolean isSunk() {
        return hits >= size;  // Makes sure that ship is counted as sunk when fully hit
    }
}
