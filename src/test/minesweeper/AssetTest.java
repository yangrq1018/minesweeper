package minesweeper;

import minesweeper.gfx.Assets;
import org.junit.jupiter.api.Test;

class AssetTest {
    @Test
    public void testImageLoading() {
        Assets.init();
    }
}