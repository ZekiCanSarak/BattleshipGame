package src;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BattleshipModel extends Observable {
    private Cell[][] grid;
    private List<Ship> ships;
    private int totalShots = 0;
    private int shipsSunk = 0;

    public BattleshipModel() {
        grid = new Cell[10][10];
        ships = new ArrayList<>();

        // Initialising the 10x10 grid
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                grid[i][j] = new Cell();
            }
        }

        // Invariant: Grid must always be 10x10
        assert grid.length == 10 && grid[0].length == 10 : "Grid must be 10x10";
    }

    // Random ship placement
    public void placeShipsRandomly() {
        Random rand = new Random();
        int[] shipSizes = {5, 4, 3, 2, 2};  // Ensuring correct ship sizes

        for (int size : shipSizes) {
            boolean placed = false;
            while (!placed) {
                int x = rand.nextInt(10);
                int y = rand.nextInt(10);
                boolean horizontal = rand.nextBoolean();

                assert size >= 2 && size <= 5 : "Invalid ship size";

                if (canPlaceShip(x, y, size, horizontal)) {
                    placeShip(x, y, size, horizontal);
                    placed = true;
                }
            }
        }

        // Postcondition: 5 ships must have been added
        assert ships.size() == 5 : "Exactly 5 ships should be placed";
    }

    // Checking if a ship can be placed
    private boolean canPlaceShip(int x, int y, int size, boolean horizontal) {
        assert size > 0 : "Ship size must be positive";

        if (horizontal && (x + size > 10)) return false;
        if (!horizontal && (y + size > 10)) return false;

        for (int i = 0; i < size; i++) {
            int row = x + (horizontal ? i : 0);
            int col = y + (!horizontal ? i : 0);
            assert row >= 0 && row < 10 && col >= 0 && col < 10 : "Ship out of grid bounds";
            if (grid[row][col].hasShip()) return false;
        }
        return true;
    }

    // Placing a ship
    private void placeShip(int x, int y, int size, boolean horizontal) {
        Ship ship = new Ship(size);
        ships.add(ship);

        for (int i = 0; i < size; i++) {
            int row = x + (horizontal ? i : 0);
            int col = y + (!horizontal ? i : 0);
            grid[row][col].setShip(ship);
        }

        // Debugging output to verify correct ship sizes
        System.out.println("Placed ship of size " + size + " at (" + x + "," + y + ") " +
                (horizontal ? "Horizontally" : "Vertically"));
    }

    // Attack logic
    public void attack(int x, int y) {
        // Precondition: coordinates must be in range 0–9
        assert x >= 0 && x < 10 && y >= 0 && y < 10 : "Attack coordinates out of bounds";

        if (grid[x][y].isHit()) {
            System.out.println("You already hit this spot! Try again.");
            return;
        }

        totalShots++;
        grid[x][y].markHit();

        if (grid[x][y].hasShip()) {
            Ship ship = grid[x][y].getShip();

            assert ship != null : "Ship reference should not be null when hasShip() is true";

            if (ship.isSunk()) {
                shipsSunk++;
                System.out.println("Hit! You sunk a ship!");
            } else {
                System.out.println("Hit!");
            }
        } else {
            System.out.println("Miss!");
        }

        notifyObservers("Move at " + x + "," + y);
    }

    // Checking if game is over 
    public boolean isGameOver() {
        return shipsSunk == 5;  // Ensures game ends exactly when 5 ships are sunk
    }

    // Getting total shots
    public int getTotalShots() {
        return totalShots;
    }

    // Getting the game grid
    public Cell[][] getGrid() {
        return grid;
    }

    // Loading ships from a file
    public void loadShipsFromFile(String filename) {
        assert filename != null : "Filename must not be null";

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                assert parts.length == 3 : "Each line must have 3 parts";

                int x = parts[0].charAt(0) - 'A';
                int y = Integer.parseInt(parts[0].substring(1)) - 1;
                boolean horizontal = parts[1].equalsIgnoreCase("H");
                int size = Integer.parseInt(parts[2]);

                assert size >= 2 && size <= 5 : "Ship size must be between 2 and 5";

                if (canPlaceShip(x, y, size, horizontal)) {
                    placeShip(x, y, size, horizontal);
                } else {
                    System.out.println("Invalid ship position: " + line);
                }
            }

            notifyObservers("Ships Loaded");
        } catch (IOException e) {
            System.out.println("Error loading file: " + e.getMessage());
        }
    }
}