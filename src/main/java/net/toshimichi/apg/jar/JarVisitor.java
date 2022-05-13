package net.toshimichi.apg.jar;

import lombok.RequiredArgsConstructor;
import org.objectweb.asm.tree.ClassNode;

@RequiredArgsConstructor
public class JarVisitor {

    private final JarVisitor jv;

    public JarVisitor() {
        this(null);
    }

    public void visitDirectory(String path) {
        if (jv != null) {
            jv.visitDirectory(path);
        }
    }

    public void visitSource(String path, ClassNode cn) {
        if (jv != null) {
            jv.visitSource(path, cn);
        }
    }

    public void visitResource(String path, byte[] bytes) {
        if (jv != null) {
            jv.visitResource(path, bytes);
        }
    }
}
