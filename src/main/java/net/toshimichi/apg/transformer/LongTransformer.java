package net.toshimichi.apg.transformer;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

import java.util.List;

public class LongTransformer extends PatternTransformer {

    @Override
    public int getSize() {
        return 3;
    }

    @Override
    public List<? extends AbstractInsnNode> transform(List<? extends AbstractInsnNode> from) {
        AbstractInsnNode i1 = from.get(0);
        AbstractInsnNode i2 = from.get(1);
        AbstractInsnNode i3 = from.get(2);
        if (!(i1 instanceof LdcInsnNode ll1)) return null;
        if (!(i2 instanceof LdcInsnNode ll2)) return null;
        if (!(ll1.cst instanceof Long l1)) return null;
        if (!(ll2.cst instanceof Long l2)) return null;
        if (!(i3 instanceof InsnNode)) return null;

        int op = i3.getOpcode();

        long result;
        if (op == Opcodes.LAND) {
            result = l1 & l2;
        } else if (op == Opcodes.LXOR) {
            result = l1 ^ l2;
        } else if (op == Opcodes.LSHL) {
            result = l1 << l2;
        } else if (op == Opcodes.LSHR) {
            result = l1 >> l2;
        } else if (op == Opcodes.LUSHR) {
            result = l1 >>> l2;
        } else if (op == Opcodes.LADD) {
            result = l1 + l2;
        } else if (op == Opcodes.LSUB) {
            result = l1 - l2;
        } else if (op == Opcodes.LMUL) {
            result = l1 * l2;
        } else if (op == Opcodes.LDIV) {
            result = l1 / l2;
        } else {
            return null;
        }

        return List.of(new LdcInsnNode(result));
    }
}
