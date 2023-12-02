package io.github.surajkumar.screen;

import java.awt.image.BufferedImage;

public class ImageUtils {

    private ImageUtils() {}

    public static BufferedImage[][] splitImage(BufferedImage image, int rows, int cols) {
        BufferedImage[][] subImages = new BufferedImage[rows][cols];

        int cellWidth = image.getWidth() / cols;
        int cellHeight = image.getHeight() / rows;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int x = j * cellWidth;
                int y = i * cellHeight;
                subImages[i][j] = image.getSubimage(x, y, cellWidth, cellHeight);
            }
        }

        return subImages;
    }

    public static boolean isImageDifferent(BufferedImage image1, BufferedImage image2) {
        if (image1 == null || image2 == null) {
            return true;
        }
        for (int x = 0; x < image1.getWidth(); x++) {
            for (int y = 0; y < image1.getHeight(); y++) {
                if (image1.getRGB(x, y) != image2.getRGB(x, y)) {
                    return true;
                }
            }
        }
        return false;
    }
}
