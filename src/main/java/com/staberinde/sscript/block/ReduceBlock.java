package com.staberinde.sscript.block;

import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import org.antlr.v4.runtime.Token;

public class ReduceBlock extends LocationAwareBlock implements CoreBlock {
    private final CoreBlock target;
    private final CoreBlock accumLambda;
    private final CoreBlock initialValue;

    public ReduceBlock(Token loc, CoreBlock target, CoreBlock initialValue, CoreBlock accumLambda) {
        super(loc);
        this.target = target;
        this.accumLambda = accumLambda;
        this.initialValue = initialValue;
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        final BlockValue initial = initialValue.run(context);
        final LambdaDef accum = accumLambda.run(context).asLambda();
        return BlockValue.from(target.run(context).asList()
                .stream().reduce(initial, (a, b) -> accum.runLambda(context, a, b)));
    }
}
