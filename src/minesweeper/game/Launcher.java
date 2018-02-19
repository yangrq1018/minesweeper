package minesweeper.game;


import javax.swing.*;

public class Launcher {

    public static void main(String[] args) throws Exception {
        // mac menubar
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        // default parameters
        int N = 10, NMines = 10;

        String[] options = {"Easy", "Advanced", "Professional"};
        String diff = (String) JOptionPane.showInputDialog(null, "Choose difficulty", "Difficulty", JOptionPane.QUESTION_MESSAGE,
        null, options, options[0]);

        switch (diff) {
            case "Easy":
                N = 10;
                NMines = 10;
                break;
            case "Advanced":
                N = 20;
                NMines = 50;
                break;
            case "Professional":
                N = 30;
                NMines = 100;
                break;
        }

//         you can use a greater N if you have a huge display
        if (N < 10 || N > 1000 || NMines < 1 || NMines > N * N) {
            System.exit(-1);
        }

        Game game = new Game("Minesweeper", N, NMines);
        game.start();
    }

}
