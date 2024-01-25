package com.staberinde.sscript.block;

import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;

public class FilterSetBlock extends LocationAwareBlock implements CoreBlock {
    private final VariablesGet source;
    private final CoreBlock filter;
    private final CoreBlock set;

    public FilterSetBlock(Token loc, VariablesGet source, CoreBlock filter, CoreBlock set) {
        super(loc);
        this.source = source;
        this.filter = filter;
        this.set = set;
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        final BlockValue sourceVal = this.source.run(context);
        final LambdaDef f = filter.run(context).asLambda();
        final BlockValue setter = set.run(context);
        filterSetList(f, setter, sourceVal.asInternalList(), context);
        return sourceVal;
    }

    private void filterSetList(LambdaDef f, BlockValue setter, List<BlockValue> list, ProgramContext<BlockValue> context) {
        final List<Integer> targetIndices = new ArrayList<>();
        for(int i=0; i< list.size(); i++) {
            if (f.runLambda(context, list.get(i)).asBoolean()) {
                targetIndices.add(i);
            }
        }
        targetIndices.forEach(i -> {
            final BlockValue newVal;
            if (setter.isLambda()) {
                newVal = setter.asLambda().runLambda(context, list.get(i));
            } else {
                newVal = setter;
            }
            list.set(i, newVal);
        });
    }
}
