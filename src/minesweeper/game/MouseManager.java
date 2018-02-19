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
    private boolean onBlinkFirstRelease = false;
    private boolean onBlinkSecondRelease = false;

    public MouseManager(Game game) {
        this.game = game;
    }


    @Override
    public void mouseReleased(MouseEvent e) {
        if (!game.isFinished()) {
            game.setFace("smile");
        }


        boolean isLeft = (e.getButton() == MouseEvent.BUTTON1);
        if (onBlinkFirstRelease) {
            onBlinkFirstRelease = false;
        } else if (onBlinkSecondRelease) {
            game.restoreTempUNC0(logX, logY); // use logged X and logged Y where the mouse is pressed, no released
            onBlinkSecondRelease = false;
        } else {
            if (game != null)
                game.onNormalClick(isLeft, e.getX(), e.getY());
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * On an failed auto expand, a blink process be will executed. Double set the blink release flags so that the blink
     * will end only after the user release the last button he holds.
     *
     * @param e mouse event
     */
    @Override
    public void mousePressed(MouseEvent e) {
        game.setFace("onclick");

        boolean isLeft = (e.getButton() == MouseEvent.BUTTON1);
        if (Math.abs(System.currentTimeMillis() - lastClickTime) < SIMUL_THRESHOLD && (isLeft != lastClickIsLeft)
                && !onBlinkSecondRelease && !onBlinkFirstRelease) {
            // detected an LR click
            if (!game.onSimulPressed(e.getX(), e.getY())) {
                onBlinkFirstRelease = true;
                onBlinkSecondRelease = true;
                // log X, Y for release use
                logX = e.getX();
                logY = e.getY();
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
