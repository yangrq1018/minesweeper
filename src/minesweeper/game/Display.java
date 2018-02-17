package minesweeper.game;

import minesweeper.gfx.Assets;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class Display {
    /**
     * The window.
     */
    private JFrame frame;
    /**
     * The board canvas.
     */
    private Canvas canvas;
    /**
     * The score, face and timer canvas.
     */
    private Canvas scoreboard;

    private String title;
    private int width, height;

    public Display(String title, int width, int height, Runnable r) {
        this.title = title;
        this.width = width;
        this.height = height;

        createDisplay();
        createMenu(r);


        frame.pack();
        frame.setVisible(true);
    }

    private void createButton() {

    }

    /**
     * Create and initialize the menu bar.
     *
     * @param r callback for restart JMenuItem
     */
    private void createMenu(Runnable r) {
        JMenuBar menuBar = new JMenuBar();
        JMenu menu;
        JMenuItem menuItem;
        menu = new JMenu("Game");
        menu.setMnemonic(KeyEvent.VK_A);
        menuBar.add(menu);
        menuItem = new JMenuItem("Restart");
        menuItem.addActionListener((e) -> r.run());
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.META_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("This restarts the game");
        menu.add(menuItem);
        frame.setJMenuBar(menuBar);

    }

    /**
     * Create the GUI objects needed. Do not explicitly set the size of JFrame, let pack determine it.
     */
    private void createDisplay() {
        frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width, height));
        canvas.setMaximumSize(new Dimension(width, height));
        canvas.setMinimumSize(new Dimension(width, height));
        canvas.setFocusable(false);
        frame.add(canvas, BorderLayout.SOUTH);

        scoreboard = new Canvas();
        scoreboard.setSize(new Dimension(width, Assets.faceHeight));
        frame.add(scoreboard, BorderLayout.NORTH);
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public Canvas getScoreboard() {
        return scoreboard;
    }

    public JFrame getFrame() {
        return frame;
    }

}
