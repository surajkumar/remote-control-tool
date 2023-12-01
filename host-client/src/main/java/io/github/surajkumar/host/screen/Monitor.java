package io.github.surajkumar.host.screen;

import java.awt.*;

public class Monitor {

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
