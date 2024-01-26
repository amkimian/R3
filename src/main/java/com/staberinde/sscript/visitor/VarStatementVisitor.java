package com.staberinde.sscript.visitor;

import com.staberinde.sscript.SScriptParser;
import com.staberinde.sscript.block.BlockStatementList;
import com.staberinde.sscript.block.DefineVariableBlock;
import com.staberinde.sscript.block.VariablesSet;
import com.staberinde.sscript.model.SParseContext;
import com.staberinde.sscript.program.CoreBlock;

public class VarStatementVisitor extends ContextualVisitor<CoreBlock>{
    VarStatementVisitor(final SParseContext parseContext) {
        super(parseContext);
    }

    @Override
    public CoreBlock visitVarStatement(SScriptParser.VarStatementContext ctx) {
        BlockStatementList bs  = new BlockStatementList();
        for(var vdc : ctx.defs) {
            final String varName = vdc.id.getText();
            if (vdc.exp != null) {
                final ExpressionVisitor ev = new ExpressionVisitor(this.parseContext);
                final CoreBlock toSet = ev.visit(vdc.exp);
                final VariablesSet vs = new VariablesSet(vdc.start, varName, toSet);
                bs.addBlock(new DefineVariableBlock(vdc.start, varName, vs));
            } else {
                bs.addBlock(new DefineVariableBlock(vdc.start, varName));
            }
        }
        return bs;
    }
}
