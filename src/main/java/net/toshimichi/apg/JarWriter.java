package net.toshimichi.apg;

import lombok.SneakyThrows;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class JarWriter extends JarVisitor {

    private final ZipOutputStream out;

    public JarWriter(ZipOutputStream out) {
        this.out = out;
    }

    @SneakyThrows
    @Override
    public void visitDirectory(String path) {
        out.putNextEntry(new ZipEntry(path));
        out.closeEntry();
    }

    @SneakyThrows
    @Override
    public void visitSource(String path, ClassNode cn) {
        ClassWriter writer = new ClassWriter(0);
        cn.accept(writer);

        out.putNextEntry(new ZipEntry(path));
        out.write(writer.toByteArray());
        out.closeEntry();
    }

    @SneakyThrows
    @Override
    public void visitResource(String path, byte[] bytes) {
        out.putNextEntry(new ZipEntry(path));
        out.write(bytes);
        out.closeEntry();
    }
}
