import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Stack;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.*;
import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ImageEditorPanel extends JPanel implements KeyListener, MouseListener  {

    Color[][] pixels;

    
    public ImageEditorPanel() {
        BufferedImage imageIn = null;
        try {
            // the image should be in the main project folder, not in \src or \bin
            imageIn = ImageIO.read(new File("lizzolarge.jpeg"));
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
        pixels = makeColorArray(imageIn);
        setPreferredSize(new Dimension(pixels[0].length, pixels.length));
        setBackground(Color.BLACK);
        addKeyListener(this);
        addMouseListener(this);
    }

    public void paintComponent(Graphics g) {
        // paints the array pixels onto the screen
        for (int row = 0; row < pixels.length; row++) {
            for (int col = 0; col < pixels[0].length; col++) {
                g.setColor(pixels[row][col]);
                g.fillRect(col, row, 1, 1);
            }
        }
    }

    public void run() {
        // call your image-processing methods here OR call them from keyboard event
        // handling methods
        // write image-processing methods as pure functions - for example: pixels =
        // pixels = flipHorizontal(pixels);
        // pixels = flipVertical(pixels);
   
        repaint();
    }
    public static Color[][] blur(Color[][] pixels) {
        int height = pixels.length;
        int width = pixels[0].length;
        Color[][] newPixels = new Color[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                int totalRed = 0, totalGreen = 0, totalBlue = 0;

                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        int newRow = row + i;
                        int newCol = col + j;

                        if (newRow >= 0 && newRow < height && newCol >= 0 && newCol < width) {
                            totalRed += pixels[newRow][newCol].getRed();
                            totalGreen += pixels[newRow][newCol].getGreen();
                            totalBlue += pixels[newRow][newCol].getBlue();
                        }
                    }
                }

                int avgRed = totalRed / 9;
                int avgGreen = totalGreen / 9;
                int avgBlue = totalBlue / 9;

                newPixels[row][col] = new Color(avgRed, avgGreen, avgBlue);
            }
        }
        return newPixels;
    }

    public Color[][] deepFry() {
        int width = pixels[0].length;
        int height = pixels.length;

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Color originalColor = pixels[row][col];

                // Increase red and green, decrease blue
                int newRed = Math.min(255, originalColor.getRed() + 30);
                int newGreen = Math.min(255, originalColor.getGreen() + 30);
                int newBlue = Math.max(0, originalColor.getBlue() - 30);

                pixels[row][col] = new Color(newRed, newGreen, newBlue);
            }
        }
        return pixels;
    }

    public Color[][] applyVintageFilter(Color[][] oldPixels) {
        Color[][] newPixels = new Color[oldPixels.length][oldPixels[0].length];

        for (int row = 0; row < oldPixels.length; row++) {
            for (int col = 0; col < oldPixels[0].length; col++) {
                // Extract RGB values
                int red = oldPixels[row][col].getRed();
                int green = oldPixels[row][col].getGreen();
                int blue = oldPixels[row][col].getBlue();

                // Apply vintage filter by reducing intensity
                int newRed = (int) (red * 0.8);
                int newGreen = (int) (green * 0.9);
                int newBlue = (int) (blue * 0.7);

                // Ensure values are within the valid range
                newRed = Math.min(255, Math.max(0, newRed));
                newGreen = Math.min(255, Math.max(0, newGreen));
                newBlue = Math.min(255, Math.max(0, newBlue));

                // Create a new Color object with the modified values
                newPixels[row][col] = new Color(newRed, newGreen, newBlue);
            }
        }

        return newPixels;
    }

    public static Color[][] grayscale(Color[][] oldPixels) {
        int height = oldPixels.length;
        int width = oldPixels[0].length;

        Color[][] newPixels = new Color[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Color oldColor = oldPixels[row][col];

                int average = (oldColor.getRed() + oldColor.getGreen() + oldColor.getBlue()) / 3;

                Color newColor = new Color(average, average, average);

                newPixels[row][col] = newColor;
            }
        }

        return newPixels;
    }


    public static Color[][] flipHorizontal(Color[][] oldPixels) {
        int width = oldPixels[0].length;
        int height = oldPixels.length;
        Color[][] newPixels = new Color[height][width];

        for (int i = 0; i < height; i++) {
            for (int c = width - 1, j = 0; c >= 0; c--, j++) {
                newPixels[i][j] = oldPixels[i][c];
            }
        }

        return newPixels;
    }

    public static Color[][] flipVertical(Color[][] oldPixels) {
        int width = oldPixels[0].length;
        int height = oldPixels.length;
        Color[][] newPixels = new Color[height][width];

        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                newPixels[row][col] = oldPixels[height - 1 - row][col];
            }
        }
        return newPixels;
    }


    public Color[][] makeColorArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        Color[][] result = new Color[height][width];
        
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Color c = new Color(image.getRGB(col, row), true);
                result[row][col] = c;
            }
        }
        // System.out.println("Loaded image: width: " +width + " height: " + height);
        return result;
    }

    public void saveImage(String filePath, String format) {
        BufferedImage imageOut = new BufferedImage(pixels[0].length, pixels.length, BufferedImage.TYPE_INT_RGB);

        for (int row = 0; row < pixels.length; row++) {
            for (int col = 0; col < pixels[0].length; col++) {
                imageOut.setRGB(col, row, pixels[row][col].getRGB());
            }
        }

        try {
            ImageIO.write(imageOut, format, new File(filePath));
            System.out.println("Image saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving image: " + e);
        }
    }

public void keyPressed(KeyEvent e) {
    if (e.getKeyChar() == 'f') { //deepfry filter
    run();
    pixels = deepFry();
        }

    if (e.getKeyCode() == KeyEvent.VK_DOWN) { //flips image verticle
    run();
    pixels = flipVertical(pixels);
        }

    if (e.getKeyCode() == KeyEvent.VK_UP) { // flips image horizontally
    run();
    pixels = flipHorizontal(pixels);
        }

    if (e.getKeyChar() == 'g') { //grayscale filter
        run();
        pixels = grayscale(pixels);
        }
    
    if (e.getKeyChar() == 'v') { //vintage filter
        run();
        pixels = applyVintageFilter(pixels);
        }
    if (e.getKeyChar() == 'b') { //blur filter
        run();
        pixels = blur(pixels);
    }
    if (e.getKeyChar() == 's') { //saves image to the project folder
        run();
        saveImage("edited_image.png", "png");
    }
  
}
public void keyReleased(KeyEvent e) {
//if (e.getKeyCode() == KeyEvent.VK_LEFT) {
// call event handling methods
}

public void keyTyped(KeyEvent e) {
// note the difference between getKeyChar and getKeyCode
if (e.getKeyChar() == 'q'){
   
}

}

public void mouseClicked(MouseEvent e) {
// called when the mouse is pressed and released quickly
}
public void mouseEntered(MouseEvent e) {
// called when the mouse enters the window
}
public void mouseExited(MouseEvent e) {
// called when the mouse leaves the window
}
public void mousePressed(MouseEvent e) {
//int pressX = e.getX();
//int pressY = e.getY();
// set a variable based on mouse coordinates
// or check a condition based on mouse coordinates
}
public void mouseReleased(MouseEvent e) {
//int releaseX = e.getX();
//int releaseY = e.getY();
// set a variable based on mouse coordinates
// or check a condition based on mouse coordinates
}
}
