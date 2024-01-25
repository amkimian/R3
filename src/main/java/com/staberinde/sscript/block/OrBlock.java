package com.staberinde.sscript.block;

import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import org.antlr.v4.runtime.Token;

import java.util.function.BinaryOperator;

public class OrBlock extends OperationBlock {
    public OrBlock(final Token loc, final CoreBlock left, final CoreBlock right, final BinaryOperator<BlockValue> operation) {
        super(loc, left, right, operation);
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        final BlockValue first = left.run(context);
        if (first.asBoolean()) {
            return first;
        } else {
            return operator.apply(first, right.run(context));
        }
    }
}
