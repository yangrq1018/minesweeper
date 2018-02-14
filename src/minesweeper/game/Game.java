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


/*
 * Class Game:
 * 		manages all resources of the minesweeper.game (mouse, board, graphics)
 * */
public class Game {

    public String title;
    protected int N;
    protected Board board;
    protected BufferStrategy bs; // subclass needs it for automation
    private Display display;
    private int width, height;
    private boolean finished;
    private MouseManager mouseManager;
    private BufferStrategy sbbs;
    private int NMines;
    private long gameStartTime;
    private int NMinesLeftNoFound;

    public Game(String title, int N, int NMines) {
        this.N = N;
        width = Assets.width * N;
        height = width;
        this.NMines = NMines;
        NMinesLeftNoFound = NMines;
        this.title = title;

        board = new Board(N, NMines);
        mouseManager = new MouseManager(this);

        display = new Display(title, width, height, this::reset); // reset reference past for set eventlisner
//        display.getFrame().addMouseListener(mouseManager);
        display.getCanvas().addMouseListener(mouseManager);
        display.getCanvas().createBufferStrategy(2);
        bs = display.getCanvas().getBufferStrategy();
        display.getScoreboard().createBufferStrategy(2);
        sbbs = display.getScoreboard().getBufferStrategy();

        Assets.init();
    }

    public void reset() {
        finished = false;
        board = new Board(N, NMines);
        display.getFrame().setTitle(title);

//        System.out.println("reset");
        start(); // repaint the canvas
    }


    public boolean isCoveredOrFlagged(int x, int y) {
        int row = y / Assets.width;
        int col = x / Assets.width;
        return board.getCellState(row, col) == CellState.COVERED || board.getCellState(row, col) == CellState.FLAGGED;
    }

    // called when mouse clicks happen (mouse release)
    public void onClick(boolean isLeft, int x, int y) {
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



        GameState result = board.getGameState();

        // when minesweeper.game ends
        setTitleAndSetFinished(result);

		/*
		When a Java program runs, a large number of Graphics objects can be created within a short time frame.
		Although the finalization process of the garbage collector also disposes of the same system resources,
		it is preferable to manually free the associated resources by calling this method rather than to rely
		on a finalization process which may not run to completion for a long period of time.
		 */
        g.dispose();
        gsb.dispose();
        bs.show();
        sbbs.show();
    }

    /**
     * @param x
     * @param y
     */
    public boolean onSimulPressed(int x, int y) {
        boolean isSuccToAutoExpand;
        if (finished)
            return true;

        int row = y / Assets.width;
        int col = x / Assets.width;
        Graphics g = bs.getDrawGraphics();

        isSuccToAutoExpand = board.inferOnUncovered(row, col, g);

        if (isSuccToAutoExpand) {
            // game could end here, check and end the game if result is not ongoing
            GameState result = board.getGameState();
            setTitleAndSetFinished(result);
        } else {
            // begin blink
            board.changeTempToUNC0(row, col, g);
        }

        bs.show();

        g.dispose();
        return isSuccToAutoExpand;
    }

    public void restoreTempUNC0(int x, int y) {
        int row = y / Assets.width;
        int col = x / Assets.width;
        Graphics g = bs.getDrawGraphics();
        board.restoreTempUNC0(row, col, g);
        bs.show();
        g.dispose();
    }

    // display the initial covered board when minesweeper.game starts
    public void start() {
        gameStartTime = System.currentTimeMillis();
        Graphics g = bs.getDrawGraphics();

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                Assets.draw(i, j, CellState.COVERED, g);
            }
        }
        bs.show();
        g.dispose();

        // set up time
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() -> {
            SwingUtilities.invokeLater(() -> {
                Graphics2D g2 = (Graphics2D) sbbs.getDrawGraphics();
                long now = System.currentTimeMillis();
                int elapseInSeconds = Math.round((now - gameStartTime) / 1000);
                Assets.drawTime(elapseInSeconds, g2, N);
                g2.dispose();
                sbbs.show();
            });
        }, 0, 1, TimeUnit.SECONDS);

        // set up mine left counter
        Graphics gCnt = sbbs.getDrawGraphics();
        Assets.drawMinesCnt(NMinesLeftNoFound, gCnt);
        gCnt.dispose();
        sbbs.show();
    }


    protected void setTitleAndSetFinished(GameState result) {
        if (result != GameState.ONGOING) {
            finished = true; // set finished flag, block further onClick event
            System.out.println("Game ended!");
            String msg = (result == GameState.LOST ? "!!!!! You Lose !!!!!" : "!!!!! You Won !!!!!");
            display.getFrame().setTitle(msg);
        }
    }

    public boolean isFinished() {
        return finished;
    }
}
