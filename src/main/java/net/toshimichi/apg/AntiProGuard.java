package net.toshimichi.apg;

import lombok.extern.slf4j.Slf4j;
import net.toshimichi.apg.jar.JarReader;
import net.toshimichi.apg.jar.JarWriter;
import net.toshimichi.apg.transformer.DistinctThrowsTransformer;
import net.toshimichi.apg.transformer.IntTransformer;
import net.toshimichi.apg.transformer.LengthTransformer;
import net.toshimichi.apg.transformer.NegTransformer;
import net.toshimichi.apg.transformer.NopTransformer;
import net.toshimichi.apg.transformer.StrictfpTransformer;
import net.toshimichi.apg.transformer.StringTransformer;
import net.toshimichi.apg.transformer.TransformerVisitor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipOutputStream;

@Slf4j
public class AntiProGuard {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            log.error("Jar file is not specified");
            return;
        }

        Path in = Path.of(args[0]);
        if (!Files.exists(in)) {
            log.error("Could not find jar file");
            return;
        }

        JarReader reader = new JarReader(Files.readAllBytes(in));

        Path out = in.resolveSibling("deobf-" + in.getFileName());
        try (ZipOutputStream os = new ZipOutputStream(Files.newOutputStream(out))) {
            JarWriter writer = new JarWriter(os);
            TransformerVisitor transformerVisitor = new TransformerVisitor(writer, List.of(
                    new IntTransformer(),
                    new LengthTransformer(),
                    new NegTransformer(),
                    new NopTransformer(),
                    new StrictfpTransformer(),
                    new DistinctThrowsTransformer(),
                    new StringTransformer()));
            reader.accept(transformerVisitor);
        }
    }
}
