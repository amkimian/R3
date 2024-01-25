package com.staberinde.sscript.block;
import com.staberinde.sscript.exception.SSException;
import com.staberinde.sscript.program.PureBlock;
import org.antlr.v4.runtime.Token;

public class LocationAwareBlock implements PureBlock {
    private final Token location;

    protected LocationAwareBlock(Token location) {
        this.location = location;
    }

    protected int getLine() {
        if (location != null) {
            return location.getLine();
        } else {
            return -1;
        }
    }

    protected int getColumn() {
        if (location != null) {
            return location.getCharPositionInLine();
        } else {
            return -1;
        }
    }

    protected SSException getRuntimeException(String message) {
        return new SSException(getLocationAsString() + " - " + message);
    }

    protected SSException getRuntimeException(Exception e) {
        return new SSException(getLocationAsString() + " - " + e.getMessage(), e);
    }

    protected Token getLocation() {
        return location;
    }

    protected String getLocationAsString() {
        if (location != null) {
            return "Line " + location.getLine() + ", Column " + location.getCharPositionInLine();
        } else {
            return "Unknown location";
        }
    }
}
