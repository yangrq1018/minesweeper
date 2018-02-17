package minesweeper;

import minesweeper.game.Game;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExampleTest {
    @BeforeAll
    public static void init() {
    }

    @Test
    public void myFirstTest() {
//        assertEquals(2, 1 + 1, "one plus one should be two");
        Game game;
        game = new Game("Minesweeper", 20, 50);
        game.start();
        assertEquals(game.isFinished(), false, "game not finished at the stage");
    }
}