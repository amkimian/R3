package com.staberinde.sscript.visitor;

import com.staberinde.sscript.SScriptLexer;
import com.staberinde.sscript.SScriptParser;
import com.staberinde.sscript.block.OperationBlock;
import com.staberinde.sscript.exception.SSParseException;
import com.staberinde.sscript.model.SParseContext;
import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.value.BlockValue;
import org.antlr.v4.runtime.Token;

import java.util.List;
import java.util.Optional;
import java.util.function.BinaryOperator;

public class ValueExpressionVisitor extends ContextualVisitor<CoreBlock> {
    ValueExpressionVisitor(final SParseContext parseContext) {
        super(parseContext);
    }

    @Override
    public CoreBlock visitParenthesisExpr(SScriptParser.ParenthesisExprContext ctx) {
        return super.visit(ctx.valueExpression());
    }

    @Override
    public CoreBlock visitPowerExpr(SScriptParser.PowerExprContext ctx) {
        return constructOpBlock(ctx.start, ctx.valueExpression(), ctx.op);
    }

    @Override
    public CoreBlock visitMulExpr(SScriptParser.MulExprContext ctx) {
        return constructOpBlock(ctx.start, ctx.valueExpression(), ctx.op);
    }

    @Override
    public CoreBlock visitAddExpr(SScriptParser.AddExprContext ctx) {
        return constructOpBlock(ctx.start, ctx.valueExpression(), ctx.op);
    }

    @Override
    public CoreBlock visitRelExpr(SScriptParser.RelExprContext ctx) {
        return constructOpBlock(ctx.start, ctx.valueExpression(), ctx.op);
    }

    @Override
    public CoreBlock visitEquExpr(SScriptParser.EquExprContext ctx) {
        return constructOpBlock(ctx.start, ctx.valueExpression(), ctx.op);
    }

    @Override
    public CoreBlock visitBaseExpr(SScriptParser.BaseExprContext ctx) {
        final BaseExpressionVisitor bev = new BaseExpressionVisitor(this.parseContext);
        return bev.visit(ctx);
    }

    private CoreBlock constructOpBlock(final Token loc, final List<SScriptParser.ValueExpressionContext> expContexts, final Token op) {
        return this.constructOpBlock(loc, expContexts, op, OperationBlock::new);
    }

    private OperationBlock constructOpBlock(final Token loc, final List<SScriptParser.ValueExpressionContext> expContexts, final Token op, final OperationBlockFactory factory) {
        final CoreBlock lhs = super.visit(expContexts.get(0));
        final CoreBlock rhs = super.visit(expContexts.get(1));

        final BinaryOperator<BlockValue> operation = Optional.ofNullable(getOperationFromType(op.getType())).orElseThrow(() -> new SSParseException("Unknown operation type: " + op.getText()));
        return factory.construct(loc, lhs, rhs, operation);
    }

    private BinaryOperator<BlockValue> getOperationFromType(final int type) {
        return switch (type) {
            case SScriptLexer.LOGICALAND -> BlockValue::logicalAnd;
            case SScriptLexer.LOGICALOR -> BlockValue::logicalOr;
            case SScriptLexer.PLUS -> BlockValue::add;
            case SScriptLexer.MINUS -> BlockValue::subtract;
            case SScriptLexer.MUL -> BlockValue::multiply;
            case SScriptLexer.DIV -> BlockValue::divide;
            case SScriptLexer.MOD -> BlockValue::mod;
            case SScriptLexer.POWER -> BlockValue::power;
            case SScriptLexer.GT -> BlockValue::greaterThan;
            case SScriptLexer.GTEQ -> (l,r) -> BlockValue.from(!l.lessThan(r).asBoolean());
            case SScriptLexer.LT -> BlockValue::lessThan;
            case SScriptLexer.LTEQ -> (l,r) -> BlockValue.from(!l.greaterThan(r).asBoolean());
            default -> null;
        };
    }
    private interface OperationBlockFactory {
        OperationBlock construct(final Token loc, final CoreBlock lhs, final CoreBlock rhs, final BinaryOperator<BlockValue> operation);
    }
}
