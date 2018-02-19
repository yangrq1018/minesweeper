package minesweeper.gfx;

import minesweeper.game.Board;
import minesweeper.game.states.CellState;

import javax.swing.*;
import java.awt.*;

public class BoardPanel extends JPanel {
    Board board;

    public BoardPanel(Board board) {
        this.board = board;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        CellState[][] states = board.getStates();
        for (int i=0; i<states.length; i++) {
            for (int j=0; j<states[0].length; j++) {
                Image img = Assets.cellState2Image(states[i][j]);
                g.drawImage(img, j*Assets.width, i*Assets.width, null);
            }
        }
    }
}
