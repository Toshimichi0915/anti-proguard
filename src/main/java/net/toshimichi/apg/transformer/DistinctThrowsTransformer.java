package net.toshimichi.apg.transformer;

import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class DistinctThrowsTransformer implements ClassTransformer {
    @Override
    public boolean transform(String path, ClassNode cn) {
        boolean changed = false;

        for (MethodNode mn : cn.methods) {
            int size = mn.exceptions.size();
            mn.exceptions = mn.exceptions.stream().distinct().toList();
            changed |= size != mn.exceptions.size();
        }
        return changed;
    }
}
