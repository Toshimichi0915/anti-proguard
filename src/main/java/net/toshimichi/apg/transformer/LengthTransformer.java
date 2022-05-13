package net.toshimichi.apg.transformer;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.List;

public class LengthTransformer extends PatternTransformer {

    @Override
    public int getSize() {
        return 2;
    }

    @Override
    public List<? extends AbstractInsnNode> transform(List<? extends AbstractInsnNode> from) {
        AbstractInsnNode i1 = from.get(0);
        AbstractInsnNode i2 = from.get(1);
        if (!(i1 instanceof LdcInsnNode ldc)) return null;
        if (!(ldc.cst instanceof String str)) return null;
        if (!(i2 instanceof MethodInsnNode method)) return null;
        if (!(method.owner + "." + method.name + method.desc).equals("java/lang/String.length()I")) return null;

        return List.of(new LdcInsnNode(str.length()));
    }
}
