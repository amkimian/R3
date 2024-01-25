package com.staberinde.sscript.model;

import com.staberinde.sscript.program.CoreBlock;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.list.UnmodifiableList;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProcDef {
    private final List<String> optionalParams;
    private final List<String> requiredParams;
    private final String funcName;
    private final CoreBlock body;

    public ProcDef(String funcName, List<String> requiredParams, CoreBlock body) {
        this(funcName, requiredParams, null, body);
    }

    public ProcDef(String funcName, List<String> requiredParams, List<String> optionalParams, CoreBlock body) {
        this.funcName = funcName;
        this.requiredParams = requiredParams;
        this.optionalParams = optionalParams != null ? UnmodifiableList.unmodifiableList(optionalParams) : null;
        this.body = body;
    }

    public String getName() {
        return funcName;
    }

    public List<String> getRequiredParams() {
        return requiredParams;
    }

    public List<String> getOptionalParams() {
        return optionalParams;
    }

    public List<String> getParameterNames() {
        if (!CollectionUtils.isEmpty(optionalParams)) {
            return Collections.unmodifiableList(Stream.of(requiredParams, optionalParams).flatMap(List::stream).collect(Collectors.toList()));
        } else {
            return requiredParams;
        }
    }

    public CoreBlock getBlock() {
        return body;
    }
}
