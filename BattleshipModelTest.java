package JUnitTests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import src.BattleshipModel;
import src.Ship;
import src.Cell;

public class BattleshipModelTest {
    private BattleshipModel model;

    @BeforeEach
    void setUp() {
        model = new BattleshipModel();
        model.placeShipsRandomly();
    }

    @Test
    void testMissRegistersCorrectly() {
        /**
         * Scenario: Attack an empty cell — expect miss
         * Loop until we find a cell without a ship, then attack it
         */
        boolean found = false;
        outer:
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (!model.getGrid()[i][j].hasShip()) {
                    model.attack(i, j);
                    assertTrue(model.getGrid()[i][j].isHit(), "Should mark empty cell as hit.");
                    assertFalse(model.getGrid()[i][j].hasShip(), "Cell should not have a ship.");
                    found = true;
                    break outer;
                }
            }
        }
        assertTrue(found, "Should have found at least one empty cell.");
    }

    @Test
    void testShipHitAndSink() {
        /**
         * Scenario: Hit all parts of a known ship and confirm it's sunk
         * We'll find the first ship and attack all its cells
         */
        Ship target = null;
        outer:
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Cell cell = model.getGrid()[i][j];
                if (cell.hasShip()) {
                    target = cell.getShip();
                    break outer;
                }
            }
        }

        assertNotNull(target, "Should have found a ship to attack.");

        // Attack all cells belonging to that ship
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (model.getGrid()[i][j].getShip() == target) {
                    model.attack(i, j);
                }
            }
        }

        assertTrue(target.isSunk(), "The selected ship should be sunk.");
    }

    @Test
    void testFullGameWinCondition() {
        /**
         * Scenario: Sink all 5 ships on the board and confirm the game ends
         * We'll attack all ship-containing cells
         */
        Cell[][] grid = model.getGrid();

        // Attack all ship cells
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (grid[i][j].hasShip()) {
                    model.attack(i, j);
                }
            }
        }

        assertTrue(model.isGameOver(), "Game should be over after all ships are sunk.");
    }

    
}
