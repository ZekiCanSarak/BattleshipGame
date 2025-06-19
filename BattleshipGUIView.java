package src;
import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class BattleshipGUIView extends JFrame implements Observer {
    private BattleshipModel model;
    private JButton[][] gridButtons = new JButton[10][10];

    public BattleshipGUIView(BattleshipModel model, BattleshipController controller) {
        this.model = model;
        model.addObserver(this);

        setTitle("Battleship Game");
        setSize(500, 500);
        setLayout(new GridLayout(10, 10));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                JButton button = new JButton();
                gridButtons[i][j] = button;
                button.setBackground(Color.BLUE);
                final int x = i, y = j;

                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        controller.handleClick(x, y);  
                    }
                });

                add(button);
            }
        }

        setVisible(true);
    }

    @Override
    public void update(Observable o, Object arg) {
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (model.getGrid()[i][j].isHit()) {
                    gridButtons[i][j].setBackground(model.getGrid()[i][j].hasShip() ? Color.RED : Color.GRAY);
                    gridButtons[i][j].setText(model.getGrid()[i][j].hasShip() ? "H" : "M");
                }
            }
        }

        if (model.isGameOver()) {  //Ensurig that the game ends correctly
            JOptionPane.showMessageDialog(this, "Game Over! You won in " + model.getTotalShots() + " shots.");
        }
    }
}