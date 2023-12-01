package io.github.surajkumar.host.screen;

import java.awt.image.BufferedImage;

public class IndexedImage {
    private final int rowIndex;
    private final int colIndex;
    private final BufferedImage image;

    public IndexedImage(int rowIndex, int colIndex, BufferedImage image) {
        this.rowIndex = rowIndex;
        this.colIndex = colIndex;
        this.image = image;
    }

    public int getRowIndex() {
        return rowIndex;
    }

    public int getColIndex() {
        return colIndex;
    }

    public BufferedImage getImage() {
        return image;
    }
}
