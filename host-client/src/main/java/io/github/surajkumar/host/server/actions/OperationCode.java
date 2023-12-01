package io.github.surajkumar.host.server.actions;

public enum OperationCode {
      MOUSE_MOVE(0)
    , MOUSE_PRESS(1)
    , MOUSE_RELEASE(2)
    , KEY_PRESS(3)
    , KEY_RELEASE(4)
    ,CTRL_ALT_DELETE(5)
    ;

    private final int operationId;

    OperationCode(int operationId) {
        this.operationId = operationId;
    }

    public static OperationCode of(int operationId) {
        for(OperationCode operationCode : OperationCode.values()) {
            if(operationCode.getOperationId() == operationId) {
                return operationCode;
            }
        }
        return null;
    }

    public int getOperationId() {
        return operationId;
    }
}
