package io.github.surajkumar.server.actions.impl;

import io.github.surajkumar.server.screen.WatcherPermissions;
import io.github.surajkumar.server.actions.Action;
import io.vertx.core.buffer.Buffer;

import java.awt.*;

public class MouseReleaseAction implements Action {
    private final Robot robot;

    public MouseReleaseAction(Robot robot) {
        this.robot = robot;
    }

    @Override
    public Buffer handle(Buffer request, WatcherPermissions permissions) {
        if (permissions.canSendMouseMovements()) {
            robot.mouseRelease(request.getInt(0));
        }
        return null;
    }
}
