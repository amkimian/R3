package com.staberinde.sscript.exception;

public class SSParseException extends RuntimeException {
    private static final String DEFAULT_FMT = "Parse error at line %d, column %d: %s";

    public final String message;
    public final int lineNum;
    public final int charPos;

    public SSParseException(String message) {
        super("Parser error " + message);
        this.message = message;
        this.lineNum = 0;
        this.charPos = 0;
    }
    public SSParseException(String message, int lineNum, int charPos) {
        super(String.format(DEFAULT_FMT, lineNum, charPos, message));
        this.message = message;
        this.lineNum = lineNum;
        this.charPos = charPos;
    }
}
