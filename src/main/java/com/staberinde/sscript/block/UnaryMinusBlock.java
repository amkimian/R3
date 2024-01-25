package com.staberinde.sscript.block;

import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import org.antlr.v4.runtime.Token;

public class UnaryMinusBlock extends LocationAwareBlock implements CoreBlock {
    private static final BlockValue NEGATIVE_ONE = BlockValue.from(-1);

    private final CoreBlock expr;

    public UnaryMinusBlock(Token loc, CoreBlock expr) {
        super(loc);
        this.expr = expr;
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        BlockValue val = expr.run(context);
        return val.multiply(NEGATIVE_ONE);
    }
}
