package com.staberinde.sscript.block;

import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import org.antlr.v4.runtime.Token;

import java.util.function.BinaryOperator;

public class OperationBlock extends LocationAwareBlock implements CoreBlock {
    protected final CoreBlock left;
    protected final CoreBlock right;

    protected final BinaryOperator<BlockValue> operator;

    public OperationBlock(Token loc, CoreBlock left, CoreBlock right, BinaryOperator<BlockValue> operator) {
        super(loc);
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        return operator.apply(left.run(context), right.run(context));
    }
}
