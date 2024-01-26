package com.staberinde.sscript;

import com.staberinde.sscript.main.MainParser;
import com.staberinde.sscript.model.BlockProgram;
import com.staberinde.sscript.model.BlockProgramContext;
import org.junit.Test;

import java.io.IOException;

public class BasicTest {

    @Test
    public void testTheSimpleLanguage() throws IOException {
        // Tests parsing and executing SS Script
        BlockProgram program = MainParser.parse("x=1;y=2*x;z=3*y;z;", null);
        BlockProgramContext ctx = new BlockProgramContext();
        System.out.println(program.run(ctx));
    }
}
