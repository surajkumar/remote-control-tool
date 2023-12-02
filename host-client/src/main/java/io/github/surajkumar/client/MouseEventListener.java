package io.github.surajkumar.client;

import io.github.surajkumar.host.server.actions.OperationCode;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.net.NetSocket;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.*;

public class MouseEventListener implements MouseListener, MouseMotionListener {
    private static final double HOST_WIDTH = 2560;
    private static final double HOST_HEIGHT = 1440;
    private final JPanel panel;
    private final NetSocket socket;

    public MouseEventListener(JPanel panel, NetSocket socket) {
        this.panel = panel;
        this.socket = socket;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        final int rightMouseButton = 3;
        final int rightMouseButtonRelease = 4;
        int button = e.getButton();
        if (button == rightMouseButton) {
            button = rightMouseButtonRelease;
        }
        Buffer buffer = Buffer.buffer();
        buffer.appendInt(OperationCode.MOUSE_PRESS.getOperationId());
        buffer.appendInt(button);
        socket.write(buffer);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        final int rightMouseButton = 3;
        final int rightMouseButtonRelease = 4;
        int button = e.getButton();
        if (button == rightMouseButton) {
            button = rightMouseButtonRelease;
        }
        Buffer buffer = Buffer.buffer();
        buffer.appendInt(OperationCode.MOUSE_RELEASE.getOperationId());
        buffer.appendInt(button);
        socket.write(buffer);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        double xScale = HOST_WIDTH / panel.getWidth();
        double yScale = HOST_HEIGHT / panel.getHeight();
        int mouseX = (int) (e.getX() * xScale);
        int mouseY = (int) (e.getY() * yScale);
        Buffer buffer = Buffer.buffer();
        buffer.appendInt(OperationCode.MOUSE_MOVE.getOperationId());
        buffer.appendInt(mouseX);
        buffer.appendInt(mouseY);
        socket.write(buffer);
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Not needed
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Not needed
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Not needed
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Not needed
    }
}
