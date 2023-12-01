package io.github.surajkumar.host.screen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
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

public class PartionScreenRecorder implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(PartionScreenRecorder.class);
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
    private ImageWriter writer;
    private ImageWriteParam param;
    private final Visualizer visualizer;
    private BufferedImage previousFrame;

    public PartionScreenRecorder(int framesPerSecond, Rectangle screenBounds) throws AWTException {
        setupWriter();
        this.framesPerSecond = MILLISECONDS_IN_SECOND / framesPerSecond;
        this.watchers = new ArrayList<>();
        this.robot = new Robot();
        this.screenBounds = screenBounds;
        // Crop the visualizer from the screenshot
        screenBounds.setRect(
                1,
                1,
                screenBounds.getWidth() - 2,
                screenBounds.getHeight() - 2);
        this.running = false;
        this.visualizer = new Visualizer(new Dimension(Monitor.getBoundsForMonitor(0).getSize()));
    }

    private void setupWriter() {
        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName(IMAGE_FORMAT);
        if(!it.hasNext()) {
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

    @Override
    public void run() {
        BufferedImage currentFrame = robot.createScreenCapture(screenBounds);
        if(!imageChanged(currentFrame, previousFrame)) {
            return;
        }

        List<IndexedImage> changedImages = new ArrayList<>();

        if(previousFrame != null) {
            BufferedImage[][] split = splitImage(currentFrame, 12, 12);
            BufferedImage[][] previous = splitImage(previousFrame, 12, 12);
            for (int i = 0; i < 12; i++) {
                for (int j = 0; j < 12; j++) {
                    if (imageChanged(split[i][j], previous[i][j])) {
                        changedImages.add(new IndexedImage(i, j, split[i][j]));
                    }
                }
            }
        } else {
            BufferedImage[][] split = splitImage(currentFrame, 12, 12);
            for (int i = 0; i < 12; i++) {
                for (int j = 0; j < 12; j++) {
                    changedImages.add(new IndexedImage(i, j, split[i][j]));
                }
            }
        }



            for(IndexedImage indexedImage : changedImages) {
                try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                    int column = indexedImage.getColIndex();
                    int row = indexedImage.getRowIndex();
                    BufferedImage bufferedImage = indexedImage.getImage();

                    ImageOutputStream payload = ImageIO.createImageOutputStream(buffer);
                    writer.setOutput(payload);

                    try {
                        writer.write(null, new IIOImage(bufferedImage, null, null), param);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    payload.flush();
                    watchers.forEach(watcher -> watcher.receiveScreenshot(buffer.toByteArray(), column, row));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            previousFrame = currentFrame;
    }

    public static BufferedImage[][] splitImage(BufferedImage image, int rows, int cols) {
        int subImageWidth = image.getWidth() / cols;
        int subImageHeight = image.getHeight() / rows;
        BufferedImage[][] subImages = new BufferedImage[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int x = j * subImageWidth;
                int y = i * subImageHeight;
                subImages[i][j] = image.getSubimage(x, y, subImageWidth, subImageHeight);
            }
        }
        return subImages;
    }

    private boolean imageChanged(BufferedImage currentFrame, BufferedImage previousFrame) {
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
        scheduledFuture = EXECUTOR.scheduleAtFixedRate(this, 0, framesPerSecond, TimeUnit.MILLISECONDS);
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
