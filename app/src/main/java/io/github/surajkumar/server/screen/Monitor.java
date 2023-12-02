package io.github.surajkumar.server.screen;

import java.awt.*;

public class Monitor {

    private Monitor() {}

    public static Rectangle getBoundsForMonitor(int monitor) {
        return getMonitors()[monitor].getDefaultConfiguration().getBounds();
    }

    public static GraphicsDevice[] getMonitors() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
    }

    public static int getNumberOfMonitors() {
        return getMonitors().length;
    }
}
