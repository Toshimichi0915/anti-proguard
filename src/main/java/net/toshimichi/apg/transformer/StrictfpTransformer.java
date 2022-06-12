package net.toshimichi.apg.transformer;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class StrictfpTransformer implements ClassTransformer {

    @Override
    public boolean transform(String path, ClassNode cn) {

        int from = cn.access;
        cn.access = from & ~Opcodes.ACC_STRICT;
        boolean changed = from != cn.access;

        for (MethodNode mn : cn.methods) {
            from = mn.access;
            mn.access = from & ~Opcodes.ACC_STRICT;
            changed |= from != mn.access;
        }


        return changed;
    }
}
