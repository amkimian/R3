package com.staberinde.sscript.block;

import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import org.antlr.v4.runtime.Token;

public class LambdaDefBlock extends LocationAwareBlock implements CoreBlock {
    private final LambdaDef lambdaDef;

    public LambdaDefBlock(Token loc, LambdaDef lambdaDef) {
        super(loc);
        this.lambdaDef = lambdaDef;
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        return BlockValue.from(lambdaDef);
    }
}
