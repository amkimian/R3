package com.staberinde.sscript.program;

import com.staberinde.sscript.annotation.SFunction;
import com.staberinde.sscript.exception.SSException;
import com.staberinde.sscript.exception.SSParseException;
import com.staberinde.sscript.model.NamedParameter;
import org.antlr.v4.runtime.Token;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;

import java.util.*;

public class DynamicRegistry
{
    private DynamicRegistry() {

    }

    private static final Map<String, CoreBlockDefinition> registry = new HashMap<>();
    static {
        addFromReflections();
    }

    private static void addFromReflections() {
        List<String> extensionPackages = new ArrayList<>();
        extensionPackages.add("com.staberinde.sscript.function");

        Object[] extensionPackagesArray = new String[extensionPackages.size()];
        extensionPackagesArray = extensionPackages.toArray(extensionPackagesArray);
        ConfigurationBuilder builder = ConfigurationBuilder.build(extensionPackagesArray);
        builder.setExpandSuperTypes(false);
        Reflections reflections = new Reflections(builder);
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(SFunction.class);
        for(var x : annotated) {
            add((Class<? extends CoreBlock>) x);
        }
    }

    public static String getNameList() {
        return registry.keySet().toString();
    }

    public static void add(final Class<? extends CoreBlock> clasz) {
        CoreBlockDefinition def = new CoreBlockDefinition(clasz);
        registry.put(def.getBlockName(), def);
    }

    public static CoreBlock constructFrom(Token loc, String name, List<NamedParameter> params) {
        CoreBlockDefinition def = registry.get(name);
        if (def != null) {
            checkParameterCount(name, params, def, loc);

            try {
                return def.newInstance(loc, params);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        throw new SSParseException("Unknown block", loc.getLine(), loc.getCharPositionInLine());
    }

    private static void checkParameterCount(String name, List<NamedParameter> params, BlockDefinition<?> def, Token loc) {
        final int unnamed = getUnnamedCount(params);
        if (def.minParamCount() > unnamed) {
            throw new SSException("Cannot do this");
        } else if (params.size() > def.totalParamCount()) {
            throw new SSException("Cannot do this either");
        }
    }

    private static int getUnnamedCount(List<NamedParameter> params) {
        int unnamedParamCount = 0;
        for(var p : params) {
            if (!p.hasName()) {
                unnamedParamCount++;
            }
        }
        return unnamedParamCount;
    }
}
