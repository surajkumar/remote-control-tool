package io.github.surajkumar.host.server.actions.impl;

import io.github.surajkumar.host.screen.WatcherPermissions;
import io.github.surajkumar.host.server.actions.Action;
import io.vertx.core.buffer.Buffer;

import java.awt.*;

public class KeyReleaseAction implements Action {
    private final Robot robot;

    public KeyReleaseAction(Robot robot) {
        this.robot = robot;
    }

    @Override
    public Buffer handle(Buffer request, WatcherPermissions permissions) {
        if (permissions.canSendKeyboardInputs()) {
            robot.keyRelease(request.getInt(0));
        }
        return null;
    }
}
