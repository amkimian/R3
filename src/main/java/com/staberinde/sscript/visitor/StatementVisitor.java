package com.staberinde.sscript.visitor;

import com.staberinde.sscript.SScriptParser;
import com.staberinde.sscript.block.AssertBlock;
import com.staberinde.sscript.block.BlockStatementList;
import com.staberinde.sscript.block.SetReturnBlock;
import com.staberinde.sscript.exception.SSException;
import com.staberinde.sscript.exception.SSParseException;
import com.staberinde.sscript.model.ProcDef;
import com.staberinde.sscript.model.SParseContext;
import com.staberinde.sscript.program.CoreBlock;
import com.staberinde.sscript.value.BlockValueNull;
import com.staberinde.sscript.value.BlockValueSpecial;
import org.antlr.v4.runtime.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StatementVisitor extends ContextualVisitor<CoreBlock> {
    private static final ClassLoader CLASSLOADER = StatementVisitor.class.getClassLoader();

    StatementVisitor(final SParseContext parseContext) {
        super(parseContext);
    }

    @Override
    public CoreBlock visitStatement(SScriptParser.StatementContext ctx) {
        if (ctx.fDefinition() != null) {
            this.addProcedureDef(ctx.fDefinition());
            return null;
        }
        final CoreBlock stmt = visitStatementInner(ctx);
        return c-> {
            if (isCurrentThreadInterrupted()) {
                throw new SSException("Interrupted");
            }
            return stmt.run(c);
        };
    }

    boolean isCurrentThreadInterrupted() {
        return Thread.currentThread().isInterrupted();
    }

    CoreBlock visitStatementInner(SScriptParser.StatementContext ctx) {
        if (ctx.assertStatement() != null) {
            return handleAssert(ctx.assertStatement());
        }
        if (ctx.assignment() != null) {
            AssignmentVisitor av = new AssignmentVisitor(this.parseContext);
            return av.visit(ctx.assignment());
        }
        if (ctx.ifStatement() != null) {
            IfStatementVisitor iv = new IfStatementVisitor(this.parseContext);
            return iv.visit(ctx.ifStatement());
        }
        if (ctx.varStatement() != null) {
            VarStatementVisitor vv = new VarStatementVisitor(this.parseContext);
            return vv.visit(ctx.varStatement());
        }
        if (ctx.forStatement() != null) {
            ForStatementVisitor fv = new ForStatementVisitor(this.parseContext);
            return fv.visit(ctx.forStatement());
        }
        if (ctx.foreachStatement() != null) {
            ForEachStatementVisitor fv = new ForEachStatementVisitor(this.parseContext);
            return fv.visit(ctx.foreachStatement());
        }
        if (ctx.whileStatement() != null) {
            WhileStatementVisitor wv = new WhileStatementVisitor(this.parseContext);
            return wv.visit(ctx.whileStatement());
        }
        if (ctx.exp != null) {
            ExpressionVisitor ev = new ExpressionVisitor(this.parseContext);
            return ev.visit(ctx.expression());
        }
        if (ctx.Break() != null) {
            return context -> BlockValueSpecial.BREAK;
        }
        if (ctx.Continue() != null) {
            return context -> BlockValueSpecial.CONTINUE;
        }
        if (ctx.Return() != null) {
            return handleReturn(ctx);
        }
        throw new SSParseException("Unknown statement: " + ctx.getText());
    }

    private CoreBlock handleReturn(SScriptParser.StatementContext ctx) {
        ExpressionVisitor ev = new ExpressionVisitor(this.parseContext);
        return new SetReturnBlock(ctx.start, ctx.ret != null ? ev.visit(ctx.ret) : context -> BlockValueNull.getInstance());
    }

    private CoreBlock handleAssert(SScriptParser.AssertStatementContext ctx) {
        final ExpressionVisitor ev = new ExpressionVisitor(this.parseContext);
        final CoreBlock exp;
        if (ctx.expression() != null) {
            exp = ev.visit(ctx.exp);
        } else {
            exp = this.visitStatement(ctx.statement());
        }
        final CoreBlock message = Optional.ofNullable(ctx.m).map(ev::visitExpression).orElse(null);
        return new AssertBlock(ctx.start, ctx.ty, exp, message);
    }

    private void addProcedureDef(SScriptParser.FDefinitionContext ctxt) {
        final BlockVisitor bv = new BlockVisitor(this.parseContext.newChild(false));
        final BlockStatementList procedure = bv.visitBlock(ctxt.xb);
        final List<String> paramNames = new ArrayList<>();
        for(Token t : ctxt.params) {
            paramNames.add(t.getText());
        }

        final String funcName = ctxt.name.getText();
        this.parseContext.registerProcedure(funcName, new ProcDef(funcName, paramNames, procedure));
    }
}
