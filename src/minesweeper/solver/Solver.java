package minesweeper.solver;

public class Solver {
    private final int[] di = new int[]{-1, -1, -1, 0, 1, 1, 1, 0};
    private final int[] dj = new int[]{-1, 0, 1, 1, 1, 0, -1, -1};


    public static void main(String[] args) {
        int N = 20, NMines = Integer.parseInt(args[0]);

        GameRobot robot = new GameRobot("Minesweeper", N, NMines, Integer.parseInt(args[1]));
        robot.start();
        robot.autoPlay();
    }
}
