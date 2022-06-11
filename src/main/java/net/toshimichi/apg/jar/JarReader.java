package net.toshimichi.apg.jar;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarReader {

    private final ArrayDeque<Consumer<JarVisitor>> exec;

    public JarReader(byte[] contents) throws IOException {
        this.exec = new ArrayDeque<>();

        try (ZipInputStream in = new ZipInputStream(new ByteArrayInputStream(contents))) {
            while (true) {
                ZipEntry entry = in.getNextEntry();
                if (entry == null) break;

                byte[] bytes = in.readAllBytes();
                if (entry.isDirectory()) {
                    exec.add(jv -> jv.visitDirectory(entry.getName()));
                } else if (entry.getName().endsWith(".class")) {
                    ClassReader reader = new ClassReader(bytes);
                    ClassNode cn = new ClassNode();
                    reader.accept(cn, ClassReader.EXPAND_FRAMES);
                    exec.add(jv -> jv.visitSource(entry.getName(), cn));
                } else {
                    exec.add(jv -> jv.visitResource(entry.getName(), bytes));
                }
            }
        }
    }

    public void accept(JarVisitor jv) {
        for (Consumer<JarVisitor> c : exec) {
            c.accept(jv);
        }
    }
}
