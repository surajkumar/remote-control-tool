package io.github.surajkumar.client;

import io.github.surajkumar.host.server.actions.OperationCode;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyboardEventListener implements KeyListener {
    private final NetSocket socket;

    public KeyboardEventListener(NetSocket socket) {
        this.socket = socket;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        Buffer buffer = Buffer.buffer();
        buffer.appendInt(OperationCode.KEY_PRESS.getOperationId());
        buffer.appendInt(keyCode);
        socket.write(buffer);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
        Buffer buffer = Buffer.buffer();
        buffer.appendInt(OperationCode.KEY_RELEASE.getOperationId());
        buffer.appendInt(keyCode);
        socket.write(buffer);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        int keyCode = e.getKeyCode();
        Buffer buffer = Buffer.buffer();
        buffer.appendInt(OperationCode.KEY_PRESS.getOperationId());
        buffer.appendInt(keyCode);
        socket.write(buffer);
        // Not needed?
    }
}
