package com.staberinde.sscript.block;

import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import org.antlr.v4.runtime.Token;

public class ForBlock extends AbstractLoopBlock {
    private final CoreBlock startBlock;
    private final CoreBlock testBlock;
    private final CoreBlock updateBlock;

    public ForBlock(Token loc, CoreBlock startBlock, CoreBlock testBlock, CoreBlock updateBlock, CoreBlock bodyBlock) {
        super(loc, bodyBlock);
        this.startBlock = startBlock;
        this.testBlock = testBlock;
        this.updateBlock = updateBlock;
    }

    @Override
    protected void setUp(ProgramContext<BlockValue> ctx) {
        startBlock.run(ctx);
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
        updateBlock.run(ctx);
    }
}
