package com.staberinde.sscript.model;

import com.staberinde.sscript.program.CoreBlock;

public class NamedParameter {
    private final String name;
    private final CoreBlock expression;
    private final boolean hasName;

    public String getName() {
        return name;
    }

    public CoreBlock getExpression() {
        return expression;
    }

    public boolean hasName() {
        return hasName;
    }

    public NamedParameter(CoreBlock expression) {
        this.hasName = false;
        this.name = "";
        this.expression = expression;
    }

    public NamedParameter(String name, CoreBlock expression) {
        this.hasName = true;
        this.name = name;
        this.expression = expression;
    }
}
