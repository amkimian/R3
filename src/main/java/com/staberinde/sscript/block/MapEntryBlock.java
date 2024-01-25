package com.staberinde.sscript.block;
import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import com.staberinde.sscript.util.Pair;

import org.antlr.v4.runtime.Token;

public class MapEntryBlock extends LocationAwareBlock implements CoreBlock {
    private final String key;
    private final CoreBlock value;

    public MapEntryBlock(Token loc, String key, CoreBlock value) {
        super(loc);
        this.key = key;
        this.value = value;
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        Pair<String, BlockValue> ret = new Pair<>(this.key, this.value.run(context));
        return BlockValue.from(ret);
    }
}
