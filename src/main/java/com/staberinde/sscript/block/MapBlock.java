package com.staberinde.sscript.block;

import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import org.antlr.v4.runtime.Token;

public class MapBlock extends LocationAwareBlock implements CoreBlock {
    private final CoreBlock target;
    private final CoreBlock mapperLambda;

    public MapBlock(Token loc, CoreBlock target, CoreBlock mapperLambda) {
        super(loc);
        this.target = target;
        this.mapperLambda = mapperLambda;
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        final LambdaDef mapper = mapperLambda.run(context).asLambda();
        return BlockValue.from(target.run(context).asList()
                .stream().map(v->mapper.runLambda(context, v)));
    }
}
