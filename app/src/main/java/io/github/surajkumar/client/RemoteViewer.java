package io.github.surajkumar.client;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RemoteViewer extends JPanel {
    private static final Logger LOGGER = LoggerFactory.getLogger(RemoteViewer.class);

    public RemoteViewer(NetSocket socket) {
        Rectangle size = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration()
                .getBounds();

        int height = (int) (size.getHeight() * 0.7);
        int width = (int) (size.getWidth() * 0.7);

        JFrame frame = new JFrame();
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.setBackground(Color.BLACK);
        frame.add(this, BorderLayout.CENTER);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        MouseEventListener mouseEventListener = new MouseEventListener(this, socket);
        this.addMouseListener(mouseEventListener);
        this.addMouseMotionListener(mouseEventListener);
        this.addKeyListener(new KeyboardEventListener(socket));
        this.setDoubleBuffered(true);
        pre_rendered = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
    }

    private final List<ReceivedImage> receivedImages = new ArrayList<>();
    private BufferedImage pre_rendered;

    public void draw(Buffer buffer) {
        ReceivedImage receivedImage;
        try {
            receivedImage = bufferToImage(buffer);
        } catch (IOException e) {
            LOGGER.error("Failed to convert received data into an image: " + e.getMessage());
            return;
        }

        if(contains(receivedImage)) {
            ReceivedImage cached = get(receivedImage);
            receivedImages.remove(cached);
            receivedImages.add(receivedImage);
        } else {
            receivedImages.add(receivedImage);
        }

        if(pre_rendered.getHeight() != getHeight() || pre_rendered.getWidth() != getWidth()) {
            pre_rendered = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            pre_rendered.getGraphics().setColor(Color.BLACK);
            pre_rendered.getGraphics().fillRect(0, 0, getWidth(), getHeight());
        }

        Graphics2D graphics = (Graphics2D) pre_rendered.getGraphics();
        graphics.setColor(Color.BLACK);

        int cellWidth = getWidth() / receivedImage.columns();
        int cellHeight = getHeight() / receivedImage.rows();
        int drawX = receivedImage.y() * cellWidth;
        int drawY = receivedImage.x() * cellHeight;

        graphics.drawImage(
                resizeImage(receivedImage.image(), cellWidth, cellHeight),
                drawX,
                drawY,
                cellWidth,
                cellHeight,
                this
        );

        repaint();
    }


    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.drawImage(pre_rendered, 0, 0, this);
    }

    private boolean contains(ReceivedImage receivedImage) {
        for(ReceivedImage r : receivedImages) {
            if(r.x() == receivedImage.x() && r.y() == receivedImage.y()) {
                return true;
            }
        }
        return false;
    }

    private ReceivedImage get(ReceivedImage receivedImage) {
        for(ReceivedImage r : receivedImages) {
            if(r.x() == receivedImage.x() && r.y() == receivedImage.y()) {
                return r;
            }
        }
        return receivedImage;
    }

    private static ReceivedImage bufferToImage(Buffer buffer) throws IOException {
        int position = 0;

        int rowSize = buffer.getInt(position);
        position += 4;
        int colSize = buffer.getInt(position);
        position += 4;

        int row = buffer.getInt(position);
        position += 4;
        int column = buffer.getInt(position);
        position += 8;
        byte[] image = buffer.getBytes(position, buffer.length());
        return new ReceivedImage(ImageIO.read(new ByteArrayInputStream(image)), rowSize, colSize, row, column);
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
