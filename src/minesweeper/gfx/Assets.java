package minesweeper.gfx;

import minesweeper.game.states.CellState;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

/**
 * Class Assets: <p>
 * Crop icons from the sprite sheet and cache as static buffered images
 * Static methods to draw icons with graphics
 */
public class Assets {
    /**
     * The mine icons are 16 pixels wide
     */
    public static final int width = 16;
    public static final int numberWidth = 12;
    public static final int numberHeight = 23;
    public static final int faceHeight = 32;
    public static final int faceWidth = 32;

    public static BufferedImage[] uncovered = new BufferedImage[9];
    public static BufferedImage covered;
    public static BufferedImage mine, flag;
    public static BufferedImage bombMine, wrongFlag;
    public static BufferedImage[] number = new BufferedImage[10];
    public static BufferedImage hyphen;
    public static BufferedImage smile, onclick, win, lose;

    public static void init() {
        System.out.println("Loading static files:");
        SpriteSheet predatorSheet = new SpriteSheet(loadImage("static/predatorskin.bmp"));
        System.out.println("predatorskind ok");

        SpriteSheet cloneSheet = new SpriteSheet(loadImage("static/cloneskin.bmp"));
        System.out.println("cloneskin ok");


        for (int i = 0; i < uncovered.length; i++) {
            uncovered[i] = predatorSheet.crop(0, i, width);
        }

        covered = predatorSheet.crop(1, 0, width);
        mine = predatorSheet.crop(1, 2, width);
        flag = predatorSheet.crop(1, 3, width);
        wrongFlag = predatorSheet.crop(1, 4, width);
        bombMine = predatorSheet.crop(1, 5, width);

        for (int i=0; i < 10; i++) {
            number[i] = cloneSheet.crop(i * numberWidth, width * 2, numberWidth, numberHeight);
        }
        hyphen = cloneSheet.crop(10 * numberWidth, width * 2, numberWidth, numberHeight);

        smile = loadImage("static/smile.png");
        onclick = loadImage("static/onclick.png");
        win = loadImage("static/win.png");
        lose = loadImage("static/lose.png");
    }


    public static void draw(int row, int col, CellState state, Graphics g) {
        Image img = cellState2Image(state);
        g.drawImage(img, width * col, width * row, null);
    }

    public static Image cellState2Image(CellState cellState) {
        BufferedImage img = covered;

        switch (cellState) {
            case COVERED:
                img = covered;
                break;
            case FLAGGED:
                img = flag;
                break;
            case TEMP_UNC0: // used for blink
                img = uncovered[0];
                break;
            case UNC0:
                img = uncovered[0];
                break;
            case UNC1:
                img = uncovered[1];
                break;
            case UNC2:
                img = uncovered[2];
                break;
            case UNC3:
                img = uncovered[3];
                break;
            case UNC4:
                img = uncovered[4];
                break;
            case UNC5:
                img = uncovered[5];
                break;
            case UNC6:
                img = uncovered[6];
                break;
            case UNC7:
                img = uncovered[7];
                break;
            case UNC8:
                img = uncovered[8];
                break;
            case WRONG_FLAG:
                img = wrongFlag;
                break;
            case MINE:
                img = mine;
                break;
            case FIRED_MINE:
                img = bombMine;
                break;
        }
        return img;
    }

    private static BufferedImage loadImage(String name) {
        try {
            ClassLoader classloader = Assets.class.getClassLoader();
            URL url = classloader.getResource(name);
            assert url != null;
            return ImageIO.read(url);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

    public static void drawMinesCnt(int left, Graphics g) {
        BufferedImage[] imgs = intToBfimgArray(left);
        for (int i = 0; i < 3; i++) {
            g.drawImage(imgs[i], i * numberWidth, 0, null);
        }
    }

    public static void drawTime(int elapse, Graphics g, int N) {
        BufferedImage[] imgs = intToBfimgArray(elapse);
        for (int i = 0; i < 3; i++) {
            g.drawImage(imgs[i], N * width + i * numberWidth - 3 * numberWidth, 0, null);
        }
    }

    public static Image stringToFaceImg(String face) {
        Image img;
        switch (face) {
            case "smile":
                img = Assets.smile;
                break;
            case "onclick":
                img = Assets.onclick;
                break;
            case "lose":
                img = Assets.lose;
                break;
            case "win":
                img = Assets.win;
                break;
            default:
                img = Assets.smile;
                break;
        }
        return img;
    }

    /**
     * 12 -> "012"
     * Three digits at most
     *
     * @param digits number
     * @return the array of {@link BufferedImage} of size three that represents the number
     */
    private static BufferedImage[] intToBfimgArray(int digits) {
        char[] text = String.format("%03d", digits).toCharArray();
        BufferedImage[] bfimg = new BufferedImage[3];
        BufferedImage img = number[0];
        for (int i = 0; i < 3; i++) {
            char e = text[i];
            switch (e) {
                case '0':
                    img = number[0];
                    break;
                case '1':
                    img = number[1];
                    break;
                case '2':
                    img = number[2];
                    break;
                case '3':
                    img = number[3];
                    break;
                case '4':
                    img = number[4];
                    break;
                case '5':
                    img = number[5];
                    break;
                case '6':
                    img = number[6];
                    break;
                case '7':
                    img = number[7];
                    break;
                case '8':
                    img = number[8];
                    break;
                case '9':
                    img = number[9];
                    break;
            }
            bfimg[i] = img;
        }
        return bfimg;
    }

}
