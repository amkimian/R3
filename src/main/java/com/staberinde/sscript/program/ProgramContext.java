package com.staberinde.sscript.program;

import com.staberinde.sscript.exception.UndefinedVariableException;
import com.staberinde.sscript.value.BlockValue;

import java.util.List;
import java.util.Map;

public abstract class ProgramContext<T extends BlockValue> {
    protected Scope<T> currentScope;
    private T returnVal;

    public Map<String, Object> getScopeMap() {
        return currentScope.getVariableMap();
    }

    protected ProgramContext(final Scope<T> scope) {
        this.currentScope = scope;
        this.returnVal = null;
    }

    public void setReturnVal(final T val) {
        this.returnVal = val;
    }

    public T getReturnVal() {
        if (this.returnVal != null) {
            return this.returnVal;
        } else {
            return this.currentScope.wrapValue(null);
        }
    }

    public void defineVariable(final String variableName) {
        currentScope.defineVariable(variableName);
    }

    public boolean isDefined(final String variableName) {
        try {
            currentScope.getVariable(variableName);
            return true;
        } catch(final UndefinedVariableException e) {
            return false;
        }
    }

    public T getVariable(final String name) {
        return currentScope.getVariable(name);
    }

    public void setVariableWithIndex(final String varName, final List<? extends BlockValue> indices, final T value) {
        currentScope.setVariableWithIndex(varName, indices, value);
    }

    public void setVariable(final String name, final T value) {
        currentScope.setVariable(name, value);
    }

    public void pushScope() {
        this.currentScope = this.currentScope.pushScope(true);
    }

    public void popScope() {
        final Scope<T> newScope = this.currentScope.popScope();
        if (newScope != null) {
            this.currentScope = newScope;
        }
    }

    public void setLocalAndPushScope(final boolean setLocal) {
        this.currentScope.setLocalAndPushScope(setLocal);
    }
}
