package io.github.surajkumar.host.screen;

import com.sun.jdi.ThreadReference;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

public class ScreenWatcher {
    private final NetSocket socket;
    private final WatcherPermissions permissions;

    public ScreenWatcher(NetSocket socket, WatcherPermissions permissions) {
        this.socket = socket;
        this.permissions = permissions;
    }

    public void receiveScreenshot(byte[] image) {
        if(permissions.canViewScreen()) {
            socket.write(Buffer
                    .buffer()
                    .appendInt(image.length)
                    .appendBytes(image));
        }
    }

    public void receiveScreenshot(byte[] image, int column, int row) {
        if(permissions.canViewScreen()) {
            System.out.println("Sending screenshot " + column + "," + row + " packet-len:" + (16 + image.length));
            socket.write(Buffer
                    .buffer()
                    .appendInt(16 + image.length)
                    .appendInt(column)
                    .appendInt(row)
                    .appendInt(image.length)
                    .appendBytes(image));
        }
    }


    public NetSocket getSocket() {
        return socket;
    }

    public WatcherPermissions getPermissions() {
        return permissions;
    }
}
