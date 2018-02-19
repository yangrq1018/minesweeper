package minesweeper.game;

import minesweeper.game.states.CellState;
import minesweeper.game.states.GameState;
import minesweeper.gfx.Assets;

import java.awt.*;
import java.util.*;
import java.util.Queue;

/**
 * managing the underlying data and work with {@link Graphics} to draw on canvas, in order to update the
 * look of the board.
 */
public class Board {
    /**
     * Static helper to quickly iterator over adjacent eight cells.
     */
    private final int[] di = new int[]{-1, -1, -1, 0, 1, 1, 1, 0};
    /**
     * Static helper to quickly iterator over adjacent eight cells.
     */
    private final int[] dj = new int[]{-1, 0, 1, 1, 1, 0, -1, -1};

    /**
     * Static helper to quickly iterator over adjacent nine cells, including (0, 0) itself.
     */
    private final int[] di0 = new int[]{-1, -1, -1, 0, 1, 1, 1, 0, 0};
    /**
     * Static helper to quickly iterator over adjacent nine cells, including (0, 0) itself.
     */
    private final int[] dj0 = new int[]{-1, 0, 1, 1, 1, 0, -1, -1, 0};

    /**
     * An array of uncovered CellStates for quickly getting the desired state.
     */
    private final CellState[] uncoveredStates = new CellState[]{
            CellState.UNC0, CellState.UNC1, CellState.UNC2, CellState.UNC3,
            CellState.UNC4, CellState.UNC5, CellState.UNC6, CellState.UNC7, CellState.UNC8
    };

    /**
     * The number of mines still covered.
     */
    private int NCovered;
    /**
     * Current state of the game.
     */
    private GameState gameState;
    /**
     * 2D array monitoring if a cell is a mine. Must be private to keep it invisible.
     * Save data in separate arrays, instead of an array of objects.
     * This increases CPU-Register cache hit so can run faster
     */
    private boolean[][] isMine;
    /**
     * The number of mines of the adjacent cells.
     */
    private int[][] mineCnt;

    public CellState[][] getStates() {
        return states;
    }

    /**
     * 2D array monitoring the current public state of each cell.
     */
    private CellState[][] states;

    /**
     * The dimension of the board.
     */
    private int N;
    /**
     * The count of total number of mines in the board.
     */
    private int NMines;

    public Board(int N, int NMines) {
        // parameters should have be checked before here
        // if, somehow, they are still invalid, overwrite them with defaults
        if (N < 10 || N > 1000 || NMines < 1 || NMines > N * N) {
            N = 20;
            NMines = 50;
        }

        this.N = N;
        this.NCovered = N * N;
        this.NMines = NMines;

        isMine = new boolean[N][N];
        // mineCnt should be one unit larger than the actual board
        mineCnt = new int[N + 2][N + 2];
        states = new CellState[N][N];

        putMines();

        for (int i = 0; i < N; i++)
            Arrays.fill(states[i], CellState.COVERED);

        gameState = GameState.ONGOING;
    }

    public void reset() {
        this.NCovered = N * N;
        isMine = new boolean[N][N];
        // mineCnt should be one unit larger than the actual board
        mineCnt = new int[N + 2][N + 2];
        states = new CellState[N][N];

        putMines();

        for (int i = 0; i < N; i++)
            Arrays.fill(states[i], CellState.COVERED);

        gameState = GameState.ONGOING;
    }

    /**
     * Return the number of mines still covered.
     *
     * @return the number of mines still covered.
     */
    public int getNCovered() {
        return NCovered;
    }

    /**
     * Randomly place mines on the board, and initiating mineCnt.
     */
    private void putMines() {
        Random rand = new Random();
        int mines = NMines;
        while (mines-- > 0) {
            int pos = rand.nextInt(NCovered);
            int x = pos % N;
            int y = pos / N;
            if (isMine[y][x]) // already a mine here
                mines++;
            else {
                isMine[y][x] = true; // place a mine here
                // update neighbor cnt
                for (int d = 0; d < di.length; d++) {
                    mineCnt[y + di[d] + 1][x + dj[d] + 1]++;
                }
            }
        }
    }

