package io.github.surajkumar.server.actions.impl;

import io.github.surajkumar.server.screen.WatcherPermissions;
import io.github.surajkumar.server.actions.Action;
import io.vertx.core.buffer.Buffer;

import java.awt.*;
import java.awt.event.KeyEvent;

public class CtrlAltDelAction implements Action {
    private final Robot robot;

    public CtrlAltDelAction(Robot robot) {
        this.robot = robot;
    }

    @Override
    public Buffer handle(Buffer request, WatcherPermissions permissions) {
        if (permissions.canSendKeyboardInputs()) {
            int ctrl = KeyEvent.VK_CONTROL;
            int alt = KeyEvent.VK_ALT;
            int del = KeyEvent.VK_DELETE;

            robot.keyPress(ctrl);
            robot.keyPress(alt);
            robot.keyPress(del);

            robot.keyRelease(ctrl);
            robot.keyRelease(alt);
            robot.keyRelease(del);
        }
        return null;
    }
}
