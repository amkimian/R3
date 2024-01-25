package com.staberinde.sscript.block;

import com.staberinde.sscript.exception.SSException;
import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import com.staberinde.sscript.value.BlockValueJavaObj;
import com.staberinde.sscript.value.BlockValueNull;
import org.antlr.v4.runtime.Token;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class VariablesGet extends LocationAwareBlock implements CoreBlock {
    private final String name;
    private final List<CoreBlock> indices;
    private final CoreBlock rangeStart;
    private final CoreBlock rangeEnd;

    public VariablesGet(Token loc, String varName) {
        super(loc);
        this.name = varName;
        this.indices = null;
        this.rangeStart = null;
        this.rangeEnd = null;
    }

    public VariablesGet(Token loc, String varName, List<CoreBlock> indices) {
        super(loc);
        this.name = varName;
        this.indices = indices;
        this.rangeStart = null;
        this.rangeEnd = null;
    }

    public VariablesGet(Token loc, String varName, CoreBlock rangeStart, CoreBlock rangeEnd) {
        super(loc);
        this.name = varName;
        this.indices = null;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        BlockValue ret = getVar(context);
        if (this.indices != null) {
            for(final CoreBlock idxBlock : indices) {
                final BlockValue idxVal = idxBlock.run(context);
                ret = getIndexOf(ret, idxVal);
            }
        } else if (this.rangeStart != null || this.rangeEnd != null) {
            ret = getSlice(context, ret);
        }
        return ret;
    }

    private BlockValue getIndexOf(final BlockValue base, final BlockValue idxVal) {
        final int idx;
        if (idxVal.isString()) {
            final String idxStr = idxVal.asString();
            if (base.isMap()) {
                return Optional.ofNullable(base.asMap().get(idxStr)).orElse(BlockValueNull.getInstance());
            } else {
                throw new SSException("Cannot index into non-map value with string");
            }
        } else {
            idx = idxVal.asInteger();
        }
        final List<BlockValue> vals = base.asInternalList();
        try {
            return vals.get(idx);
        } catch(final IndexOutOfBoundsException e) {
            throw getRuntimeException(e);
        }
    }

    private BlockValue getVar(final ProgramContext<BlockValue> context) {
        if (this.name.indexOf(".") != -1) {
            return dottedGet(context, this.name);
        } else {
            return context.getVariable(this.name);
        }
    }

    private BlockValue dottedGet(ProgramContext<BlockValue> context, String name) {
        String[] parts = name.split("\\.");
        BlockValue current = context.getVariable(parts[0]);
        int point = 1;
        while(current != null && point < parts.length) {
            if (current.isMap()) {
                Map<String, BlockValue> mapVal = current.asMap();
                if (mapVal == null) {
                    throw getRuntimeException("Not a map");
                } else {
                    if (parts[point].startsWith("^")) {
                        BlockValue index = context.getVariable(parts[point].substring(1));
                        current = mapVal.get(index.asString());
                    } else {
                        current = mapVal.get(parts[point]);
                    }
                    point++;
                }
            } else if (current.isJavaObj()) {
                return ((BlockValueJavaObj)current).getJavaField(parts[point]);
            }
        }
        if (current == null) {
            throw getRuntimeException("No dotted path to " + name);
        }
        return current;
    }
    private BlockValue getSlice(final ProgramContext<BlockValue> context, final BlockValue base) {
        final int start = Optional.ofNullable(this.rangeStart).map(rs->rs.run(context).asInteger()).orElse(0);
        final Optional<Integer> endOpt = Optional.ofNullable(this.rangeEnd).map(re->re.run(context).asInteger());

        if (base.isString()) {
            final String rootStr = base.asString();
            int end = endOpt.orElse(rootStr.length());
            if (end < 0) {
                end = rootStr.length() + end;
            }
            return BlockValue.from(rootStr.substring(start, end));
        } else {
            final List<BlockValue> rootList = base.asInternalList();
            final int end = endOpt.orElse(rootList.size());
            return BlockValue.from(rootList.subList(start, end));
        }
    }
}
