package minesweeper.game;

public class Solver {
    private final int[] di = new int[]{-1, -1, -1, 0, 1, 1, 1, 0};
    private final int[] dj = new int[]{-1, 0, 1, 1, 1, 0, -1, -1};


    public static void main(String[] args) {
        int N = 20, NMines = 50;

        GameRobot robot = new GameRobot("Minesweeper", N, NMines, 300);
        robot.start();
        robot.autoPlay();
    }
}
