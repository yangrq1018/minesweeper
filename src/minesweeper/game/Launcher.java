package minesweeper.game;


import javax.swing.*;

public class Launcher {

    public static void main(String[] args) {
        // mac menubar
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        // default parameters
        int N = 20, NMines = 50;

//        if (args.length > 0) {
//            try {
//                N = Integer.parseInt(args[0]);
//                NMines = Integer.parseInt(args[1]);
//            } catch (Exception ex) {
//                System.out.println("Invalid parameters!");
//                System.out.println("should be: java -jar Minesweeper.jar [Board_width Number_of_Mines]");
//                System.exit(-1);
//            }
//        }

//        String[] options = {"Easy", "Advanced", "Professional"};
//        String diff = (String) JOptionPane.showInputDialog(null, "Choose difficulty", "Difficulty", JOptionPane.QUESTION_MESSAGE,
//        null, options, options[0]);
//
//        switch (diff) {
//            case "Easy":
//                N = 10;
//                NMines = 15;
//                break;
//            case "Advanced":
//                N = 20;
//                NMines = 30;
//                break;
//            case "Professional":
//                N = 30;
//                NMines = 45;
//                break;
//        }

//         you can use a greater N if you have a huge display
        if (N < 10 || N > 1000 || NMines < 1 || NMines > N * N) {
            System.exit(-1);
        }

        Game game = new Game("Minesweeper", N, NMines);
        game.start();
    }

}
