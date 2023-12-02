package io.github.surajkumar.host.server;

import io.github.surajkumar.host.screen.*;
import io.github.surajkumar.host.server.actions.Action;
import io.github.surajkumar.host.server.actions.ActionHandler;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public class HostServerHandler implements Handler<NetSocket> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HostServerHandler.class);

    private final PartionScreenRecorder recorder;

    public HostServerHandler(int monitor) {
        try {
            this.recorder = new PartionScreenRecorder(30, Monitor.getBoundsForMonitor(monitor));
        } catch (AWTException e) {
            LOGGER.error("Cannot create screen recorder: " + e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public void handle(NetSocket socket) {
        socket.closeHandler(
                handler -> {
                    recorder.removeWatcher(getWatcher(socket));
                    if (recorder.getWatchers().isEmpty()) {
                        LOGGER.info(
                                "No clients currently connected, stopping screen capture until a"
                                        + " new client connects");
                        recorder.stop();
                    }
                });
        socket.handler(
                buffer -> {
                    if (!containsSocket(socket)) {
                        HandshakeMessage handshakeMessage = decodeHandshake(buffer);
                        if (AuthenticationHandler.authenticate(handshakeMessage.password())) {
                            LOGGER.info(handshakeMessage.name() + " has connected successfully");
                            recorder.registerWatcher(
                                    new ScreenWatcher(socket, new WatcherPermissions()));
                            if (!recorder.isRunning()) {
                                LOGGER.info("Screen capture has started");
                                recorder.start();
                            }
                        } else {
                            LOGGER.warn(
                                    handshakeMessage.name() + " has entered an invalid password");
                        }
                    } else {
                        int operation = buffer.getInt(0);
                        Action action = ActionHandler.getActionFor(operation);
                        if (action != null) {
                            LOGGER.trace("Received " + action);
                            Buffer response =
                                    action.handle(
                                            buffer.getBuffer(4, buffer.length()),
                                            getWatcher(socket).getPermissions());
                            if (response != null) {
                                socket.write(response);
                            }
                        } else {
                            LOGGER.trace("Unknown action received: " + operation);
                        }
                    }
                });
    }

    private HandshakeMessage decodeHandshake(Buffer buffer) {
        int position = 0;
        int nameLen = buffer.getInt(position);
        position += 4;
        int passwordLen = buffer.getInt(position);
        position += 4;
        String name = buffer.getString(position, (position + nameLen));
        position += nameLen;
        String password = buffer.getString(position, (position + passwordLen));
        return new HandshakeMessage(name, password);
    }

    public boolean containsSocket(NetSocket socket) {
        for (ScreenWatcher watcher : recorder.getWatchers()) {
            if (watcher.getSocket() == socket) {
                return true;
            }
        }
        return false;
    }

    public ScreenWatcher getWatcher(NetSocket socket) {
        for (ScreenWatcher watcher : recorder.getWatchers()) {
            if (watcher.getSocket() == socket) {
                return watcher;
            }
        }
        return null;
    }
}
