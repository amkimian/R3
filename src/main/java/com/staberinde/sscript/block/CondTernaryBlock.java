package com.staberinde.sscript.block;

import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import org.antlr.v4.runtime.Token;

public class CondTernaryBlock extends LocationAwareBlock implements CoreBlock {
    private final CoreBlock orExpr;
    private final CoreBlock ifTrue;
    private final CoreBlock ifFalse;

    public CondTernaryBlock(Token loc, CoreBlock orExpr, CoreBlock ifTrue, CoreBlock ifFalse) {
        super(loc);
        this.orExpr = orExpr;
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
       BlockValue orExprValue = orExpr.run(context);
            if(orExprValue.asBoolean()) {
                return ifTrue.run(context);
            } else {
                return ifFalse.run(context);
            }
    }
}
