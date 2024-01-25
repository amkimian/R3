package com.staberinde.sscript.block;

import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class LambdaCallBlock extends LocationAwareBlock implements CoreBlock {
    private final String varName;
    private final List<CoreBlock> params;

    public LambdaCallBlock(Token loc, String varName, List<CoreBlock> params) {
        super(loc);
        this.varName = varName;
        this.params = params;
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        final LambdaDef lambda = context.getVariable(varName).asLambda();
        return lambda.runLambda(context, params.stream().map(p->p.run(context)).toArray(BlockValue[]::new));
    }
}
