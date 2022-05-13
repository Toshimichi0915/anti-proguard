package net.toshimichi.apg.transformer;

import org.objectweb.asm.tree.ClassNode;

public interface ClassTransformer {

    boolean transform(String path, ClassNode cn);
}
