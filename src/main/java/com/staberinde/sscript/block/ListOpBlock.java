package com.staberinde.sscript.block;

import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import com.staberinde.sscript.value.ValueType;
import org.antlr.v4.runtime.Token;

import java.util.function.BinaryOperator;

public class ListOpBlock extends OperationBlock {
    public ListOpBlock(final Token loc, final CoreBlock left, final CoreBlock right, final BinaryOperator<BlockValue> listOp) {
        super(loc, left, right, listOp);
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        final BlockValue l = left.run(context);
        final BlockValue r = right.run(context);
        if (l.getType().equals(ValueType.LIST)) {
            return l.doWithEach(el -> operator.apply(el, r));
        } else if (r.getType().equals(ValueType.LIST)) {
            return r.doWithEach(el -> operator.apply(l, el));
        } else {
            throw getRuntimeException("Cannot apply list operation to non-list values");
        }
    }
}
