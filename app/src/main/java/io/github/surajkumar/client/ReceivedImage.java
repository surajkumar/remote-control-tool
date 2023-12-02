package io.github.surajkumar.client;

import java.awt.image.BufferedImage;

public record ReceivedImage(BufferedImage image,
                           int rows,
                           int columns,
                           int x,
                           int y) {
}