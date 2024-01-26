package com.staberinde.sscript.util;

import com.staberinde.sscript.exception.SSParseException;
import com.staberinde.sscript.main.MainParser;
import com.staberinde.sscript.model.BlockProgram;
import com.staberinde.sscript.model.BlockProgramContext;
import com.staberinde.sscript.value.BlockValue;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class BaseScriptRun {
    private static final ClassLoader CLASSLOADER = BaseScriptRun.class.getClassLoader();

    public BlockProgram parseProgram(String resourceName) throws IOException {
        InputStream io = CLASSLOADER.getResourceAsStream(resourceName.startsWith("$") ? resourceName.substring(1) : resourceName);
        if (io != null) {
            final String text = IOUtils.toString(io);
            Path parent = Paths.get(resourceName).getParent();
            return MainParser.parse(text, parent == null ? null : parent.toString());
        }
        throw new SSParseException("Resource not found: " + resourceName);
    }

    public BlockProgramContext runScript(String resourceName, Map<String, BlockValue> params) throws IOException {
        BlockProgramContext ctx = BlockProgramContext.createFromParameters(params);
        BlockProgram p = parseProgram(resourceName);
        p.run(ctx);
        return ctx;
    }

    public BlockProgramContext runScript(String resourceName) throws IOException {
        return runScript(resourceName, new HashMap<>());
    }

    public void runScriptFolder(String folderName) throws IOException, URISyntaxException {
        URL dirURL = CLASSLOADER.getResource(folderName.substring(1));
        if (dirURL != null && dirURL.getProtocol().equals("file")) {
            File[] files = new File(dirURL.toURI()).listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        runScript(folderName + file.getName());
                    }
                }
            }
        }
    }
}
