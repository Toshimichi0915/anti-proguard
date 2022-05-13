package net.toshimichi.apg;

import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
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
            reader.accept(writer);
        }
    }
}
