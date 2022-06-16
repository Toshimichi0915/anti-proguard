package net.toshimichi.apg.transformer;

import net.toshimichi.apg.utils.OpcodeUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

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
        if (i2.getOpcode() != Opcodes.INEG && i2.getOpcode() != Opcodes.LNEG) return null;
        boolean lneg = i1 instanceof LdcInsnNode lin1 && lin1.cst instanceof Long && i2.getOpcode() == Opcodes.LNEG;

        return List.of(new LdcInsnNode(lneg ? -l : (int) -l));
    }
}
