package src;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;

class BattleshipCLIView implements Observer {
    private BattleshipModel model;
    private Map<Integer, Integer> remainingShips;
    private boolean[][] previousHits;
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_CYAN = "\u001B[36m";

    public BattleshipCLIView(BattleshipModel model) {
        this.model = model;
        model.addObserver(this);
        initializeRemainingShips();
        previousHits = new boolean[10][10];
    }

    private void initializeRemainingShips() {
        remainingShips = new HashMap<>();
        remainingShips.put(5, 1);  // One battleship (size 5)
        remainingShips.put(4, 1);  // One cruiser (size 4)
        remainingShips.put(3, 1);  // One submarine (size 3)
        remainingShips.put(2, 2);  // Two destroyers (size 2)
    }

    private String getShipName(int size) {
        switch (size) {
            case 5: return "Battleship";
            case 4: return "Cruiser";
            case 3: return "Submarine";
            case 2: return "Destroyer";
            default: return "Ship";
        }
    }

    private void displayRemainingShips() {
        System.out.println(ANSI_CYAN + "\n=== Remaining Enemy Ships ===" + ANSI_RESET);
        for (Map.Entry<Integer, Integer> entry : remainingShips.entrySet()) {
            if (entry.getValue() > 0) {
                String shipName = getShipName(entry.getKey());
                System.out.printf("%s (Size %d) × %d%n", 
                    shipName, entry.getKey(), entry.getValue());
            }
        }
        System.out.println();
    }

    private void displayGrid() {
        Cell[][] grid = model.getGrid();
        System.out.println(ANSI_BLUE + "\n=== Battle Grid ===" + ANSI_RESET);
        
        // Print column numbers
        System.out.print("   ");
        for (int i = 1; i <= 10; i++) {
            System.out.print(i + " ");
        }
        System.out.println();

        // Print grid with row letters
        for (int i = 0; i < 10; i++) {
            System.out.print((char)('A' + i) + "  ");
            for (int j = 0; j < 10; j++) {
                if (grid[i][j].isHit()) {
                    if (grid[i][j].hasShip()) {
                        System.out.print(ANSI_RED + "X " + ANSI_RESET);
                    } else {
                        System.out.print("O ");
                    }
                } else {
                    System.out.print("· ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public void startGame() {
        Scanner scanner = new Scanner(System.in);
        System.out.println(ANSI_GREEN + "\nWelcome to Battleship!" + ANSI_RESET);
        System.out.println("Try to sink all enemy ships by entering coordinates.");
        System.out.println("Format: A1 to J10 (e.g., B5 or H10)\n");
        
        displayRemainingShips();
        displayGrid();

        while (!model.isGameOver()) {
            System.out.print("Enter target coordinates: ");
            String input = scanner.next().toUpperCase();
            
            if (input.length() < 2 || input.length() > 3) {
                System.out.println(ANSI_RED + "Invalid input! Format must be A1 - J10." + ANSI_RESET);
                continue;
            }

            int x = input.charAt(0) - 'A'; 
            int y;
            try {
                y = Integer.parseInt(input.substring(1)) - 1;
            } catch (NumberFormatException e) {
                System.out.println(ANSI_RED + "Invalid number format! Try again." + ANSI_RESET);
                continue;
            }

            if (x < 0 || x >= 10 || y < 0 || y >= 10) {
                System.out.println(ANSI_RED + "Invalid coordinates! Must be between A1 and J10." + ANSI_RESET);
                continue;
            }

            if (model.getGrid()[x][y].isHit()) {
                System.out.println(ANSI_YELLOW + "You've already fired at this location! Try again." + ANSI_RESET);
                continue;
            }

            model.attack(x, y);
        }

        // Final game state
        displayGrid();
        System.out.println(ANSI_GREEN + "Congratulations! You've won the game in " + 
            model.getTotalShots() + " shots!" + ANSI_RESET);
        scanner.close();
    }

    @Override
    public void update(Observable o, Object arg) {
        Cell[][] grid = model.getGrid();
        boolean shipSunkThisTurn = false;
        Ship sunkShip = null;

        // Process new hits
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (grid[i][j].isHit() && !previousHits[i][j]) {
                    previousHits[i][j] = true;
                    if (grid[i][j].hasShip()) {
                        Ship ship = grid[i][j].getShip();
                        if (ship.isSunk()) {
                            shipSunkThisTurn = true;
                            sunkShip = ship;
                            // Update remaining ships count
                            int shipSize = ship.getSize();
                            remainingShips.put(shipSize, remainingShips.get(shipSize) - 1);
                        }
                    }
                }
            }
        }

        // Clear screen (print newlines)
        System.out.println("\n\n");

        // Show appropriate messages
        if (shipSunkThisTurn && sunkShip != null) {
            String shipName = getShipName(sunkShip.getSize());
            System.out.println(ANSI_YELLOW + "DIRECT HIT! You've sunk the enemy " + 
                shipName + " (Size " + sunkShip.getSize() + ")!" + ANSI_RESET);
        } else if (arg != null && arg.toString().contains("Move")) {
            String[] coords = arg.toString().split(" ")[2].split(",");
            int x = Integer.parseInt(coords[0]);
            int y = Integer.parseInt(coords[1]);
            if (grid[x][y].hasShip()) {
                System.out.println(ANSI_RED + "DIRECT HIT! Keep firing!" + ANSI_RESET);
            } else {
                System.out.println("Miss! Try again!");
            }
        }

        displayRemainingShips();
        displayGrid();
    }
}