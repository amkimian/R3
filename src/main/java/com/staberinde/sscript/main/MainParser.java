package com.staberinde.sscript.main;

import com.staberinde.sscript.SScriptLexer;
import com.staberinde.sscript.SScriptParser;
import com.staberinde.sscript.exception.SSParseException;
import com.staberinde.sscript.model.BlockProgram;
import com.staberinde.sscript.model.SParseContext;
import com.staberinde.sscript.visitor.MainBlockVisitor;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.BitSet;
import java.util.Optional;
import java.util.function.Supplier;

public class MainParser {
    private MainParser() {

    }

    public static BlockProgram parse(final String text, final String rootPath) throws IOException {
        return parse(text, null, rootPath);
    }

    public static BlockProgram parse(final String text, final SParseContext rootContext, final String rootPath) throws IOException {
        return parse(text, rootContext, rootPath, false).get();
    }

    public static BlockProgram parseLibrary(final String text, final SParseContext rootContext, final String library) throws IOException {
        String savedLibrary = rootContext.getLibrary();
        rootContext.setLibrary(library);
        BlockProgram prog = parse(text, rootContext, rootContext.getRootPath(), false).get();
        rootContext.setLibrary(savedLibrary);
        return prog;
    }

    private static Supplier<BlockProgram> parse(final String text, final SParseContext rootContext, final String rootPath, final boolean lazyResolve) throws IOException {
        final InputStream stream = new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8));
        final SScriptLexer lexer = new SScriptLexer(CharStreams.fromStream(stream, StandardCharsets.UTF_8));
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final SScriptParser parser = new SScriptParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new ANTLRErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object o, int i, int i1, String s, RecognitionException e) {
                throw new SSParseException("Syntax error at " + i + ":" + i1 + " " + s);
            }

            @Override
            public void reportAmbiguity(Parser parser, DFA dfa, int i, int i1, boolean b, BitSet bitSet, ATNConfigSet atnConfigSet) {

            }

            @Override
            public void reportAttemptingFullContext(Parser parser, DFA dfa, int i, int i1, BitSet bitSet, ATNConfigSet atnConfigSet) {

            }

            @Override
            public void reportContextSensitivity(Parser parser, DFA dfa, int i, int i1, int i2, ATNConfigSet atnConfigSet) {

            }
        });

        final SParseContext parseContext = Optional.ofNullable(rootContext).orElse(new SParseContext(true, rootPath, null));
        final MainBlockVisitor mb = new MainBlockVisitor(parseContext, lazyResolve);
        return mb.visitMainBlock(parser.mainBlock());
    }
}
