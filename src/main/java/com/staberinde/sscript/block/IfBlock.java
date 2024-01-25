package com.staberinde.sscript.block;

import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import com.staberinde.sscript.value.BlockValueNull;
import org.antlr.v4.runtime.Token;

public class IfBlock extends LocationAwareBlock implements CoreBlock {
    private final CoreBlock testBlock;
    private final CoreBlock trueBlock;
    private final CoreBlock elseBlock;

    public IfBlock(Token loc, CoreBlock testBlock, CoreBlock trueBlock, CoreBlock elseBlock) {
        super(loc);
        this.testBlock = testBlock;
        this.trueBlock = trueBlock;
        this.elseBlock = elseBlock;
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        BlockValue testVal = testBlock.run(context);
        BlockValue ret = BlockValueNull.getInstance();
        if (testVal.asBoolean()) {
            context.setLocalAndPushScope(false);
            ret = trueBlock.run(context);
            context.popScope();
        } else if (elseBlock != null) {
            context.setLocalAndPushScope(false);
            ret = elseBlock.run(context);
            context.popScope();
        }
        return ret;
    }
}
