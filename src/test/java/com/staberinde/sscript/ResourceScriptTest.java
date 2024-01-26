package com.staberinde.sscript;

import com.staberinde.sscript.util.BaseScriptRun;
import org.junit.Test;

import java.io.IOException;

public class ResourceScriptTest extends BaseScriptRun {
    @Test
    public void testResourceScript() throws IOException {
        var ctx = runScript("$test.ss");
        System.out.println(ctx.toString());
    }
}
