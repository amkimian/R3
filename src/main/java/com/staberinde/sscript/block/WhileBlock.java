package com.staberinde.sscript.block;

import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import org.antlr.v4.runtime.Token;

public class WhileBlock extends AbstractLoopBlock {
    private final CoreBlock testBlock;

    public WhileBlock(Token loc, CoreBlock testBlock, CoreBlock bodyBlock) {
        super(loc, bodyBlock);
        this.testBlock = testBlock;
    }

    @Override
    protected void setUp(ProgramContext<BlockValue> ctx) {

    }

    @Override
    protected void prepare(ProgramContext<BlockValue> ctx) {

    }

    @Override
    protected boolean test(ProgramContext<BlockValue> ctx) {
        return testBlock.run(ctx).asBoolean();
    }

    @Override
    protected void update(ProgramContext<BlockValue> ctx) {

    }
}
