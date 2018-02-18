package minesweeper.Solver;

import minesweeper.game.Game;
import minesweeper.game.states.CellState;
import minesweeper.game.states.GameState;

import java.awt.*;
import java.util.Random;
import java.util.Stack;
import java.util.concurrent.TimeUnit;

public class GameRobot extends Game {

    private final int[] di = new int[]{-1, -1, -1, 0, 1, 1, 1, 0};
    private final int[] dj = new int[]{-1, 0, 1, 1, 1, 0, -1, -1};
    private final int TIMEOUT;
    private static final int ROBOT_TAKEOVER_THRESHOLD = 10;

    private Random random;

    public GameRobot(String title, int N, int NMines, int TIMEOUT) {
        super(title, N, NMines);
        random = new Random();
        this.TIMEOUT = TIMEOUT;
    }

    private void flagPossible() {
        for (int r=0; r<N; r++) {
            for (int c=0; c<N; c++) {

                if (CellState.isUncovered(board.getCellState(r, c)) && board.getCellState(r, c) !=
                        CellState.UNC0) {
                    int minCnt = board.getCellState(r, c).getValue(); // get value from State interface to be fair
                    int flagCnt = 0;
                    // COVERED CELLs stack surrounding
                    Stack<Integer> cs = new Stack<>();

                    for (int i=0; i<di.length; i++) {
                        int _r = r+di[i];
                        int _c = c+dj[i];
                        if (_r>=0 && _r<N && _c>=0 && _c<N) {
                            if (board.getCellState(_r, _c) == CellState.COVERED) {
                                cs.add(_r*N + _c);
                            } else if (board.getCellState(_r, _c) == CellState.FLAGGED) {
                                flagCnt++;
                            }
                        }
                    }

                    if (minCnt == flagCnt + cs.size()) {
                        while (!cs.empty()) {
                            int row = cs.peek() / N;
                            int col = cs.pop() % N;
                            flagCell(row, col);
                        }
                    }
                }
            }
        }

    }

    private void timeOut() {
        // pause
        try {
            TimeUnit.MILLISECONDS.sleep(TIMEOUT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void uncoverPossible() {
        for (int row=0; row<N; row++) {
            for (int col=0; col<N; col++) {
                CellState s = board.getCellState(row, col);
                if (!(s.getValue() > 0)) {
                    continue;
                }

                int minesCount = s.getValue();
                int flaggedCell = 0;

                Stack<Integer> cs = new Stack<>();

                for (int i = 0; i < di.length; i++) {
                    // avoid out of bound
                    int _r = row + di[i];
                    int _c = col + dj[i];
                    if (_r >= 0 && _r < N && _c >= 0 && _c < N) {
                        if (board.getCellState(_r, _c) == CellState.FLAGGED) {
                            flaggedCell++;
                        } else if (board.getCellState(_r, _c) == CellState.COVERED) {
                            cs.add(_r*N+_c);
                        }
                    }
                }

                if (minesCount == flaggedCell) {
                    // automatically click each covered cell
                    // as we believe there are empty
                    while (!cs.isEmpty()) {
                        int rcover = cs.peek()/N;
                        int ccover = cs.pop()%N;
                        unCoverCell(rcover, ccover);
                    }
                }
            }
        }

    }

    public void autoPlay() {
        boolean start = true;
        while (!isFinished()) {
            if (start) {
                while (N*N - board.getNCovered() <= ROBOT_TAKEOVER_THRESHOLD) {
                    // random click
                    int randomKey = random.nextInt(N * N);
                    int row = randomKey / N;
                    int col = randomKey % N;
                    unCoverCell(row, col);
                }
                start = false;
            }

            flagPossible();
            timeOut();
            // try to uncover cells certain not mine according to the flagged cells and mineCnt
            uncoverPossible();
            System.out.println("Uncovered: "+board.getNCovered()+"/"+N*N);
            timeOut();
        }
    }

    public boolean unCoverCell(int row, int col) {
        if (isFinished())
            return false;

        Graphics g = bs.getDrawGraphics();

        boolean response = board.uncoverCell(row, col, g);
        bs.show();
        GameState result = board.getGameState();
        // when minesweeper.game ends
        SetFinishedFlag(result);

        g.dispose();
        return response;
    }


    public void flagCell(int row, int col) {
        if (isFinished())
            return;

        Graphics g = bs.getDrawGraphics();
        board.toggleFlag(row, col, g);
        bs.show();
        g.dispose();
    }


}
