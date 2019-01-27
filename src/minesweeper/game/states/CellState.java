package minesweeper.game.states;

/**
 * Enum class to indicate the public state of a cell. The information stored in CellState[][] states should be free to access because it is the
 * public interface of the board through which the player perceives the game.
 */
public enum CellState {
    /**
     * The cell is masked with blank.
     */
    COVERED (-1),
    /**
     * The cell is toggled with a flag to mark a mine under it.
     */
    FLAGGED (-2),
    /**
     * Reveal no mines around. Exposed as blank.
     */
    UNC0 (0),
    /**
     * One mine around.
     */
    UNC1 (1),
    /**
     * Two mines around.
     */
    UNC2 (2),
    /**
     * Three mines around.
     */
    UNC3 (3),
    /**
     * Four mines around.
     */
    UNC4 (4),
    /**
     * Five mines around.
     */
    UNC5 (5),
    /**
     * Six mines around.
     */
    UNC6 (6),
    /**
     * Seven mines around.
     */
    UNC7 (7),
    /**
     * Eight mines around.
     */
    UNC8 (8),
    /**
     * Used when {@code uncoverAll}, indicating a toggled flag where there is no mine beneath.
     */
    WRONG_FLAG(-1),
    /**
     * A normal mine, already flagged.
     */
    MINE(-1),
    /**
     * A Triggered mine.
     */
    FIRED_MINE(-1),
    /**
     * A temporary CellState when cannot auto uncovered some cells. In mousePressed event callback, the board will
     * temporarily set those cells to UNC0 blank state and mark them in the 2D array as {@code TEMP_UNC0}. Later
     * in mouseRelease callback, the board will redraw their look and set their states back to {@code COVERED} again.
     */
    TEMP_UNC0(-999);

    private final int value;

    CellState(int i) {
        value = i;
    }

    /**
     * Quickly check if the cell is uncovered with information. i.e. one of the states of UNC1-9. This method is used
     * to perform a preliminary check to decide if possible perform an inferring uncover.
     *
     * @param state the state to test
     * @return true if is UNC1-9, otherwise false
     */
    public static boolean isUncovered(CellState state) {
        if (state == COVERED || state == FLAGGED || state == UNC0) {
            return false;
        }
        return true;
    }
    
    public static boolean isCovered(CellState state) {
        return (state == Covered);
    }

    /**
     * Return the count of mines around of the eight adjacent cells of this cell. It is needed to convert an enumerated
     * object to the numeric data it represents.
     *
     * @return
     */
    public int getValue() {
        return value;
    }
}
