package com.staberinde.sscript.block;
import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.value.BlockValue;
import com.staberinde.sscript.program.ProgramContext;

import com.staberinde.sscript.value.BlockValueNull;
import com.staberinde.sscript.value.SpecialValue;
import org.antlr.v4.runtime.Token;

public abstract class AbstractLoopBlock extends LocationAwareBlock implements CoreBlock {

    private final CoreBlock loopBody;

    protected AbstractLoopBlock(final Token loc, CoreBlock loopBody) {
        super(loc);
        this.loopBody = loopBody;
    }

    protected abstract void setUp(final ProgramContext<BlockValue> ctx);
    protected abstract void prepare(final ProgramContext<BlockValue> ctx);
    protected abstract boolean test(final ProgramContext<BlockValue> ctx);
    protected abstract void update(final ProgramContext<BlockValue> ctx);

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        try {
            context.setLocalAndPushScope(false);
            this.setUp(context);

            while(this.test(context)) {
                this.prepare(context);
                final BlockValue loopRet = this.loopBody.run(context);
                if (loopRet != null && loopRet.isSpecial()) {
                    if (loopRet.asSpecial().equals(SpecialValue.RETURN)) {
                        return loopRet;
                    } else {
                        break;
                    }
                }

                this.update(context);
            }
        } finally {
            context.popScope();
        }
        return BlockValueNull.getInstance();
    }
}
