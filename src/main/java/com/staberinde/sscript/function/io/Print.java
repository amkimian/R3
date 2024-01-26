package com.staberinde.sscript.function.io;

import com.staberinde.sscript.annotation.SFunction;
import com.staberinde.sscript.annotation.SParam;
import com.staberinde.sscript.block.LocationAwareBlock;
import com.staberinde.sscript.program.BaseBlock;
import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import org.antlr.v4.runtime.Token;

@SFunction("print")
public class Print extends LocationAwareBlock implements CoreBlock {
    @SParam(0)
    private BaseBlock toPrint;
    public Print(Token loc) {
        super(loc);
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        BlockValue value = toPrint.run(context);
        System.out.println(value.toString());
        return value;
    }
}
