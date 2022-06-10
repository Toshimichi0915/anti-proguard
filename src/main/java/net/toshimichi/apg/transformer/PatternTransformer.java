package net.toshimichi.apg.transformer;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

abstract public class PatternTransformer implements ClassTransformer {

    abstract public int getSize();

    abstract public List<? extends AbstractInsnNode> transform(List<? extends AbstractInsnNode> from);

    @Override
    public boolean transform(String path, ClassNode cn) {
        int size = getSize();
        for (MethodNode mn : cn.methods) {
            InsnList il = mn.instructions;
            for (int i = 0; i < il.size() - size; i++) {
                List<AbstractInsnNode> from = new ArrayList<>(size);
                for (int j = 0; j < size; j++) {
                    from.add(il.get(i + j));
                }

                from = Collections.unmodifiableList(from);
                List<? extends AbstractInsnNode> to = transform(from);
                if (to == null) continue;

                AbstractInsnNode cursor = il.get(i);
                for (int j = 0; j < to.size(); j++) {
                    AbstractInsnNode in = to.get(to.size() - j - 1);
                    il.insertBefore(cursor, in);
                    cursor = in;
                }

                from.forEach(il::remove);
                return true;
            }
        }
        return false;
    }
}