    /**
     * When the game stopps, display covered mines. The method automatically flags all mines not flagged by user
     * if he wins, displays mines as mines if he loses.
     * <p>
     * For the cells misflagged, not a mine, it is displayed as a cross-mine image (WRONG_FLAG).
     *
     * @param won win or loss
     * @see Graphics
     */
    private void uncoverAll(boolean won) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (states[i][j] == CellState.COVERED && isMine[i][j]) {
                    states[i][j] = won ? CellState.FLAGGED : CellState.MINE;
                } else if (states[i][j] == CellState.FLAGGED && !isMine[i][j]) {
                    states[i][j] = CellState.WRONG_FLAG;
                }
            }
        }
    }

    /**
     * Breadth first search, auto expand to reach non-UNC0 frontier.
     * @param row the row index of the cell
     * @param col the column index of the cell
     */
    private void bfs(int row, int col) {
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
//            System.out.println(q.toString());
            int r = q.peek() / N;
            int c = q.poll() % N;
//            System.out.println("state:"+states[r][c].toString());

            if (states[r][c] != CellState.COVERED)
                continue;

            states[r][c] = uncoveredStates[mineCnt[r + 1][c + 1]];
            NCovered--; // decrease ACovered now
            if (NMines == NCovered) {
                gameState = GameState.WON;
                uncoverAll(true); // uncover all in win mode
            }

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

    /**
     * Parameter The first Step to uncover a cell. A primary check is performed first to see if unveiling this cell
     * will end the game. If so, the game is stopped, else, a Bread-first search will be performed on the concerning
     * cell and auto expand the region if needed.<p>
     * Note that {@code Graphics g} is not manually disposed in this method, instead it should be disposed by the caller
     * , the constructor of graphics. The game may end by this method.
     * <p>
     * Change the state of cell only in bfs, do not change in this method.
     *
     * @param row row index of the cell
     * @param col column index of the cell
     * @return true if successfully uncover a mine, end the game, or uncover a region; false if hit a mine, lose the
     * game or click on an invalid cell
     */
    public boolean uncoverCell(int row, int col) {
        if (states[row][col] != CellState.COVERED) // click on non-covered tile, ignore
            return false;
        if (isMine[row][col]) { // hit a mine
            gameState = GameState.LOST;
            states[row][col] = CellState.FIRED_MINE;
            uncoverAll(false);
        } else {
            NCovered--;
//            states[row][col] = uncoveredStates[getMineCnt(row, col)];
            if (NCovered == NMines) { // win
                states[row][col] = uncoveredStates[mineCnt[row+1][col+1]];
                gameState = GameState.WON;
                uncoverAll(true); // uncover all in win mode
            } else
                bfs(row, col); // uncover, game will move on
        }
        return true;
    }


    /**
     * Inferring on the cell on an LR click callback. The game may end by this method calling {@code uncover}
     *
     * @param row row index of the cell
     * @param col column index of the cell
     * @return true if can infer and expand; false if click on invalid cells or lack conditions to auto uncover
     */
    public boolean inferOnCell(int row, int col) {
        // check (row, col) is uncovered and non zero
        CellState state = states[row][col];
        if (state == CellState.UNC0 || state == CellState.COVERED) { // nothing to infers
            return false;
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
                        uncoverCell(_r, _c);
                    }
                }
            }
            return true; // performed inference
        } else {
            // return false to inform the game to blink
            return false;
        }
    }

    /**
     * Restore the appearance of temporarily marked as UNC0 cells.
     * Use di0 and dj0, need to blink the center cell as well.
     * @param row
     * @param col
     */
    public void restoreTempUNC0(int row, int col) {
        for (int i = 0; i < di0.length; i++) {
            int _r = row + di0[i];
            int _c = col + dj0[i];
            if (_r >= 0 && _r < N && _c >= 0 && _c < N) { // prevent out of bound
                if (getCellState(_r, _c) == CellState.TEMP_UNC0) {
                    // set TEMP_UNC0 back to COVERED
                    states[_r][_c] = CellState.COVERED;
                }
            }
        }
    }

    /**
     * Temporarily change the appearance surrounding cells of (row, col) to UNC0;
     * @param row the center cell row index
     * @param col the center cell column index
     */
    public void changeTempToUNC0(int row, int col) {
        for (int i = 0; i < di0.length; i++) {
            int _r = row + di0[i];
            int _c = col + dj0[i];
            if (_r >= 0 && _r < N && _c >= 0 && _c < N) { // prevent out of bound
                if (getCellState(_r, _c) == CellState.COVERED) {
                    // will change back later, does not affect the game
                    states[_r][_c] = CellState.TEMP_UNC0;
                }
            }
        }
    }

    /**
     * Offset (row, col) indices by 1 to find its mineCnt.
     * @param row row index
     * @param col column index
     * @return mine counter of this cell
     */
    protected int getMineCnt(int row, int col) {
        return mineCnt[row + 1][col + 1];
    }

    /**
     * Get the cell state of (row, col)
     * @param row row index
     * @param col column index
     * @return the cellState at (row, col)
     */
    public CellState getCellState(int row, int col) {
        return states[row][col];
    }

    /**
     * Flag or un-flag a cell.
     * @param row row index
     * @param col column index
     * @return the change of mines remaining to be discovered. -1 if flag a cell; 1 if unflag a cell; 0 if not viable.
     */
    public int toggleFlag(int row, int col) {
        int delta = 0;
        if (states[row][col] == CellState.COVERED) {
            // flag one cell
            states[row][col] = CellState.FLAGGED;
            delta = -1;
        } else if (states[row][col] == CellState.FLAGGED) {
            states[row][col] = CellState.COVERED;
            delta = 1;
        }
        return delta;
    }

    /**
     * Get current game state.
     * @return
     */
    public GameState getGameState() {
        return gameState;
    }
}
