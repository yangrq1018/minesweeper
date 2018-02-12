package minesweeper.game.states;

public enum CellState {
    COVERED (-1),
    FLAGGED (-2),
    UNC0 (0),
    UNC1 (1),
    UNC2 (2),
    UNC3 (3),
    UNC4 (4),
    UNC5 (5),
    UNC6 (6),
    UNC7 (7),
    UNC8 (8),
    WRONG_FLAG(-1),
    MINE(-1),
    FIRED_MINE(-1),
    TEMP_UNC0(-999);

    public int getValue() {
        return value;
    }

    private final int value;
    CellState(int i) {
        value = i;
    }

    public static boolean isUncovered(CellState state) {
        if (state == COVERED || state == FLAGGED || state == UNC0) {
            return false;
        }
        return true;
    }
}
