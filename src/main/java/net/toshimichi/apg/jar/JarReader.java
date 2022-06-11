package net.toshimichi.apg.jar;

import me.coley.cafedude.InvalidClassException;
import me.coley.cafedude.classfile.ClassFile;
import me.coley.cafedude.io.ClassFileReader;
import me.coley.cafedude.io.ClassFileWriter;
import me.coley.cafedude.transform.IllegalStrippingTransformer;
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

                    // bypass anti-ASM mechanisms
                    ClassFileReader cfr = new ClassFileReader();
                    ClassFile cf = cfr.read(bytes);
                    new IllegalStrippingTransformer(cf).transform();
                    bytes = new ClassFileWriter().write(cf);

                    ClassReader reader = new ClassReader(bytes);
                    ClassNode cn = new ClassNode();
                    reader.accept(cn, ClassReader.EXPAND_FRAMES | ClassReader.SKIP_DEBUG);
                    exec.add(jv -> jv.visitSource(entry.getName(), cn));
                } else {
                    byte[] copy = bytes; // effective final
                    exec.add(jv -> jv.visitResource(entry.getName(), copy));
                }
            }
        } catch (InvalidClassException e) {
            throw new IOException(e);
        }
    }

    public void accept(JarVisitor jv) {
        for (Consumer<JarVisitor> c : exec) {
            c.accept(jv);
        }
    }
}
