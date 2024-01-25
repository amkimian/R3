package com.staberinde.sscript.program;

import com.staberinde.sscript.annotation.SFunction;
import com.staberinde.sscript.exception.SSException;

import java.lang.annotation.Annotation;

public class CoreBlockDefinition extends BlockDefinition<CoreBlock> {
    public CoreBlockDefinition(final Class<? extends CoreBlock> clazz) {
        super(clazz, SFunction.class);
    }

    protected String getName(Annotation a) {
        return ((SFunction) a).value();
    }

    protected TypedProxyBuilder<CoreBlock> newProxyBuilder() {
        try {
            return new TypedProxyBuilder<>(CoreBlock.class, this.baseClass.getMethod("run", ProgramContext.class), 0);
        } catch(NoSuchMethodException e) {
            throw new SSException(e);
        }
    }
}
