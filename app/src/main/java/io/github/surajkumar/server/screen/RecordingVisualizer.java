package io.github.surajkumar.server.screen;

import java.awt.*;

import javax.swing.*;

public class RecordingVisualizer {
    private static final Color TRANSPARENT = new Color(0, 0, 0, 0);
    private final JFrame frame;

    public RecordingVisualizer(Dimension dimension, int x, int y) {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setSize(dimension);
        frame.setUndecorated(true);
        frame.setBackground(TRANSPARENT);
        frame.setLocation(x, y);
        JPanel panel = new JPanel();
        panel.setBackground(TRANSPARENT);
        panel.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 1));
        frame.add(panel);
    }

    public void show() {
        frame.setVisible(true);
    }

    public void hide() {
        frame.setVisible(false);
    }
}
