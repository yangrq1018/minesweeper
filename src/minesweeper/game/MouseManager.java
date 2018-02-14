package minesweeper.game;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseManager implements MouseListener {
    private static final int SIMUL_THRESHOLD = 200;
    private Game game;
    private long lastClickTime = 0;
    private int logX;
    private int logY;
    private boolean lastClickIsLeft;
    private boolean blockUncoverOnRelease = false;
    private boolean onBlink = false;

    public MouseManager(Game game) {
        this.game = game;
    }


    @Override
    public void mouseReleased(MouseEvent e) {
        if (blockUncoverOnRelease) {
            blockUncoverOnRelease = false;
            return;
        }



        boolean isLeft = (e.getButton() == MouseEvent.BUTTON1);
        if (onBlink) {
            game.restoreTempUNC0(logX, logY); // use logged X and logged Y where the mouse is pressed, no released
            onBlink = false; // turn off onBlink signal
        } else {
            if (game != null)
                game.onClick(isLeft, e.getX(), e.getY());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
        boolean isLeft = (e.getButton() == MouseEvent.BUTTON1);
        if (Math.abs(System.currentTimeMillis() - lastClickTime) < SIMUL_THRESHOLD && (isLeft != lastClickIsLeft)
                && onBlink == false) {
            if (game.isCoveredOrFlagged(e.getX(), e.getY())) {
                // block uncover on release, since this is a simul click on covered/flagged
                blockUncoverOnRelease = true;
            } else {
                // have detected a simultaneous left and right click
                if (game.onSimulPressed(e.getX(), e.getY())) {
                } else {
                    onBlink = true; // cannot auto click, blink to user
                    // log X, Y for release use
                    logX = e.getX();
                    logY = e.getY();
                }
            }
        }

        lastClickIsLeft = isLeft;
        lastClickTime = System.currentTimeMillis();

    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

}
