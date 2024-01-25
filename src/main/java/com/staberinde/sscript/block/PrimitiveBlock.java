package com.staberinde.sscript.block;

import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import com.staberinde.sscript.value.BlockValueNull;
import org.antlr.v4.runtime.Token;


public class PrimitiveBlock extends LocationAwareBlock implements CoreBlock {
    public enum PrimitiveType {
     NULL,
        BOOL,
        INTEGER,
        FLOAT,
        DOUBLE,
        LONG,
        STRING
    }

    private final BlockValue atom;

    public PrimitiveBlock(Token loc, PrimitiveType type, String toParse) {
        super(loc);
        switch(type) {
            case NULL -> atom = BlockValueNull.getInstance();
            case BOOL -> atom = BlockValue.from(Boolean.parseBoolean(toParse));
            case INTEGER -> atom = BlockValue.from(Integer.parseInt(toParse));
            case FLOAT -> atom = BlockValue.from(Float.parseFloat(toParse));
            case DOUBLE -> atom = BlockValue.from(Double.parseDouble(toParse));
            case LONG -> atom = BlockValue.from(Long.parseLong(toParse));
            case STRING -> atom = BlockValue.from(toParse);
            default -> atom = BlockValue.from(toParse);
        }
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        return atom;
    }
}
