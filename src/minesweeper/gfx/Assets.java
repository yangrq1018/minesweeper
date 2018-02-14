package minesweeper.gfx;

import minesweeper.game.states.CellState;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/*
 * Class Assets:
 * 		- Crop icons from the sprite sheet and cache as static buffered images
 * 		- Draw icons to the board
 * */
public class Assets {

    // the icons in the sprite sheet are 16 pixels wide
    public static final int width = 16;
    public static final int numberWidth = 11;
    public static final int numberHeight = 21;

    public static BufferedImage[] uncovered = new BufferedImage[9];
    public static BufferedImage covered;
    public static BufferedImage mine, flag;
    public static BufferedImage bombMine, wrongFlag;
    public static BufferedImage[] number = new BufferedImage[10];
    public static BufferedImage hyphen;

    public static void init() {
        SpriteSheet predatorSheet = new SpriteSheet(loadImage("res/predatorskin.bmp"));
        SpriteSheet cloneSheet = new SpriteSheet(loadImage("res/cloneskin.bmp"));

        for (int i = 0; i < uncovered.length; i++) {
            uncovered[i] = predatorSheet.crop(0, i, width);
        }

        covered = predatorSheet.crop(1, 0, width);
        mine = predatorSheet.crop(1, 2, width);
        flag = predatorSheet.crop(1, 3, width);
        wrongFlag = predatorSheet.crop(1, 4, width);
        bombMine = predatorSheet.crop(1, 5, width);

        for (int i=0; i < 10; i++) {
            if (i==0){
                number[i] = cloneSheet.crop(0, width*2+1, numberWidth, numberHeight);
            }else {
                number[i] = cloneSheet.crop(i*numberWidth+i, width*2+1, numberWidth, numberHeight);
            }
        }
        hyphen = cloneSheet.crop(10*numberWidth+10, width*2+1, numberWidth, numberHeight);
    }

    private static void saveNumberImage(BufferedImage img, String fn) {
        try {
            File outputFile = new File("/Users/RockyYANG/Documents/workspace/minesweeper/"+fn);
            ImageIO.write(img, "bmp", outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public static void draw(int row, int col, CellState state, Graphics g) {
        BufferedImage img = covered;

        switch (state) {
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

        g.drawImage(img, width * col, width * row, null);
    }

    public static BufferedImage loadImage(String path) {
        try {
            return ImageIO.read(Assets.class.getResource(path));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }
}
