package com.staberinde.sscript.block;

import com.staberinde.sscript.exception.SSException;
import com.staberinde.sscript.model.NamedParameter;
import com.staberinde.sscript.model.ProcDef;
import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import com.staberinde.sscript.value.SpecialValue;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class CallProcBlock extends LocationAwareBlock implements CoreBlock {
    private final String funcName;
    private final List<NamedParameter> params;
    private final ProcDef procDef;

    public CallProcBlock(Token loc, String funcName, List<NamedParameter> params, ProcDef procDef) {
        super(loc);
        this.funcName = funcName;
        this.params = params;
        this.procDef = procDef;
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        final List<String> requiredParams = procDef.getRequiredParams();
        if (params.size() < requiredParams.size()) {
            throw new SSException("Not enough parameters for function " + funcName);
        }

        final BlockValue prevRet = context.getReturnVal();
        try {
            context.pushScope();
            for(int i=0; i< params.size(); i++) {
                BlockValue p = params.get(i).getExpression().run(context);
                context.setVariable(requiredParams.get(i), p);
            }
            final BlockValue procRet = procDef.getBlock().run(context);
            if (procRet.isSpecial() && procRet.asSpecial().equals(SpecialValue.RETURN)) {
                return context.getReturnVal();
            } else {
                return procRet;
            }
        } finally {
            context.popScope();
            context.setReturnVal(prevRet);
        }
    }
}
