package com.staberinde.sscript.block;

import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import org.antlr.v4.runtime.Token;

public class FilterBlock extends LocationAwareBlock implements CoreBlock {
    private final CoreBlock target;
    private final CoreBlock filter;

    public FilterBlock(Token loc, CoreBlock target, CoreBlock filter) {
        super(loc);
        this.target = target;
        this.filter = filter;
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        final LambdaDef f = filter.run(context).asLambda();
        final BlockValue target = this.target.run(context);
        return BlockValue.from(target.asList().stream().filter(v->f.runLambda(context, v).asBoolean()));
    }
}
