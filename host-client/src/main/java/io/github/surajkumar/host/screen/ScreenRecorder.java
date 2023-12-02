package io.github.surajkumar.host.screen;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class ScreenRecorder implements Runnable {
    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(1);
    private static final String IMAGE_FORMAT = "TIFF";
    private static final String COMPRESSION_TYPE = "LZW";
    private static final float COMPRESSION_QUALITY = 0f;
    private static final int MILLISECONDS_IN_SECOND = 1000;
    private final int framesPerSecond;
    private final List<ScreenWatcher> watchers;
    private final Robot robot;
    private final Rectangle screenBounds;
    private boolean running;
    private ScheduledFuture<?> scheduledFuture;
    private final Visualizer visualizer;

    public ScreenRecorder(int framesPerSecond, Rectangle screenBounds) throws AWTException {
        setupWriter();
        this.framesPerSecond = MILLISECONDS_IN_SECOND / framesPerSecond;
        this.watchers = new ArrayList<>();
        this.robot = new Robot();
        this.screenBounds = screenBounds;
        // Crop the visualizer from the screenshot
        screenBounds.setRect(1, 1, screenBounds.getWidth() - 2, screenBounds.getHeight() - 2);
        this.running = false;
        this.visualizer = new Visualizer(new Dimension(Monitor.getBoundsForMonitor(0).getSize()));
    }

    private ImageWriter writer;
    private ImageWriteParam param;

    private void setupWriter() {
        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName(IMAGE_FORMAT);
        if (!it.hasNext()) {
            throw new UnsupportedOperationException(IMAGE_FORMAT + " format not supported.");
        }
        writer = it.next();
        param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionType(COMPRESSION_TYPE);
        if (param.canWriteCompressed()) {
            param.setCompressionQuality(COMPRESSION_QUALITY);
        }
    }

    private BufferedImage previousFrame;

    @Override
    public void run() {
        BufferedImage image = robot.createScreenCapture(screenBounds);

        if (!imageChanged(image)) {
            return;
        }

        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            ImageOutputStream payload = ImageIO.createImageOutputStream(buffer);
            writer.setOutput(payload);

            try {
                writer.write(null, new IIOImage(image, null, null), param);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            payload.flush();

            watchers.forEach(watcher -> watcher.receiveScreenshot(buffer.toByteArray()));

            previousFrame = image;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean imageChanged(BufferedImage currentFrame) {
        if (previousFrame == null) {
            return true;
        }
        int width = currentFrame.getWidth();
        int height = currentFrame.getHeight();
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (pixelChanged(currentFrame.getRGB(x, y), previousFrame.getRGB(x, y))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean pixelChanged(int pixel1, int pixel2) {
        return pixel1 != pixel2;
    }

    public void start() {
        running = true;
        scheduledFuture =
                EXECUTOR.scheduleAtFixedRate(this, 0, framesPerSecond, TimeUnit.MILLISECONDS);
        visualizer.show();
    }

    public void stop() {
        if (scheduledFuture != null && !scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(false);
        }
        running = false;
        visualizer.hide();
    }

    public void registerWatcher(ScreenWatcher watcher) {
        watchers.add(watcher);
    }

    public void removeWatcher(ScreenWatcher watcher) {
        watchers.remove(watcher);
    }

    public List<ScreenWatcher> getWatchers() {
        return watchers;
    }

    public boolean isRunning() {
        return running;
    }
}
