package com.staberinde.sscript.block;

import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import com.staberinde.sscript.value.SpecialValue;

import java.util.List;

public class LambdaDef {
    public BlockStatementList getBlock() {
        return block;
    }

    private final List<String> varNames;
    private final BlockStatementList block;

    public LambdaDef(List<String> varNames, BlockStatementList block) {
        this.varNames = varNames;
        this.block = block;
    }

    public BlockValue runLambda(final ProgramContext<BlockValue> ctx, final BlockValue... args) {
        final BlockValue prevRet = ctx.getReturnVal();
        try {
            ctx.pushScope();
            int varPoint = 0;
            for(String varName : varNames) {
                if (args.length > varPoint) {
                    ctx.setVariable(varName, args[varPoint]);
                }
                varPoint++;
            }
            return this.executeBlock(ctx);
        } finally {
            ctx.popScope();
            ctx.setReturnVal(prevRet);
        }
    }

    public BlockValue executeBlock(final ProgramContext<BlockValue> ctx) {
        final BlockValue lambdaRet = this.block.run(ctx);
        if (lambdaRet.isSpecial() && lambdaRet.asSpecial().equals(SpecialValue.RETURN)) {
            return ctx.getReturnVal();
        } else {
            return lambdaRet;
        }
    }
}
