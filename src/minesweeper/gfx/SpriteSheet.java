package minesweeper.gfx;

import java.awt.image.BufferedImage;

public class SpriteSheet {

    private BufferedImage sheet;

    public SpriteSheet(BufferedImage sheet) {
        this.sheet = sheet;
    }

    /**
     * Crop an icon.
     *
     * @param row   row index
     * @param col   column index
     * @param width the dimension of the icon
     * @return image cropped
     */
    public BufferedImage crop(int row, int col, int width) {
        int y = width * row;
        int x = width * col;
        return sheet.getSubimage(x, y, width, width);
    }

    /**
     * Crop a certain area in the sheet.
     * @param x x value
     * @param y y value
     * @param deltaX width
     * @param deltaY height
     * @return image cropped
     */
    public BufferedImage crop(int x, int y, int deltaX, int deltaY) {
        return sheet.getSubimage(x, y, deltaX, deltaY);
    }
}
