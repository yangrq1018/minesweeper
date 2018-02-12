package minesweeper.gfx;
// File: ImageTool.java
//
// To convert Java's Image instances to pixel arrays and back.
// Author: Rahul Simha
// Inspired by Dick Baldwin's example: http://www.developer.com/java/other/article.php/3403921
// Modified: Aug 21, 2006
// Modified: Aug 29, 2006

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;

// A JPanel to draw the image. Must override paintComponent.

class ImagePanel extends JPanel {

    Image image;

    public void paintComponent(Graphics g) {
        if (image != null) {
            g.drawImage(image, 0, 0, this);
        } else {
            g.drawString("No image", 50, 50);
        }
    }

} // end of ImagePanel class


// The main class - ImageTool. This has four useful methods:
// (1) readImageFile() - to read an Image in GIF/JPG/PNG format
//     from a file in the current directory. Returns an instance
//     of java.awt.Image after it has been completely read from file.
// (2) imageToPixels() - to convert a Java Image instance into a 2D
//     array. Each 2D pixel is itself four int's: one each for red,
//     blue, green and alpha (transparency).
// (3) pixelsToImage() - to go the other way.
// (4) showImage() - to bring up a frame with the given image.


public class ImageTool extends JFrame {

    static int locationX = 0;
    static int locationY = 0;

    public static void main(String[] argv) {
        ImageTool imTool = new ImageTool();
        Image image = imTool.readImageFile("test.gif");
        imTool.showImage(image, "BEFORE");

        // Now modify image.
        int[][][] pixels = imTool.imageToPixels(image);
        for (int row = 0; row < pixels.length; row += 2) {
            for (int col = 0; col < pixels[row].length; col++) {
                // Zero it out.
                pixels[row][col][0] = 0;
                pixels[row][col][1] = 0;
                pixels[row][col][2] = 0;
                pixels[row][col][3] = 0;
            }
        }
        Image image2 = imTool.pixelsToImage(pixels);
        imTool.showImage(image2, "AFTER");

        // Create a pure-pixel image.
        int size = 500;
        pixels = new int[size][size][4];
        for (int i = 0; i < pixels.length; i++) {
            for (int j = 0; j < pixels[0].length; j++) {
                for (int k = 0; k < 4; k++) {
                    pixels[i][j][k] = 255;
                }
            }
        }

        int start = size / 4;
        int end = (int) (3.0 * size / 4.0);
        for (int i = start; i <= end; i++) {
            for (int j = start; j <= end; j++) {
                for (int k = 1; k < 4; k++) {
                    pixels[i][j][k] = 0;
                }
            }
        }

        Image image3 = imTool.pixelsToImage(pixels);
        imTool.writeToJPGFile(image3, "artificial.jpg");
        Image image4 = imTool.readImageFile("artificial.jpg");
        imTool.showImage(image4, "ARTIFICIAL");

    }

    public void showImage(Image image) {
        showImage(image, "No title");
    }

    public void showImage(Image image, String title) {
        // Make a frame and set parameters.
        JFrame f = new JFrame();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(400, 300);
        f.setTitle(title);
        locationX += 20;
        locationY += 20;
        f.setLocation(locationX, locationY);

        // Add an instance of the image-drawing panel.
        //** Change to include image in ImagePanel's constructor.
        ImagePanel drawPanel = new ImagePanel();
        drawPanel.image = image;
        Container cPane = f.getContentPane();
        cPane.add(drawPanel, BorderLayout.CENTER);

        // Display.
        //** Add a windowclosing listener.
        f.setVisible(true);
    }

    public int[][][] imageToPixels(Image image) {
        // Java's peculiar way of extracting pixels is to give them
        // back as a one-dimensional array from which we will construct
        // our version.
        int numRows = image.getHeight(this);
        int numCols = image.getWidth(this);
        int[] oneDPixels = new int[numRows * numCols];

        // This will place the pixels in oneDPixels[]. Each int in
        // oneDPixels has 4 bytes containing the 4 pieces we need.
        PixelGrabber grabber = new PixelGrabber(image, 0, 0, numCols, numRows,
                oneDPixels, 0, numCols);
        try {
            grabber.grabPixels(0);
        } catch (InterruptedException e) {
            System.out.println(e);
        }

        // Now we make our array.
        int[][][] pixels = new int[numRows][numCols][4];
        for (int row = 0; row < numRows; row++) {
            // First extract a row of int's from the right place.
            int[] aRow = new int[numCols];
            for (int col = 0; col < numCols; col++) {
                int element = row * numCols + col;
                aRow[col] = oneDPixels[element];
            }

            // In Java, the most significant byte is the alpha value,
            // followed by R, then G, then B. Thus, to extract the alpha
            // value, we shift by 24 and make sure we extract only that byte.
            for (int col = 0; col < numCols; col++) {
                pixels[row][col][0] = (aRow[col] >> 24) & 0xFF;  // Alpha
                pixels[row][col][1] = (aRow[col] >> 16) & 0xFF;  // Red
                pixels[row][col][2] = (aRow[col] >> 8) & 0xFF;  // Green
                pixels[row][col][3] = (aRow[col]) & 0xFF;        // Blue
            }
        }

        return pixels;
    }

    public Image pixelsToImage(int[][][] pixels) {
        int numRows = pixels.length;
        int numCols = pixels[0].length;
        int[] oneDPixels = new int[numRows * numCols];

        int index = 0;
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                oneDPixels[index] = ((pixels[row][col][0] << 24) & 0xFF000000)
                        | ((pixels[row][col][1] << 16) & 0x00FF0000)
                        | ((pixels[row][col][2] << 8) & 0x0000FF00)
                        | ((pixels[row][col][3]) & 0x000000FF);
                index++;
            }
        }

        // The MemoryImageSource class is an ImageProducer that can
        // build an image out of 1D pixels. Then, rather confusingly,
        // the createImage() method, inherited from Component, is used
        // to make the actual Image instance. This is simply Java's
        // confusing, roundabout way. An alternative is to use the
        // Raster models provided in BufferedImage.
        MemoryImageSource imSource = new MemoryImageSource(numCols, numRows, oneDPixels, 0, numCols);
        Image I = createImage(imSource);
        return I;

    }

    public Image readImageFile(String fileName) {
        //** This could be upgraded to use Java's new image
        // reading/writing API's.
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image image = tk.getImage(fileName);
        MediaTracker tracker = new MediaTracker(this);
        tracker.addImage(image, 1);
        try {
            tracker.waitForID(1);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
        return image;
    }

    public int[][][] imageFileToPixels(String fileName) {
        Image image = readImageFile(fileName);
        int[][][] pixels = imageToPixels(image);
        return pixels;
    }

    public BufferedImage toBufferedImage(Image image) {
//        if (image instanceof BufferedImage) {
//            return (BufferedImage) image;
//        }

        // Exploit ImageIcon's code that waits for all pixels to be loaded.
        image = new ImageIcon(image).getImage();

        int numRows = image.getHeight(null);
        int numCols = image.getWidth(null);

        // We'll assume RGB + transparency. If not, this has to be fixed.
        BufferedImage bufImage = new BufferedImage(numCols, numRows, BufferedImage.TYPE_INT_ARGB);

        Graphics g = bufImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();

        return bufImage;
    }

    public void writeToJPGFile(Image image, String fileName) {
        try {
            BufferedImage bufImage = toBufferedImage(image);
            ImageIO.write(bufImage, "jpg", new File(fileName));
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void writeToJPGFile(int[][][] pixels, String fileName) {
        Image image = pixelsToImage(pixels);
        writeToJPGFile(image, fileName);
    }

}
