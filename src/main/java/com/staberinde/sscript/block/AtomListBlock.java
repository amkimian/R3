package com.staberinde.sscript.block;

import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

public class AtomListBlock extends LocationAwareBlock implements CoreBlock {
    private final List<CoreBlock> blocks = new ArrayList<>();

    public AtomListBlock(Token loc) {
        super(loc);
    }

    public void addValue(CoreBlock block) {
        blocks.add(block);
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        List<BlockValue> retList = new ArrayList<>();
        for(CoreBlock block : blocks) {
            retList.add(block.run(context));
        }
        return BlockValue.from(retList);
    }

}
