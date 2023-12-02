package io.github.surajkumar.server.actions.impl;

import io.github.surajkumar.screen.WatcherPermissions;
import io.github.surajkumar.server.actions.Action;
import io.vertx.core.buffer.Buffer;

public class DoNothingAction implements Action {
    @Override
    public Buffer handle(Buffer request, WatcherPermissions permissions) {
        return null;
    }
}
