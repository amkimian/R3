package com.staberinde.sscript.block;

import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import com.staberinde.sscript.value.BlockValueNull;

import java.util.ArrayList;
import java.util.List;

public class BlockStatementList implements CoreBlock {
    private final List<CoreBlock> blockList = new ArrayList<>();

    public void addBlock(CoreBlock block) {
        this.blockList.add(block);
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        BlockValue last = null;
        for(CoreBlock block : this.blockList) {
            last = block.run(context);
            if (last != null && last.isSpecial()) {
                return last;
            }
        }
        return last==null ? BlockValueNull.getInstance() : last;
    }
}
