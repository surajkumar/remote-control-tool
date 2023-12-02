package io.github.surajkumar.server.screen;

import java.awt.image.BufferedImage;

public record IndexedImage(BufferedImage image, int row, int column) {}
