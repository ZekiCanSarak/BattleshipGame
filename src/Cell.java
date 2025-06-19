package src;
public class Cell {
    private boolean isHit = false;
    private Ship ship;

    public boolean hasShip() {
        return ship != null;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }

    public void markHit() {
        isHit = true;
        if (ship != null) {
            ship.hit();
        }
    }

    public boolean isHit() {
        return isHit;
    }

    public Ship getShip() {  
        return ship;
    }

    public String display() {
        if (!isHit) return ".";
        return (ship != null) ? "H" : "M";
    }
}
