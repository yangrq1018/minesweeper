package minesweeper.game;

import minesweeper.gfx.Assets;
import minesweeper.gfx.BoardPanel;
import minesweeper.gfx.FaceButton;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class Display {
    /**
     * The window.
     */
    public JFrame frame;
    /**
     * The board canvas.
     */
    private BoardPanel boardPanel;
    /**
     * The score, face and timer canvas.
     */
    private Canvas bannerCanvas;
    private Game game;
    private FaceButton faceButton;

    private String title;
    private int width, height;

    public Display(String title, int width, int height, Game game) {
        this.game = game;
        this.title = title;
        this.width = width;
        this.height = height;

        createDisplay();
        createMenu(game::reset);
        createButton();

        frame.pack();
        frame.setVisible(true);
    }

    public void drawSmileFace() {
        faceButton.setIcon(new ImageIcon(Assets.smile));
    }

    public void setFace(String face) {
        Image img = Assets.stringToFaceImg(face);
        faceButton.setIcon(new ImageIcon(img));
    }

    private void createButton() {
        faceButton = new FaceButton(game);
        frame.add(faceButton, BorderLayout.NORTH);
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

        boardPanel = new BoardPanel(game.board);
        boardPanel.setPreferredSize(new Dimension(width, height));
        boardPanel.setFocusable(false);
        frame.add(boardPanel, BorderLayout.SOUTH);

        bannerCanvas = new Canvas();
        bannerCanvas.setPreferredSize(new Dimension(width, Assets.faceHeight));
        frame.add(bannerCanvas, BorderLayout.CENTER);
    }


    public Canvas getBannerCanvas() {
        return bannerCanvas;
    }

    public JFrame getFrame() {
        return frame;
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }
}
