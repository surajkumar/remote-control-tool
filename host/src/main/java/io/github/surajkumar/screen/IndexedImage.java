package io.github.surajkumar.screen;

import java.awt.image.BufferedImage;

public record IndexedImage(BufferedImage image,
                           int row,
                           int column) {
}
