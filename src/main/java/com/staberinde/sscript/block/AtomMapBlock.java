package com.staberinde.sscript.block;

import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.util.Pair;
import com.staberinde.sscript.value.BlockValue;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AtomMapBlock extends LocationAwareBlock implements CoreBlock {
    private final List<MapEntryBlock> entries = new ArrayList<>();

    public AtomMapBlock(Token loc) {
        super(loc);
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        Map<String, BlockValue> ret = new HashMap<>();
        for(MapEntryBlock entry : entries) {
            BlockValue e = entry.run(context);
            Pair<String, BlockValue> res = e.asPair();
            ret.put(res.getFirstValue(), res.getSecondValue());
        }
        return BlockValue.from(ret);
    }

    public void addEntry(MapEntryBlock entry) {
        entries.add(entry);
    }
}
