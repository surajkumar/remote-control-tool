package io.github.surajkumar.screen;

import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class ScreenWatcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenWatcher.class);
    private final NetSocket socket;
    private final WatcherPermissions permissions;
    private final List<Buffer> queue;

    public ScreenWatcher(NetSocket socket,
                         WatcherPermissions permissions) {
        this.socket = socket;
        this.permissions = permissions;
        this.queue = new ArrayList<>();
    }



    public void receiveScreenshot(byte[] image, int row, int column, Grid grid) {
        if (permissions.canViewScreen()) {
            Buffer header = Buffer
                    .buffer()
                    .appendInt(grid.getRows())
                    .appendInt(grid.getColumns())
                    .appendInt(row)
                    .appendInt(column)
                    .appendInt(image.length);

            int packetSize =
                    header.length()
                            + image.length
                            + 4;

            LOGGER.debug("Queued screenshot [row={}][col={}][packetSize={}]", row, column, packetSize);

            queue.add(Buffer.buffer()
                    .appendInt(packetSize)
                    .appendBuffer(header)
                    .appendBytes(image));
        }
    }

    public void sendScreenshots() {
        int totalSize = 0;
        for(Buffer b : queue) {
            socket.write(b);
            totalSize += b.length();
        }
        LOGGER.debug("Sent {} frames, total bytes {}", queue.size(), totalSize);
        if(totalSize > 40000) {
            LOGGER.warn("Big frame!");
        }
        queue.clear();
    }

    public NetSocket socket() {
        return socket;
    }

    public WatcherPermissions permissions() {
        return permissions;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (ScreenWatcher) obj;
        return Objects.equals(this.socket, that.socket) &&
                Objects.equals(this.permissions, that.permissions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(socket, permissions);
    }

    @Override
    public String toString() {
        return "ScreenWatcher[" +
                "socket=" + socket + ", " +
                "permissions=" + permissions + ']';
    }

}
