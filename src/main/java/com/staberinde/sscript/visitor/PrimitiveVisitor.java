package com.staberinde.sscript.visitor;

import com.staberinde.sscript.SScriptBaseVisitor;
import com.staberinde.sscript.SScriptParser;
import com.staberinde.sscript.block.PrimitiveBlock;
import com.staberinde.sscript.program.CoreBlock;

public class PrimitiveVisitor extends SScriptBaseVisitor<CoreBlock> {

    private String getMultiline(String input) {
        input = input.substring(input.indexOf("\r\n") + 2);
        input = input.substring(0, input.lastIndexOf("###"));
        return input;
    }

    @Override
    public CoreBlock visitPrimitive(SScriptParser.PrimitiveContext ctx) {
        if (ctx.Integer() != null) {
            return new PrimitiveBlock(ctx.start, PrimitiveBlock.PrimitiveType.INTEGER, ctx.Integer().getText());
        } else if (ctx.Long() != null) {
            return new PrimitiveBlock(ctx.start, PrimitiveBlock.PrimitiveType.LONG, ctx.Long().getText());
        } else if (ctx.Number() != null) {
            final String num = ctx.Number().getText().toLowerCase();
            if (num.charAt(num.length()-1) == 'f') {
                return new PrimitiveBlock(ctx.start, PrimitiveBlock.PrimitiveType.FLOAT, num);
            } else {
                return new PrimitiveBlock(ctx.start, PrimitiveBlock.PrimitiveType.DOUBLE, num);
            }
        } else if (ctx.String() != null) {
            return new PrimitiveBlock(ctx.start, PrimitiveBlock.PrimitiveType.STRING, ctx.String().getText());
        } else if (ctx.Bool() != null) {
            return new PrimitiveBlock(ctx.start, PrimitiveBlock.PrimitiveType.BOOL, ctx.Bool().getText());
        } else if (ctx.Null() != null) {
            return new PrimitiveBlock(ctx.start, PrimitiveBlock.PrimitiveType.NULL, ctx.Null().getText());
        } else if (ctx.MultiLineString() != null) {
            return new PrimitiveBlock(ctx.start, PrimitiveBlock.PrimitiveType.STRING, getMultiline(ctx.MultiLineString().getText()));
        } else {
            throw new RuntimeException("Invalid primitive");
        }
    }
}
