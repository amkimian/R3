package com.staberinde.sscript.block;

import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import com.staberinde.sscript.value.BlockValueSpecial;
import org.antlr.v4.runtime.Token;

public class SetReturnBlock extends LocationAwareBlock implements CoreBlock {
    private final CoreBlock returnExp;

    public SetReturnBlock(Token loc, CoreBlock returnExp) {
        super(loc);
        this.returnExp = returnExp;
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        final BlockValue val = returnExp.run(context);
        context.setReturnVal(val);
        return BlockValueSpecial.RETURN;
    }
}
