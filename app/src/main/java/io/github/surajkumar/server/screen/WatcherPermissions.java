package io.github.surajkumar.server.screen;

public class WatcherPermissions {
    private boolean downloadFiles;
    private boolean sendKeyboardInputs;
    private boolean sendMouseMovements;
    private boolean viewScreen;

    public WatcherPermissions() {
        downloadFiles = false;
        sendKeyboardInputs = false;
        sendMouseMovements = false;
        viewScreen = true;
    }

    public boolean isDownloadFiles() {
        return downloadFiles;
    }

    public void setDownloadFiles(boolean downloadFiles) {
        this.downloadFiles = downloadFiles;
    }

    public boolean canSendKeyboardInputs() {
        return sendKeyboardInputs;
    }

    public void setSendKeyboardInputs(boolean sendKeyboardInputs) {
        this.sendKeyboardInputs = sendKeyboardInputs;
    }

    public boolean canSendMouseMovements() {
        return sendMouseMovements;
    }

    public void setSendMouseMovements(boolean sendMouseMovements) {
        this.sendMouseMovements = sendMouseMovements;
    }

    public boolean canViewScreen() {
        return viewScreen;
    }

    public void setViewScreen(boolean viewScreen) {
        this.viewScreen = viewScreen;
    }
}
