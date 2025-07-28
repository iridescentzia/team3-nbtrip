package org.scoula.settlement.exception;

public class SettlementException extends RuntimeException {
    private final SettlementErrorCode errorCode;
    private final Object[] params;

    public SettlementException(SettlementErrorCode errorCode, Object... params) {
        super(String.format(errorCode.getMessageTemplate(), params));
        this.errorCode = errorCode;
        this.params = params;
    }

    public SettlementErrorCode getErrorCode() {
        return errorCode;
    }

    public Object[] getParams() {
        return params;
    }
}

