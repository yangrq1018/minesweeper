package minesweeper.game;

import minesweeper.game.states.CellState;
import minesweeper.game.states.GameState;
import minesweeper.gfx.Assets;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * manages all resources of the minesweeper.game (mouse, board, graphics)
 */

public class Game {
    public String title;
    public int N;
    protected Board board;
    protected BufferStrategy bs; // subclass needs it for automation
    ScheduledExecutorService service;
    private Display display;
    private int width, height;
    private boolean finished;
    private MouseManager mouseManager;
    private BufferStrategy sbbs;
    private int NMines;
    private long gameStartTime;
    private int NMinesLeftNoFound;
    private int timeElapsed;
    public int faceDrawingOriginX;


    public Game(String title, int N, int NMines) {
        this.N = N;
        width = Assets.width * N;
        height = width;
        this.NMines = NMines;
        NMinesLeftNoFound = NMines;
        this.title = title;
        this.faceDrawingOriginX = N * Assets.width / 2 - Assets.faceWidth / 2;

        // set up assets first
        Assets.init();

        board = new Board(N, NMines);
        mouseManager = new MouseManager(this);

        display = new Display(title, width, height, this); // reset reference past for set eventlisner
        display.getCanvas().addMouseListener(mouseManager);
        display.getCanvas().createBufferStrategy(2);
        bs = display.getCanvas().getBufferStrategy();
        display.getScoreboard().createBufferStrategy(2);
        sbbs = display.getScoreboard().getBufferStrategy();

    }

    public void reset() {
        finished = false;
        // get a new board
        board = new Board(N, NMines);
        display.getFrame().setTitle(title);
        start(); // repaint the canvas
    }


    public boolean isCoveredOrFlagged(int x, int y) {
        int row = y / Assets.width;
        int col = x / Assets.width;
        return board.getCellState(row, col) == CellState.COVERED || board.getCellState(row, col) == CellState.FLAGGED;
    }


    /**
     * Callback when mouse click happens in a normal uncover.
     * <p>
     * When a Java program runs, a large number of Graphics objects can be created within a short time frame.
     * Although the finalization process of the garbage collector also disposes of the same system resources,
     * it is preferable to manually free the associated resources by calling this method rather than to rely
     * on a finalization process which may not run to completion for a long period of time.
     *
     * @param isLeft Click is the left button of the mouse
     * @param x      click point x (horizontal axis)
     * @param y      click point y (vertical axis)
     */
    public void onNormalClick(boolean isLeft, int x, int y) {
        if (finished)
            return;

        int row = y / Assets.width;
        int col = x / Assets.width;
        Graphics g = bs.getDrawGraphics();
        Graphics gsb = sbbs.getDrawGraphics();

        if (isLeft)
            board.uncoverCell(row, col, g);
        else {
            int response = board.toggleFlag(row, col, g);
            NMinesLeftNoFound += response;
            Assets.drawMinesCnt(NMinesLeftNoFound, gsb);
        }


        g.dispose();
        gsb.dispose();
        bs.show();
        sbbs.show();

        checkGameStateAndEndIfPossible();
    }

    /**
     * If the game is possible to end, call this to finalize.
     */
    private void checkGameStateAndEndIfPossible() {
        GameState result = board.getGameState();
        if (result != GameState.ONGOING) {
            //  when minesweeper.game ends
            SetFinishedFlag(result);
            killScheduleTimer(result);
            // change face
            String face = (result == GameState.WON) ? "win" : "lose";
            setFace(face);
        }

        int choice = promptUserWhenGameEnds(result);
        if (choice == 1) {
            System.exit(0);
        } else if (choice == 0) {
            reset();
        }
    }

