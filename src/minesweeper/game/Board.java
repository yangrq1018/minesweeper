package minesweeper.game;

import minesweeper.game.states.CellState;
import minesweeper.game.states.GameState;
import minesweeper.gfx.Assets;

import java.awt.*;
import java.util.*;
import java.util.Queue;

/*
 * Class Board:
 * 		manages the underlying data
 * 		and update the data when user clicks the mouse
 * */

public class Board {
    // used in putMines() and bfs()
    private final int[] di = new int[]{-1, -1, -1, 0, 1, 1, 1, 0};
    private final int[] dj = new int[]{-1, 0, 1, 1, 1, 0, -1, -1};
    private final CellState[] uncoveredStates = new CellState[]{
            CellState.UNC0, CellState.UNC1, CellState.UNC2, CellState.UNC3,
            CellState.UNC4, CellState.UNC5, CellState.UNC6, CellState.UNC7, CellState.UNC8
    };
    private int N;
    private int NMines;

    public int getNCovered() {
        return NCovered;
    }

    private int NCovered;
    private GameState gameState;
    // save data in separate arrays, instead of an array of objects
    // this increases CPU-Register cache hit so can run faster
    private boolean[][] isMine;
    private int[][] mineCnt;
    private CellState[][] states;

    public Board(int N, int NMines) {
        // parameters should have be checked before here
        // if, somehow, they are still invalid, overwrite them with defaults
        if (N < 10 || N > 1000 || NMines < 1 || NMines > N * N) {
            N = 30;
            NMines = 100;
        }

        this.N = N;
        this.NCovered = N * N;
        this.NMines = NMines;

        isMine = new boolean[N][N];
        mineCnt = new int[N + 2][N + 2];
        states = new CellState[N][N];

        putMines();

        for (int i = 0; i < N; i++)
            Arrays.fill(states[i], CellState.COVERED);

        gameState = GameState.ONGOING;
    }

    // randomly place mines in the board
    // and update "count of mines" of neighboring cells
    private void putMines() {
        Random rand = new Random();
        int mines = NMines;
        while (mines-- > 0) {
            int pos = rand.nextInt(NCovered);
            int x = pos % N;
            int y = pos / N;
            if (isMine[y][x])
                mines++;
            else {
                isMine[y][x] = true;
                for (int d = 0; d < di.length; d++) {
                    mineCnt[y + di[d] + 1][x + dj[d] + 1]++;
                }
            }
        }
    }

