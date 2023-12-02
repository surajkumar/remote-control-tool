package io.github.surajkumar.screen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenRecorder.class);
    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(1);
    private final ScreenRecorderConfiguration configuration =
            new ScreenRecorderConfiguration("TIFF", "LZW", 0.50F);
    private final List<ScreenWatcher> watchers;
    private final Robot robot;
    private final Rectangle screenBounds;
    private final int framesPerSecond;
    private final RecordingVisualizer visualizer;
    private boolean running;
    private ScheduledFuture<?> scheduledFuture;
    private ImageWriter writer;
    private ImageWriteParam param;
    private BufferedImage previousFrame;
    private final Grid grid;

    public ScreenRecorder(int framesPerSecond, Rectangle screenBounds) throws AWTException {
        setupWriter();
        this.framesPerSecond = 1000 / framesPerSecond;
        this.watchers = new ArrayList<>();
        this.robot = new Robot();
        this.screenBounds = screenBounds;
        screenBounds.setRect(0, 0, screenBounds.getWidth() - 2, screenBounds.getHeight() - 2);
        this.running = false;
        this.visualizer =
                new RecordingVisualizer(new Dimension(Monitor.getBoundsForMonitor(0).getSize()));
        this.grid = new Grid();
    }

    private void setupWriter() {
        Arrays.stream(ImageIO.getWriterFormatNames()).toList().forEach(System.out::println);

        Iterator<ImageWriter> it = ImageIO.getImageWritersByFormatName(configuration.imageFormat());
        if (!it.hasNext()) {
            throw new UnsupportedOperationException(
                    configuration.imageFormat() + " format not supported.");
        }
        writer = it.next();
        param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        if (param.canWriteCompressed()) {
            param.setCompressionType(configuration.compressionType());
            param.setCompressionQuality(configuration.compressionQuality());
        }
    }

    @Override
    public void run() {
        BufferedImage currentFrame = robot.createScreenCapture(screenBounds);
        if (!ImageUtils.isImageDifferent(currentFrame, previousFrame)) {
            return;
        }

        List<IndexedImage> changedImages = new ArrayList<>();
        BufferedImage[][] split =
                ImageUtils.splitImage(currentFrame, grid.getRows(), grid.getColumns());
        if (previousFrame == null) {
            for (int i = 0; i < grid.getRows(); i++) {
                for (int j = 0; j < grid.getColumns(); j++) {
                    changedImages.add(new IndexedImage(split[i][j], i, j));
                }
            }
        } else {
            BufferedImage[][] previous =
                    ImageUtils.splitImage(previousFrame, grid.getRows(), grid.getColumns());
            for (int i = 0; i < grid.getRows(); i++) {
                for (int j = 0; j < grid.getColumns(); j++) {
                    if (ImageUtils.isImageDifferent(split[i][j], previous[i][j])) {
                        changedImages.add(new IndexedImage(split[i][j], i, j));
                    }
                }
            }
        }

        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            for (IndexedImage indexedImage : changedImages) {
                BufferedImage bufferedImage = indexedImage.image();
                try (ImageOutputStream payload = ImageIO.createImageOutputStream(buffer)) {
                    writer.setOutput(payload);
                    writer.write(null, new IIOImage(bufferedImage, null, null), param);
                    payload.flush();
                    watchers.forEach(
                            watcher ->
                                    watcher.receiveScreenshot(
                                            buffer.toByteArray(),
                                            indexedImage.row(),
                                            indexedImage.column(),
                                            grid));
                    buffer.reset();
                }
            }
        } catch (IOException e) {
            LOGGER.error("Error while processing frame: {}", e.getMessage(), e);
        } finally {
            watchers.parallelStream().forEach(ScreenWatcher::sendScreenshots);
        }

        previousFrame = currentFrame;
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
