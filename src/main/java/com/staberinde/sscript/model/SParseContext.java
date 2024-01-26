package com.staberinde.sscript.model;

import com.staberinde.sscript.SScriptParser;
import com.staberinde.sscript.block.BlockStatementList;
import com.staberinde.sscript.block.CallProcBlock;
import com.staberinde.sscript.exception.SSException;
import com.staberinde.sscript.program.CoreBlock;
import org.antlr.v4.runtime.Token;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.list.UnmodifiableList;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public class SParseContext {
    private final AtomicReference<BlockProgram> programRef = new AtomicReference<>();
    private final boolean isRoot;

    private final List<String> requiredParams = new ArrayList<>();
    private List<String> optionalParams;

    private List<SParseContext> children = new ArrayList<>();

    protected final Map<String, ProcDef> procedureDefs;

    private final Map<ProcCallInfo, AtomicReference<CoreBlock>> outstandingProcCalls = new HashMap<>();

    private final String rootPath;
    private String library;

    public SParseContext(final boolean isRoot, final String rootPath, final String library) {
        this.isRoot = isRoot;
        this.rootPath = rootPath;
        this.library = library;
        this.procedureDefs = new HashMap<>();
    }

    public String getRootPath() {
        return rootPath;
    }

    public String getLibrary() {
        return library;
    }

    public boolean isFinalized() {
        return this.programRef.get() != null;
    }

    public Supplier<BlockProgram> programReference() {
        return () -> Optional.ofNullable(this.programRef.get()).orElseThrow(() -> new SSException("Program not finalized"));
    }

    public ProcDef getRegisteredProcedure(final String procName) {
        return this.procedureDefs.get(procName);
    }

    public void registerProcedure(final String procName, final ProcDef procDef) {
        this.children.forEach(child -> child.registerProcedure(procName, procDef));
        String name = procDef.getName();
        if (library != null) {
            name = library + "." + name;
        }
        if (procedureDefs.containsKey(name)) {
            throw new SSException("Duplicate procedure definition: " + name);
        }
        procedureDefs.put(name, procDef);
    }

    public AtomicReference<CoreBlock> registerProcedureCall(final SScriptParser.ProcCallContext ctx, final List<NamedParameter> params) {
        String functionName = ctx.name.getText();
        functionName = this.library == null ? functionName : this.library + "." + functionName;
        final AtomicReference<CoreBlock> callRef = new AtomicReference<>();
        outstandingProcCalls.put(new ProcCallInfo(ctx.start, functionName, params), callRef);
        return callRef;
    }

    private void resolveProcedureCalls() {
        this.outstandingProcCalls.forEach((info, ref) -> {
           final ProcDef registeredDef = this.procedureDefs.get(info.functionName);
           if (registeredDef == null) {
               throw new SSException("Procedure not found: " + info.functionName);
           } else if (registeredDef.getRequiredParams().size() != info.params.size()) {
               throw new SSException("Procedure " + info.functionName + " requires " + registeredDef.getRequiredParams().size() + " parameters, but " + info.params.size() + " were provided");
           } else {
               ref.set(new CallProcBlock(info.location, info.functionName, info.params, registeredDef));
           }

        });
        this.outstandingProcCalls.clear();
    }

    public void addRequiredParams(String name) {
        requiredParams.add(name);
    }

    public List<String> getRequiredParams() {
        return new ArrayList<String>(requiredParams);
    }

    public void addOptionalParams(String name) {
        if (optionalParams == null) {
            optionalParams = new ArrayList<>();
        }
        optionalParams.add(name);
    }

    public List<String> getOptionalParams() {
        return optionalParams == null ? null : new ArrayList<String>(optionalParams);
    }

    public List<String> getParameterNames() {
        if (CollectionUtils.isEmpty(optionalParams)) {
            return new ArrayList<>(requiredParams);
        } else {
            List<String> params = new ArrayList<>(requiredParams);
            params.addAll(optionalParams);
            return params;
        }
    }

    public void finalizeProgram(final BlockStatementList statements) {
        this.children.forEach(child -> child.finalizeProgram(statements));
        resolveProcedureCalls();
        final BlockProgram program = new BlockProgram(statements, UnmodifiableList.unmodifiableList(this.requiredParams));
        programRef.set(program);
    }

    public SParseContext newChild(final boolean isRoot) {
        final SParseContext child = new SParseContext(isRoot, this.rootPath, this.library);
        this.children.add(child);
        return child;
    }

    public void setLibrary(final String library) {
        this.library = library;
    }

    private static final class ChildParseContext extends SParseContext {
        ChildParseContext(final SParseContext parent, final boolean isRoot) {
            super(isRoot, parent.getRootPath(), parent.getLibrary());
            this.procedureDefs.putAll(parent.procedureDefs);
        }
    }

    private static final class ProcCallInfo {
        private final Token location;
        private final String functionName;
        private final List<NamedParameter> params;

        ProcCallInfo(final Token location, final String functionName, final List<NamedParameter> params) {
            this.location = location;
            this.functionName = functionName;
            this.params = params;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) return true;
            if (!(o instanceof ProcCallInfo)) return false;
            final ProcCallInfo that = (ProcCallInfo) o;
            return Objects.equals(location, that.location) &&
                    Objects.equals(functionName, that.functionName) &&
                    Objects.equals(params, that.params);
        }

        @Override
        public int hashCode() {
            return Objects.hash(location, functionName, params);
        }
    }

}