    // when minesweeper.game stopped, display covered mines, etc.
    private void uncoverAll(Graphics g, boolean won) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (states[i][j] == CellState.COVERED && isMine[i][j]) {
                    states[i][j] = won ? CellState.FLAGGED : CellState.MINE;
                    Assets.draw(i, j, states[i][j], g);
                } else if (states[i][j] == CellState.FLAGGED && !isMine[i][j]) {
                    states[i][j] = CellState.WRONG_FLAG;
                    Assets.draw(i, j, states[i][j], g);
                }
            }
        }
    }

    // when click on an empty cell (has no mines around)
    // expand the uncovered region automatically
    // breadth first search
    private void bfs(int row, int col, Graphics g) {
        Queue<Integer> q = new ArrayDeque<>();
        Set<Integer> visited = new HashSet<>();

        /*
        Restore NCovered which has -- before bfs, we do this for a preliminary check of status
        if not Win, we need to restore it back and do a bfs
         */
        NCovered++;
        q.add(row * N + col);
        visited.add(row * N + col);

        while (!q.isEmpty()) {
            int r = q.peek() / N;
            int c = q.poll() % N;

            if (states[r][c] != CellState.COVERED)
                continue;

            states[r][c] = uncoveredStates[mineCnt[r + 1][c + 1]];
            Assets.draw(r, c, states[r][c], g); // reveal the concerning cell
            NCovered--; // decrease ACovered now

            // if the concerning cell is not UNC0, no need to consider queueing its neighbors
            if (states[r][c] != CellState.UNC0)
                continue;

            // reveal a UNC0, queue its neighbor
            for (int i = 0; i < di.length; i++) {
                int _r = r + di[i];
                int _c = c + dj[i];
                int key = _r * N + _c;
                if (_r < 0 || _r >= N || _c < 0 || _c >= N || visited.contains(key))
                    continue;
                q.add(key);
                visited.add(key); // anti-double queueing a cell
            }
        }

        // can bfs end the game ???
        if (NCovered == NMines)
            gameState = GameState.WON;
    }

    // when user left-clicks on a cell, uncover it
    // the minesweeper.game can end after this
    public boolean uncoverCell(int row, int col, Graphics g) {
        if (states[row][col] != CellState.COVERED) // click on non-covered tile, ignore
            return false;
        if (isMine[row][col]) { // hit a mine
            gameState = GameState.LOST;
            uncoverAll(g, false);
            states[row][col] = CellState.FIRED_MINE;
            Assets.draw(row, col, CellState.FIRED_MINE, g);
        } else {
            NCovered--;
            // draw the uncovered tile, show the adjacent mine count
            Assets.draw(row, col, uncoveredStates[mineCnt[row + 1][col + 1]], g);
            if (NCovered == NMines) { // win
                gameState = GameState.WON;
                uncoverAll(g, true); // uncover all in win mode
            } else
                bfs(row, col, g); // uncover, game will move on
        }
//        g.dispose();
        // DO NOT DISPOSE here! let caller Game.onClick dispose it
        // OR when inferOnUncovered call this method multiple times the graphics won't be
        // displayed
        return true;
    }


    // inferred on an already uncovered cell to auto expand
    // Graphics as argument
    public boolean inferOnUncovered(int row, int col, Graphics g) {
        // check (row, col) is uncovered and non zero
        // we are sure at this stage the clicked cell is not covered or flagged
        if (states[row][col] == CellState.UNC0) { // nothing to infers
            return true;
        }

        int minesCount = getMineCnt(row, col);
        int flaggedCell = 0;
        for (int i = 0; i < di.length; i++) {
            // avoid out of bound
            int _r = row + di[i];
            int _c = col + dj[i];
            if (_r >= 0 && _r < N && _c >= 0 && _c < N) {
                if (getCellState(_r, _c) == CellState.FLAGGED) {
                    flaggedCell++;
                }
            }
        }


        if (minesCount == flaggedCell) {
            // automatically click each covered cell
            // as we believe there are empty
            for (int i = 0; i < di.length; i++) {
                int _r = row + di[i];
                int _c = col + dj[i];
                if (_r >= 0 && _r < N && _c >= 0 && _c < N) { // prevent out of bound
                    if (getCellState(_r, _c) == CellState.COVERED) {
                        uncoverCell(_r, _c, g);
                    }
                }
            }
            return true; // performed inference
        } else {
            // return false to inform the game to blink
            return false;
        }

        // notice that if hit a mine in
    }


    public void restoreTempUNC0(int row, int col, Graphics g) {
        for (int i = 0; i < di.length; i++) {
            int _r = row + di[i];
            int _c = col + dj[i];
            if (_r >= 0 && _r < N && _c >= 0 && _c < N) { // prevent out of bound
                if (getCellState(_r, _c) == CellState.TEMP_UNC0) {
                    // set TEMP_UNC0 back to COVERED
                    states[_r][_c] = CellState.COVERED;
                    Assets.draw(_r, _c, CellState.COVERED, g);
                }
            }
        }
    }

    public void changeTempToUNC0(int row, int col, Graphics g) {
        for (int i = 0; i < di.length; i++) {
            int _r = row + di[i];
            int _c = col + dj[i];
            if (_r >= 0 && _r < N && _c >= 0 && _c < N) { // prevent out of bound
                if (getCellState(_r, _c) == CellState.COVERED) {
                    // will change back later, does not affect the game
                    states[_r][_c] = CellState.TEMP_UNC0;
                    Assets.draw(_r, _c, CellState.TEMP_UNC0, g);
                }
            }
        }
    }

    // offset by 1
    protected int getMineCnt(int row, int col) {
        return mineCnt[row + 1][col + 1];
    }

    /**
     * Cell state is public information, so modifier is public
     *
     * @param row
     * @param col
     * @return
     */
    public CellState getCellState(int row, int col) {
        return states[row][col];
    }

    // when user right-clicks on a cell, we set/remove the flag
    public int toggleFlag(int row, int col, Graphics g) {
        int delta = 0;
        if (states[row][col] == CellState.COVERED) {
            // flag one cell
            states[row][col] = CellState.FLAGGED;
            delta = -1;
        } else if (states[row][col] == CellState.FLAGGED) {
            states[row][col] = CellState.COVERED;
            delta = 1;
        }

        Assets.draw(row, col, states[row][col], g);
        g.dispose();
        return delta;
    }

    public GameState getGameState() {
        return gameState;
    }
}
