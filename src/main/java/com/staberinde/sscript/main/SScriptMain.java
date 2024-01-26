package com.staberinde.sscript.main;

import com.staberinde.sscript.model.BlockProgram;
import com.staberinde.sscript.model.BlockProgramContext;
import com.staberinde.sscript.value.BlockValue;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class SScriptMain {
    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java -jar sscript.jar <filename>");
            System.exit(1);
        }

        String filename = args[0];
        final List<BlockValue> params = args.length > 1 ? Arrays.stream(args).skip(1).map(String::trim).map(BlockValue::from).collect(Collectors.toList()) : Collections.emptyList();
        Map<String, BlockValue> a = new HashMap<>();
        a.put("args", BlockValue.from(params));
        try {
            Path p = Paths.get(filename);
            InputStream io = Files.newInputStream(p);
            final String text = IOUtils.toString(io);
            BlockProgram program = MainParser.parse(text, p.getParent().toString());
            BlockProgramContext ctx = BlockProgramContext.createFromParameters(a);
            program.run(ctx);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}
