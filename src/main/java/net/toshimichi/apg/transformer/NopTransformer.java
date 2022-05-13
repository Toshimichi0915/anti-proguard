package net.toshimichi.apg.transformer;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;

import java.util.List;

public class NopTransformer extends PatternTransformer {
    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public List<? extends AbstractInsnNode> transform(List<? extends AbstractInsnNode> from) {
        if (from.get(0).getOpcode() != Opcodes.NOP) return null;
        return List.of();
    }
}
