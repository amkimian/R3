package com.staberinde.sscript.block;

import com.staberinde.sscript.exception.SSParseException;
import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.program.ProgramContext;
import com.staberinde.sscript.value.BlockValue;
import org.antlr.v4.runtime.Token;
import com.staberinde.sscript.SScriptLexer;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;

public class OperatorEqualsVariable extends LocationAwareBlock implements CoreBlock {
    private static final BlockValue ZERO = BlockValue.from(0);

    private final CoreBlock getter;
    private final CoreBlock argBlock;

    private final BinaryOperator<BlockValue> operation;
    private final BiConsumer<ProgramContext<BlockValue>, BlockValue> setter;

    public OperatorEqualsVariable(Token loc, CoreBlock getter, final Token op, CoreBlock argBlock,
                                  BiConsumer<ProgramContext<BlockValue>, BlockValue> setter) {
        super(loc);
        this.getter = getter;
        this.argBlock = argBlock;
        this.operation = parseOperation(op);
        this.setter = setter;
    }

    @Override
    public BlockValue run(ProgramContext<BlockValue> context) {
        final BlockValue variable = Optional.ofNullable(getter.run(context)).filter(v->!v.isNull()).orElse(ZERO);
        final BlockValue argument = argBlock.run(context);
        final BlockValue newVal = operation.apply(variable, argument);
        setter.accept(context, newVal);
        return newVal;
    }

    private static BinaryOperator<BlockValue> parseOperation(Token op) {
        return switch (op.getType()) {
            case SScriptLexer.LOGICALAND -> BlockValue::logicalAnd;
            case SScriptLexer.LOGICALOR -> BlockValue::logicalOr;
            case SScriptLexer.PLUS -> BlockValue::add;
            case SScriptLexer.MINUS -> BlockValue::subtract;
            case SScriptLexer.MUL -> BlockValue::multiply;
            case SScriptLexer.DIV -> BlockValue::divide;
            case SScriptLexer.MOD -> BlockValue::mod;
            case SScriptLexer.POWER -> BlockValue::power;
            default -> throw new SSParseException("Unknown operator: " + op.getText());
        };
    }
}
