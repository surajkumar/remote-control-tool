package io.github.surajkumar.server.actions.impl;

import io.github.surajkumar.server.screen.WatcherPermissions;
import io.github.surajkumar.server.actions.Action;
import io.vertx.core.buffer.Buffer;

import java.awt.*;

public class MousePressAction implements Action {
    private final Robot robot;

    public MousePressAction(Robot robot) {
        this.robot = robot;
    }

    @Override
    public Buffer handle(Buffer request, WatcherPermissions permissions) {
        if (permissions.canSendMouseMovements()) {
            robot.mousePress(request.getInt(0));
        }
        return null;
    }
}
