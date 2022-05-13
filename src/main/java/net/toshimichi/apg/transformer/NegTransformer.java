package net.toshimichi.apg.transformer;

import net.toshimichi.apg.utils.OpcodeUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.util.List;

public class NegTransformer extends PatternTransformer {

    @Override
    public int getSize() {
        return 2;
    }

    @Override
    public List<? extends AbstractInsnNode> transform(List<? extends AbstractInsnNode> from) {
        AbstractInsnNode i1 = from.get(0);
        AbstractInsnNode i2 = from.get(1);

        Long l = OpcodeUtils.toLong(i1);
        if (l == null) return null;
        if (i2.getOpcode() != Opcodes.INEG) return null;

        return List.of(OpcodeUtils.fromLong(i1.getOpcode(), -l));
    }
}
