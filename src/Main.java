package src;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        BattleshipModel model = new BattleshipModel();
        BattleshipController controller = new BattleshipController(model);

        try (Scanner scanner = new Scanner(System.in)) {
            //Asking if user wants to load ships from a file
            System.out.print("Load ships from file? (yes/no): ");
            String fileChoice = scanner.nextLine();
            if (fileChoice.equalsIgnoreCase("yes")) {
                model.loadShipsFromFile("ships.txt");
            } else {
                model.placeShipsRandomly();  //Only place if not loading from file
                System.out.println("Ships placed randomly.");
            }

            // Giving the option to choose from CLI or GUI
            System.out.print("Start (CLI/GUI)? ");
            String choice = scanner.nextLine();
            if (choice.equalsIgnoreCase("CLI")) {
                BattleshipCLIView cliView = new BattleshipCLIView(model);
                cliView.startGame();
            } else {
                new BattleshipGUIView(model, controller);
            }
        }
    }
}
