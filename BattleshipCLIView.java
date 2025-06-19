package src;
import java.util.Scanner;

class BattleshipCLIView implements Observer {
    private BattleshipModel model;

    public BattleshipCLIView(BattleshipModel model) {
        this.model = model;
        model.addObserver(this);
    }

    public void startGame() {
        Scanner scanner = new Scanner(System.in);
        while (!model.isGameOver()) {
            System.out.print("Enter coordinates (e.g., A1): ");
            String input = scanner.next().toUpperCase();
            
            if (input.length() < 2 || input.length() > 3) {
                System.out.println("Invalid input! Format must be A1 - J10.");
                continue;
            }

            int x = input.charAt(0) - 'A'; 
            int y = Integer.parseInt(input.substring(1)) - 1;

            if (x < 0 || x >= 10 || y < 0 || y >= 10) {
                System.out.println("Invalid coordinates! Try again.");
                continue;
            }

            model.attack(x, y);
        }

        System.out.println("Game Over! You won in " + model.getTotalShots() + " shots.");
        scanner.close();
    }

    @Override
    public void update(Observable o, Object arg) {
        Cell[][] grid = model.getGrid();

        System.out.println("=== Grid Status ===");
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (grid[i][j].isHit()) {
                    char row = (char) ('A' + i);
                    int col = j + 1;
                    String status = grid[i][j].hasShip() ? "Hit" : "Miss";
                    System.out.println(row + "" + col + ": " + status);
                }
            }
        }

        if (model.isGameOver()) {
            System.out.println("Game Over! You won in " + model.getTotalShots() + " shots.");
        }
    }
}