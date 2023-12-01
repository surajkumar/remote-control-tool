package io.github.surajkumar.host.screen;

import javax.swing.*;
import java.awt.*;

public class Visualizer {
    private final JFrame frame;

    public Visualizer(Dimension dimension) {
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setSize(dimension);
        frame.setUndecorated(true);
        frame.setBackground(new Color(0, 0, 0, 0));
        JPanel panel = new JPanel();
        panel.setBackground(new Color(0, 0, 0, 0));
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
