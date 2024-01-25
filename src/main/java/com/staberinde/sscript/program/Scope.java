package com.staberinde.sscript.program;

import com.staberinde.sscript.exception.SSException;
import com.staberinde.sscript.exception.UndefinedVariableException;
import com.staberinde.sscript.util.Pair;
import com.staberinde.sscript.util.RawValueConverter;
import com.staberinde.sscript.value.BlockValue;
import com.staberinde.sscript.value.BlockValueList;
import com.staberinde.sscript.value.BlockValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Scope<T extends BlockValue> {
    private final Map<String, T> variables = new HashMap<>();

    private Scope<T> parentScope = null;
    private boolean setIsLocal = true;

    public Map<String, Object> getVariableMap() {
        Map<String, Object> ret = new HashMap<>();
        for(var e : variables.entrySet()) {
            if (e.getValue() instanceof BlockValueMap) {
                ret.put(e.getKey(), RawValueConverter.convertMap(e.getValue().asMap()));
            } else if (e.getValue() instanceof BlockValueList) {
                ret.put(e.getKey(), RawValueConverter.convertList(e.getValue().asList()));
            } else {
                ret.put(e.getKey(), e.getValue().asObj());
            }
        }
        if (parentScope != null) {
            ret.putAll(parentScope.getVariableMap());
        }
        return ret;
    }

    public void defineVariable(String variableName) {
        if (!variables.containsKey(variableName)) {
            variables.put(variableName, this.wrapValue(null));
        }
    }

    public void setVariableByName(String name, T argValue) {
        if (setIsLocal || parentScope == null || variables.containsKey(name)) {
            variables.put(name, argValue);
        } else {
            this.parentScope.setVariableByName(name, argValue);
        }
    }

    public void setLocalVariable(String name, T argValue) {
        this.variables.put(name, argValue);
    }

    public void setVariable(String name, T value) {
        if (name.indexOf('.') == -1) {
            setVariableByName(name, value);
        } else {
            doDottedSetVariableName(name, value);
        }
    }

    private void doDottedSetVariableName(String name, T value) {
        Pair<BlockValue, String> variable = getValue(name, true);
        Map<String, BlockValue> asMap = variable.getFirstValue().asMap();
        asMap.put(variable.getSecondValue(), value);
    }

    private boolean variableExists(String name) {
        if (!variables.containsKey(name)) {
            if (parentScope != null) {
                return parentScope.variableExists(name);
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public void setVariableWithIndex(String varName, List<? extends BlockValue> indices, BlockValue toSet) {
        final BlockValue rootVal = getValue(varName, false).getFirstValue();
        doIndexSet(rootVal, indices, toSet);
    }

    private void doIndexSet(final BlockValue rootVal, final List<? extends BlockValue> indices, BlockValue toSet) {
        if (rootVal.isMap()) {
            final Map<String, BlockValue> rootMap = rootVal.asMap();
            final BlockValue keyVal = indices.remove(0);
            if (!keyVal.isString()) {
                throw new SSException("Cannot access numeric index of map");
            }
            final String key = keyVal.asString();
            if (indices.isEmpty()) {
                rootMap.put(key, toSet);
            } else {
                doIndexSet(rootMap.get(key), indices, toSet);
            }
        } else {
            List<BlockValue> rootList = rootVal.asInternalList();
            final BlockValue idVal = indices.remove(0);
            final int idx = resolveIndex(rootVal, idVal);
            try {
                if (indices.isEmpty()) {
                    rootList.set(idx, toSet);
                } else {
                    doIndexSet(rootList.get(idx), indices, toSet);
                }
            } catch(final IndexOutOfBoundsException e) {
                throw new SSException(e);
            }
        }
    }

    private int resolveIndex(final BlockValue value, final BlockValue indexValue) {
        if (indexValue.isString()) {
            throw new SSException("Cannot access string index");
        } else {
            return indexValue.asInteger();
        }
    }

    private void ensureVariableExists(String variableName) {
        if (!variableExists(variableName)) {
            variables.put(variableName, this.wrapValue(new HashMap<String, T>()));
        }
    }

    private Pair<BlockValue, String> getValue(String name, boolean withLast) {
        if (name.indexOf('.') == -1) {
            return new Pair<>(getVariable(name), null);
        } else {
            return getDottedValue(name, withLast);
        }
    }

    private Pair<BlockValue, String> getDottedValue(String name, boolean withLast) {
        String[] parts = name.split("\\.");
        ensureVariableExists(parts[0]);
        BlockValue current = getVariable(parts[0]);
        int point = 1;
        int lastPoint = withLast ? parts.length -1 : parts.length;
        while(point < lastPoint) {
            Map<String, BlockValue> asMap = current.asMap();
            if (!asMap.containsKey(parts[point])) {
                asMap.put(parts[point], this.wrapValue(new HashMap<String, BlockValue>()));
            }
            current = asMap.get(parts[point]);
            point++;
        }
        return new Pair<>(current, withLast ? parts[point] : "");
    }

    public T getVariable(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name);
        } else if (parentScope != null) {
            return parentScope.getVariable(name);
        } else {
            throw new UndefinedVariableException("Cannot find that variable - " + name);
        }
    }

    public Scope<T> pushScope(boolean setIsLocal) {
        final Scope<T> s = this.newScope();
        s.setIsLocal = setIsLocal;
        s.parentScope = this;
        return s;
    }

    public Scope<T> setLocalAndPushScope(boolean setLocal) {
        return pushScope(setLocal);
    }

    public Scope<T> popScope() {
        return parentScope;
    }

    public abstract Scope<T> newScope();
    public abstract T wrapValue(final Object o);
}
