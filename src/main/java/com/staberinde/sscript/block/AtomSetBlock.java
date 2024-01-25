package com.staberinde.sscript.block;

import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AtomSetBlock extends LocationAwareBlock implements CoreBlock {
    private final List<CoreBlock> blocks = new ArrayList<>();

    public AtomSetBlock(Token loc) {
        super(loc);
    }

    public void addEntry(CoreBlock block) {
        blocks.add(block);
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        Set<BlockValue> retList = new HashSet<>();
        for(CoreBlock block : blocks) {
            retList.add(block.run(context));
        }
        return BlockValue.from(retList);
    }
}
