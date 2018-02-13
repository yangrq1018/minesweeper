package minesweeper.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class Display {

    private JFrame frame;
    private Canvas canvas;

    private String title;
    private int width, height;

    public Display(String title, int width, int height, Runnable r) {
        this.title = title;
        this.width = width;
        this.height = height;

        createDisplay();

        JMenuBar menuBar = new JMenuBar();
        JMenu menu;
        JMenuItem menuItem;

        menu = new JMenu("Game");
        menu.setMnemonic(KeyEvent.VK_A);
        menuBar.add(menu);
        menuItem = new JMenuItem("Restart");

        menuItem.addActionListener((e) ->{r.run();});

        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.META_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("This restarts the game");
        menu.add(menuItem);
        frame.setJMenuBar(menuBar);
        frame.pack();
        frame.setVisible(true);
    }


    private void createDisplay() {
        frame = new JFrame(title);
        // do not set size explicitly, let pack do it
//        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);


        canvas = new Canvas();
        canvas.setPreferredSize(new Dimension(width, height));
        canvas.setMaximumSize(new Dimension(width, height));
        canvas.setMinimumSize(new Dimension(width, height));
        canvas.setFocusable(false);
        frame.add(canvas);

    }

    public Canvas getCanvas() {
        return canvas;
    }

    public JFrame getFrame() {
        return frame;
    }

}
