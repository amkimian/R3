package com.staberinde.sscript.program;

import com.staberinde.sscript.annotation.SParam;
import com.staberinde.sscript.exception.SSException;
import com.staberinde.sscript.exception.SSParseException;
import com.staberinde.sscript.model.NamedParameter;
import com.staberinde.sscript.value.BlockValue;
import org.antlr.v4.runtime.Token;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public abstract class BlockDefinition<T extends PureBlock> {
    protected final Class<? extends T> baseClass;

    private final Map<Integer, ParamInfo> valueIndex = new HashMap<>();
    private final Map<String, ParamInfo> nameIndex = new HashMap<>();

    private final String blockName;

    private int minimumParameters = 0;

    protected BlockDefinition(final Class<? extends T> clazz, final Class<? extends Annotation> annClazz) {
        this.baseClass = clazz;
        this.blockName = Stream.of(baseClass.getAnnotations())
                .filter(a -> a.annotationType().equals(annClazz))
                .findFirst()
                .map(this::getName)
                .orElseGet(this.baseClass::getCanonicalName);
        final Field[] cFields = clazz.getDeclaredFields();
        for(Field field : cFields) {
            final SParam annotation;
            if ((annotation = field.getAnnotation(SParam.class)) != null) {
                field.setAccessible(true);
                final ParamInfo info = new ParamInfo(field, annotation);
                this.valueIndex.put(annotation.value(), info);
                if (annotation.name().isEmpty()) {
                    this.minimumParameters++;
                } else {
                    this.nameIndex.put(annotation.name(), info);
                }
            }
        }
    }

    public T newInstance(final Token loc, final List<NamedParameter> params) throws InstantiationException, IllegalAccessException {
        final T instance = getBaseInstance(loc);
        final AtomicReference<TypedProxyBuilder<T>> proxyBuilderRef = new AtomicReference<>();

        for(int i=0; i< params.size(); i++) {
            final NamedParameter np = params.get(i);
            if (np.hasName()) {
                if (!this.processField(nameIndex.get(np.getName()), instance, np.getExpression(), proxyBuilderRef)) {
                    throw new SSParseException("No parameter", loc.getLine(), loc.getCharPositionInLine());
                }
            } else {
                if (!this.processField(valueIndex.get(i), instance, params.get(i).getExpression(), proxyBuilderRef)) {
                    throw new SSParseException("No parameter", loc.getLine(), loc.getCharPositionInLine());
                }
            }
        }

        final TypedProxyBuilder<T> proxyBuilder = proxyBuilderRef.get();
        if (proxyBuilder != null) {
            return proxyBuilder.build(instance);
        } else {
            return instance;
        }
    }

    private T getBaseInstance(final Token loc) throws InstantiationException, IllegalAccessException {
        try {
            Constructor<? extends T> c = this.baseClass.getDeclaredConstructor(Token.class);
            return c.newInstance(loc);
        } catch(final Exception e) {
            return baseClass.newInstance();
        }
    }

    private boolean processField(final ParamInfo paramInfo, final T instance, RunnableBlock<? extends BlockValue> valueBlock, final AtomicReference<TypedProxyBuilder<T>> builderRef) throws IllegalAccessException {
        if (paramInfo == null) {
            return false;
        }
        if (paramInfo.needsProxy()) {
            addTypedField(paramInfo, valueBlock, builderRef);
            return true;
        }

        final Field field = paramInfo.field;
        if (field.getType().isAssignableFrom(BaseBlock.class) && !valueBlock.getClass().isAssignableFrom(BaseBlock.class)) {
            final CoreBlock blockValue;
            try {
                blockValue = (CoreBlock) valueBlock;
            } catch (final ClassCastException e) {
                throw new SSException(e);
            }
            field.set(instance, (BaseBlock) blockValue::run);
        } else {
            field.set(instance, valueBlock);
        }
        return true;
    }

    private void addTypedField(final ParamInfo info, final RunnableBlock<? extends BlockValue> valueBlock, final AtomicReference<TypedProxyBuilder<T>> builderRef) {
        final TypedProxyBuilder<T> proxyBuilder = builderRef.updateAndGet(builder -> Optional.ofNullable(builder).orElseGet(this::newProxyBuilder));
        proxyBuilder.addTypedField(info, valueBlock);
    }

    protected abstract String getName(Annotation a);
    protected abstract TypedProxyBuilder<T> newProxyBuilder();

    public String getBlockName() {
        return this.blockName;
    }

    public int minParamCount() {
        return this.minimumParameters;
    }

    public int totalParamCount() {
        return this.valueIndex.size();
    }
}
