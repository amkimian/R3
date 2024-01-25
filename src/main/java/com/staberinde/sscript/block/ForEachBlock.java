package com.staberinde.sscript.block;

import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import org.antlr.v4.runtime.Token;

import java.util.Iterator;

public class ForEachBlock extends AbstractLoopBlock {
    private final String variableName;
    private final CoreBlock collectionBlock;

    private Iterator<BlockValue> collIter = null;
    private Iterator<String> collIterString = null;

    public ForEachBlock(Token loc, String variableName, CoreBlock collectionBlock, CoreBlock body) {
        super(loc, body);
        this.variableName = variableName;
        this.collectionBlock = collectionBlock;
    }

    @Override
    protected void setUp(ProgramContext<BlockValue> ctx) {
        BlockValue coll = collectionBlock.run(ctx);
        if (coll.isList()) {
            this.collIter = coll.asList().iterator();
        } else if (coll.isMap()) {
            this.collIterString = coll.asMap().keySet().iterator();
        } else if (coll.isSet()) {
            this.collIter = coll.asSet().iterator();
        }
    }

    @Override
    protected void prepare(ProgramContext<BlockValue> ctx) {
        if (this.collIter != null) {
            ctx.setVariable(this.variableName, collIter.next());
        } else {
            ctx.setVariable(this.variableName, BlockValue.from(collIterString.next()));
        }
    }

    @Override
    protected void update(ProgramContext<BlockValue> ctx) {

    }

    @Override
    protected boolean test(ProgramContext<BlockValue> ctx) {
        if (this.collIter != null) {
            return this.collIter.hasNext();
        } else {
            return this.collIterString.hasNext();
        }
    }
}
