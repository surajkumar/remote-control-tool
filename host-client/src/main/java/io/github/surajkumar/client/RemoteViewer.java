package io.github.surajkumar.client;

import io.github.surajkumar.host.screen.Grid;
import io.github.surajkumar.host.screen.IndexedImage;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class RemoteViewer extends JPanel {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteViewer.class);

    public RemoteViewer(NetSocket socket) {
        JFrame frame = new JFrame();
        frame.setSize(1080, 720);
        frame.setLocationRelativeTo(null);
        frame.setBackground(Color.BLACK);
        frame.add(this, BorderLayout.CENTER);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setDoubleBuffered(true);

        MouseEventListener mouseEventListener = new MouseEventListener(this, socket);
        this.addMouseListener(mouseEventListener);
        this.addMouseMotionListener(mouseEventListener);
        this.addKeyListener(new KeyboardEventListener(socket));
    }

    public void draw(Buffer buffer) {
        IndexedImage indexedImage;
        try {
            indexedImage = toIndexedImage(buffer);
        } catch (IOException e) {
            LOGGER.error("Failed to convert received data into an image: " + e.getMessage());
            return;
        }
        if (indexedImage != null) {

            int cellWidth = getWidth() / Grid.COLUMNS;
            int cellHeight = getHeight() / Grid.ROWS;
            int drawX = indexedImage.getColIndex() * cellWidth;
            int drawY = indexedImage.getRowIndex() * cellHeight;

            this.getGraphics()
                    .drawImage(
                            resizeImage(indexedImage.getImage(), cellWidth, cellHeight),
                            drawX,
                            drawY,
                            cellWidth,
                            cellHeight,
                            this);
        }
    }

    private static IndexedImage toIndexedImage(Buffer buffer) throws IOException {
        if (buffer.length() < 16) {
            System.out.println("Too small of a buffer received");
            return null;
        }
        int position = 0;
        int column = buffer.getInt(position);
        position += 4;
        int row = buffer.getInt(position);
        position += 4;
        int len = buffer.getInt(position);
        position += 4;
        byte[] image = buffer.getBytes(position, position + len);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(image);
        return new IndexedImage(row, column, ImageIO.read(byteArrayInputStream));
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int newWidth, int newHeight) {
        BufferedImage resizedImage =
                new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        AffineTransform transform =
                AffineTransform.getScaleInstance(
                        (double) newWidth / originalImage.getWidth(),
                        (double) newHeight / originalImage.getHeight());
        AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BICUBIC);
        op.filter(originalImage, resizedImage);
        g.dispose();
        return resizedImage;
    }
}
