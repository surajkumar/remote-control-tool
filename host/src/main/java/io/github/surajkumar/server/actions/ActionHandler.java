package io.github.surajkumar.server.actions;

import io.github.surajkumar.server.actions.impl.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ActionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ActionHandler.class);
    private static final Map<OperationCode, Action> ACTIONS = new HashMap<>();
    private static final DoNothingAction DO_NOTHING_ACTION = new DoNothingAction();

    static {
        Robot robot = null;
        try {
            robot = new Robot();
        } catch (Exception e) {
            LOGGER.error(
                    "Unable to create Robot so not external input will work: " + e.getMessage());
        }

        if (robot != null) {
            ACTIONS.put(OperationCode.MOUSE_MOVE, DO_NOTHING_ACTION);
            ACTIONS.put(OperationCode.MOUSE_PRESS, new MousePressAction(robot));
            ACTIONS.put(OperationCode.MOUSE_RELEASE, new MouseReleaseAction(robot));
            ACTIONS.put(OperationCode.KEY_PRESS, new KeyPressAction(robot));
            ACTIONS.put(OperationCode.KEY_RELEASE, new KeyReleaseAction(robot));
            ACTIONS.put(OperationCode.CTRL_ALT_DELETE, new CtrlAltDelAction(robot));
        }
    }

    public static Action getActionFor(int id) {
        return ACTIONS.get(OperationCode.of(id));
    }
}
