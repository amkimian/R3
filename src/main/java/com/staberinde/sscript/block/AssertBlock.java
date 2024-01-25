package com.staberinde.sscript.block;

import com.staberinde.sscript.SScriptParser;
import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import com.staberinde.sscript.value.BlockValueNull;
import org.antlr.v4.runtime.Token;

public class AssertBlock extends LocationAwareBlock implements CoreBlock {
    private final CoreBlock testBlock;
    private final CoreBlock messageBlock;
    private final boolean truth;
    private final boolean exceptionExpected;

    public AssertBlock(Token loc, Token ty, CoreBlock testBlock, CoreBlock messageBlock) {
        super(loc);
        this.testBlock = testBlock;
        this.messageBlock = messageBlock;
        this.truth = ty.getType() == SScriptParser.ASSERT || ty.getType() == SScriptParser.ASSERTTRUE;
        this.exceptionExpected = ty.getType() == SScriptParser.ASSERTEXCEPTION;
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        if (this.exceptionExpected) {
            final BlockValue ret;
            try {
                ret = this.testBlock.run(context);
            } catch(Exception e) {
                return BlockValueNull.getInstance();
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("Expected exception, but got " + ret.convertToString());
            if (this.messageBlock != null) {
                sb.append(String.format(" (%s) ",this.messageBlock.run(context).convertToString()));
            }
            throw getRuntimeException(sb.toString());
        } else {
            boolean test = testBlock.run(context).asBoolean();
            if (test && truth || (!test && !truth)) {
                return BlockValueNull.getInstance();
            } else {
                final String message = messageBlock == null ? "Asset failed" : messageBlock.run(context).convertToString();
                throw getRuntimeException(message);
            }
        }
    }
}
