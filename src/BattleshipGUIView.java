package src;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;

class BattleshipGUIView extends JFrame implements Observer {
    private BattleshipModel model;
    private JButton[][] gridButtons = new JButton[10][10];
    private static final Color WATER_COLOR = new Color(28, 107, 160);
    private static final Color MISS_COLOR = new Color(169, 169, 169);
    private static final Color HIT_COLOR = new Color(178, 34, 34);
    private static final Font CELL_FONT = new Font("Arial", Font.BOLD, 16);
    private JLabel messageLabel;
    private JLabel remainingShipsLabel;
    private Timer fadeTimer;
    private float fadeAlpha = 1.0f;
    private boolean[][] previousHits;
    private Map<Integer, Integer> remainingShips;  // Map of ship size to count

    public BattleshipGUIView(BattleshipModel model, BattleshipController controller) {
        this.model = model;
        model.addObserver(this);
        previousHits = new boolean[10][10];
        initializeRemainingShips();

        setTitle("Naval Battle");
        setSize(600, 750);  // Increased height for remaining ships panel
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(44, 62, 80));

        // Create grid panel
        JPanel gridPanel = new JPanel(new GridLayout(10, 10, 2, 2));
        gridPanel.setBackground(new Color(44, 62, 80));

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                JButton button = new JButton();
                gridButtons[i][j] = button;
                styleButton(button);
                final int x = i, y = j;

                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        controller.handleClick(x, y);
                    }
                });

                gridPanel.add(button);
            }
        }

        // Add title label
        JLabel titleLabel = new JLabel("BATTLESHIP", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Create bottom panel for messages and remaining ships
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(new Color(44, 62, 80));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        // Create remaining ships panel
        remainingShipsLabel = new JLabel(createRemainingShipsText(), SwingConstants.CENTER);
        remainingShipsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        remainingShipsLabel.setForeground(new Color(52, 152, 219));
        bottomPanel.add(remainingShipsLabel, BorderLayout.NORTH);
        
        // Create message panel
        messageLabel = new JLabel("Begin the battle!", SwingConstants.CENTER);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 18));
        messageLabel.setForeground(Color.WHITE);
        bottomPanel.add(messageLabel, BorderLayout.CENTER);

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(gridPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        setLocationRelativeTo(null);
        setVisible(true);

        // Initialize fade timer
        fadeTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fadeAlpha -= 0.05f;
                if (fadeAlpha <= 0.3f) {
                    fadeAlpha = 1.0f;
                    ((Timer)e.getSource()).stop();
                }
                messageLabel.setForeground(new Color(1f, 1f, 1f, fadeAlpha));
            }
        });
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

    private String createRemainingShipsText() {
        StringBuilder sb = new StringBuilder("<html><center>Remaining Ships:<br>");
        for (Map.Entry<Integer, Integer> entry : remainingShips.entrySet()) {
            if (entry.getValue() > 0) {
                String shipName = getShipName(entry.getKey());
                sb.append(String.format("%s (Size %d) × %d<br>", 
                    shipName, entry.getKey(), entry.getValue()));
            }
        }
        sb.append("</center></html>");
        return sb.toString();
    }

    private void styleButton(JButton button) {
        button.setBackground(WATER_COLOR);
        button.setForeground(Color.WHITE);
        button.setFont(CELL_FONT);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(44, 62, 80), 1),
            BorderFactory.createLineBorder(new Color(52, 152, 219), 1)
        ));
        button.setFocusPainted(false);
        button.setOpaque(true);
    }

    private void showMessage(String message, Color color) {
        messageLabel.setText(message);
        messageLabel.setForeground(color);
        fadeAlpha = 1.0f;
        fadeTimer.restart();
    }

    @Override
    public void update(Observable o, Object arg) {
        boolean shipSunkThisTurn = false;
        int currentX = -1;
        int currentY = -1;
        Ship sunkShip = null;
        
        // Get current move coordinates if available
        if (arg != null && arg.toString().contains("Move")) {
            String[] coords = arg.toString().split(" ")[2].split(",");
            currentX = Integer.parseInt(coords[0]);
            currentY = Integer.parseInt(coords[1]);
        }
        
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Cell cell = model.getGrid()[i][j];
                JButton button = gridButtons[i][j];
                
                if (cell.isHit() && !previousHits[i][j]) {  // Only process new hits
                    previousHits[i][j] = true;  // Mark as processed
                    
                    if (cell.hasShip()) {
                        Ship ship = cell.getShip();
                        button.setBackground(HIT_COLOR);
                        button.setText("💥");
                        
                        // Check if this hit sunk the ship
                        if (ship.isSunk()) {
                            shipSunkThisTurn = true;
                            sunkShip = ship;
                            // Update remaining ships count
                            int shipSize = ship.getSize();
                            remainingShips.put(shipSize, remainingShips.get(shipSize) - 1);
                            remainingShipsLabel.setText(createRemainingShipsText());
                        }
                    } else {
                        button.setBackground(MISS_COLOR);
                        button.setText("•");
                    }
                    button.setEnabled(false);
                }
            }
        }

        // Show appropriate message based on the turn's outcome
        if (model.isGameOver()) {
            showMessage("Victory! All enemy ships have been sunk! Total shots: " + model.getTotalShots(), 
                       new Color(46, 204, 113));  // Green color for victory
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(this,
                    "Congratulations! You won in " + model.getTotalShots() + " shots!",
                    "Victory!",
                    JOptionPane.INFORMATION_MESSAGE);
            });
        } else if (currentX != -1 && currentY != -1) {  // Only show messages for current move
            Cell currentCell = model.getGrid()[currentX][currentY];
            if (currentCell.hasShip()) {
                if (shipSunkThisTurn && sunkShip != null) {
                    String shipName = getShipName(sunkShip.getSize());
                    showMessage("Direct hit! You've sunk the enemy " + shipName + " (Size " + sunkShip.getSize() + ")! 🚢", 
                              new Color(241, 196, 15));  // Yellow color for sunk ship
                } else {
                    showMessage("Direct hit! Keep firing! 🎯", new Color(231, 76, 60));  // Red color for hit
                }
            } else {
                showMessage("Miss! Try again! 💦", Color.WHITE);
            }
        }
    }
}