    /**
     * Ask the user whether to play the game again.
     *
     * @param result Win or Lose?
     * @return the option user chooses
     */
    private int promptUserWhenGameEnds(GameState result) {
        if (result != GameState.ONGOING) {
            String title = (result == GameState.WON) ? "You Won :D" : "You Lose :-(";
            String msg = (result == GameState.WON) ? "Woo! Your time: " + timeElapsed + " seconds" : "Challenge again?";

            Object[] options = {"Yes, once more!", "Quit"};
            int n = JOptionPane.showOptionDialog(display.getFrame(), msg, title,
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
            return n;
        } else {
            return -1;
        }
    }

    /**
     * Callback method when an LR click is incurred.
     *
     * @param x horizontal coordinate
     * @param y vertical coordinate
     */
    public boolean onSimulPressed(int x, int y) {
        boolean canAutoExpand;
        if (finished)
            return true;

        int row = y / Assets.width;
        int col = x / Assets.width;
        Graphics g = bs.getDrawGraphics();

        canAutoExpand = board.inferOnCell(row, col, g);

        if (canAutoExpand) {
            // game could have ended here, check and end the game if result is not ongoing
            GameState result = board.getGameState();
        } else {
            // begin blink
            board.changeTempToUNC0(row, col, g);
        }

        bs.show();
        g.dispose();

        /*check must be after bs.show to update the board
         * so user can see the final board upon lose*/
        checkGameStateAndEndIfPossible();

        return canAutoExpand;
    }


    /**
     * Distribute restoreTempUNC0 task to the underlying board.
     * @param x horizontal (to col)
     * @param y vertical (to row)
     */
    public void restoreTempUNC0(int x, int y) {
        int row = y / Assets.width;
        int col = x / Assets.width;
        Graphics g = bs.getDrawGraphics();
        board.restoreTempUNC0(row, col, g);
        bs.show();
        g.dispose();
    }

    /**
     * Prepare the game. Paint the board by the first time. Set up time, counter, face.
     */
    public void start() {
        gameStartTime = System.currentTimeMillis();
        NMinesLeftNoFound = NMines;
        Graphics g = bs.getDrawGraphics();

        // do not draw canvas here, draw it when it is constructed.
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                Assets.draw(i, j, CellState.COVERED, g);
            }
        }
        g.dispose();
        bs.show();

        // set up button icon
        setFace("smile");

        // set up left counter
        Graphics gUp = sbbs.getDrawGraphics();
        Assets.drawMinesCnt(NMinesLeftNoFound, gUp);
        gUp.dispose();
        sbbs.show();

        // set up Timer
        setupScheduleTimer();
    }

    /**
     * Set up timer. The timer will run in a scheduled single thread executor.
     */
    private void setupScheduleTimer() {
        // set up time
        service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
            SwingUtilities.invokeLater(() -> {
                Graphics2D g2 = (Graphics2D) sbbs.getDrawGraphics();
                long now = System.currentTimeMillis();
                timeElapsed = Math.round((now - gameStartTime) / 1000);
                Assets.drawTime(timeElapsed, g2, N);
                g2.dispose();
                sbbs.show();
            });
        }, 0, 1, TimeUnit.SECONDS);
    }



    public void setFace(String face) {
        display.setFace(face);
    }

    /**
     * Stop the timer when game ends
     * @param result state of the game
     */
    private void killScheduleTimer(GameState result) {
        if (result != GameState.ONGOING) {
            service.shutdown();
        }
    }

    /**
     * Because the normal GUI game relies on EventListener call back to check and end the game,
     * the method is designed protected for subclass robots can end the game by direct call
     * @param result state of the game
     */
    protected void SetFinishedFlag(GameState result) {
        if (result != GameState.ONGOING) {
            finished = true; // set finished flag, block further onClick event
            String winOrLose = (result == GameState.WON) ? "win" : "lose";
            System.out.println("Game ends, you " + winOrLose);
        }
    }

    /**
     * Indicate the game is finished or not
     * @return true if game is finished, false otherwise
     */
    public boolean isFinished() {
        return finished;
    }
}
