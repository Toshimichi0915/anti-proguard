package net.toshimichi.apg.transformer;

import net.toshimichi.apg.utils.OpcodeUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnNode;

import java.util.List;

public class IntTransformer extends PatternTransformer {

    @Override
    public int getSize() {
        return 3;
    }

    @Override
    public List<? extends AbstractInsnNode> transform(List<? extends AbstractInsnNode> from) {
        AbstractInsnNode i1 = from.get(0);
        AbstractInsnNode i2 = from.get(1);
        AbstractInsnNode i3 = from.get(2);
        Long l1 = OpcodeUtils.toLong(i1);
        if (l1 == null) return null;
        Long l2 = OpcodeUtils.toLong(i2);
        if (l2 == null) return null;
        if (!(i3 instanceof InsnNode)) return null;

        int op = i3.getOpcode();

        long result;
        if (op == Opcodes.IAND) {
            result = l1 & l2;
        } else if (op == Opcodes.IXOR) {
            result = l1 | l2;
        } else if (op == Opcodes.ISHL) {
            result = l1 << l2;
        } else if (op == Opcodes.ISHR) {
            result = l1 >> l2;
        } else if (op == Opcodes.IADD) {
            result = l1 + l2;
        } else if (op == Opcodes.ISUB) {
            result = l1 - l2;
        } else if (op == Opcodes.IMUL) {
            result = l1 * l2;
        } else if (op == Opcodes.IDIV) {
            result = l1 / l2;
        } else {
            return null;
        }

        return List.of(OpcodeUtils.fromLong(i1.getOpcode(), result));
    }
}
