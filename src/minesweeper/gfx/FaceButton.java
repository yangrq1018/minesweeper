package minesweeper.gfx;

import minesweeper.game.Game;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FaceButton extends JButton implements ActionListener {
    Game game;

    /**
     * Must be public access else classes in other packages cannot call this constructor
     * @param game
     */
    public FaceButton(Game game) {
        super(new ImageIcon(Assets.smile));
        this.game = game;
//        this.drawingOrigin = game.N * Assets.width / 2 - Assets.faceWidth / 2;
        this.addActionListener(this);
//        this.setLayout(null);
//        this.setBounds(drawingOrigin, 0, Assets.faceWidth, Assets.faceHeight);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        game.reset();
    }
}
