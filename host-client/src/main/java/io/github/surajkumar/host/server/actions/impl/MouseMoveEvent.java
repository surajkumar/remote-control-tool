package io.github.surajkumar.host.server.actions.impl;

import io.github.surajkumar.host.screen.WatcherPermissions;
import io.github.surajkumar.host.server.actions.Action;
import io.vertx.core.buffer.Buffer;

import java.awt.*;

public class MouseMoveEvent implements Action {
    private final Robot robot;

    public MouseMoveEvent(Robot robot) {
        this.robot = robot;
    }

    @Override
    public Buffer handle(Buffer request, WatcherPermissions permissions) {
        if(permissions.canSendMouseMovements()) {
            int x = request.getInt(0);
            int y = request.getInt(4);
            robot.mouseMove(x, y);
        }
        return null;
    }
}
