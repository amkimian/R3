package com.staberinde.sscript.block;

import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import org.antlr.v4.runtime.Token;

public class UnaryNegBlock extends LocationAwareBlock implements CoreBlock {
    private final boolean notEach;

    private final CoreBlock expr;

    public UnaryNegBlock(Token loc, CoreBlock expr, boolean notEach) {
        super(loc);
        this.expr = expr;
        this.notEach = notEach;
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        BlockValue val = expr.run(context);
        if (notEach) {
            return BlockValue.from(val.doWithEach(v-> BlockValue.from(!v.asBoolean())));
        } else {
            return BlockValue.from(!val.asBoolean());
        }
    }
}
