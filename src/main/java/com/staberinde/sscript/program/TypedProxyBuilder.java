package com.staberinde.sscript.program;

import com.staberinde.sscript.exception.SSException;
import com.staberinde.sscript.value.BlockValue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TypedProxyBuilder<T extends PureBlock> {
    private static final ClassLoader CLASSLOADER = TypedProxyBuilder.class.getClassLoader();

    private final Class<T> proxyInterface;
    private final Method runMethod;
    private final int contextArg;

    private final Map<ParamInfo, RunnableBlock<? extends BlockValue>> targetFields = new HashMap<>();

    public TypedProxyBuilder(final Class<T> proxyInterface, final Method runMethod, final int contextArg) {
        this.proxyInterface = proxyInterface;
        this.runMethod = runMethod;
        this.contextArg = contextArg;
    }

    void addTypedField(final ParamInfo info, final RunnableBlock<? extends BlockValue> valueBlock) {
        targetFields.put(info, valueBlock);
    }

    T build(final T instance) {
        final InvocationHandler runHandler = (proxy, method, args) -> {
            try {
                if (method.getName().equals(runMethod.getName())) {
                    final ProgramContext<? extends BlockValue> context = (ProgramContext<? extends BlockValue>) args[contextArg];
                    this.targetFields.forEach((paramInfo, runnableBlock) -> {
                        final BlockValue fieldValue = runnableBlock.run(castContext(context, context.getClass()));
                        try {
                            paramInfo.field.set(instance, Objects.requireNonNull(paramInfo.converter).apply(fieldValue));
                        } catch (final IllegalAccessException e) {
                            throw new SSException(e);
                        }
                    });
                }
                return method.invoke(instance, args);
            } catch (InvocationTargetException e) {
                throw e.getCause(); // TODO
            }
        };
        return (T) Proxy.newProxyInstance(CLASSLOADER, new Class[]{this.proxyInterface}, runHandler);
    }

    private <C extends ProgramContext> C castContext(final Object context, final Class<C> classz) {
        return classz.cast(context);
    }
}
