package net.toshimichi.apg.utils;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

public class OpcodeUtils {

    public static Long toLong(AbstractInsnNode i) {
        int op = i.getOpcode();
        if (op == Opcodes.ICONST_0) {
            return 0L;
        } else if (op == Opcodes.ICONST_1) {
            return 1L;
        } else if (op == Opcodes.ICONST_2) {
            return 2L;
        } else if (op == Opcodes.ICONST_3) {
            return 3L;
        } else if (op == Opcodes.ICONST_4) {
            return 4L;
        } else if (op == Opcodes.ICONST_5) {
            return 5L;
        } else if (op == Opcodes.ICONST_M1) {
            return -1L;
        } else if (i instanceof IntInsnNode ii && (op == Opcodes.BIPUSH || op == Opcodes.SIPUSH)) {
            return (long) ii.operand;
        } else if (i instanceof LdcInsnNode l) {
            if (l.cst instanceof Long ll) {
                return ll;
            } else if (l.cst instanceof Integer ii) {
                return (long) ii;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static AbstractInsnNode fromLong(int opcode, long result) {
        if (opcode == Opcodes.BIPUSH) {
            return new IntInsnNode(Opcodes.BIPUSH, (byte) result);
        } else if (opcode == Opcodes.SIPUSH) {
            return new IntInsnNode(Opcodes.SIPUSH, (short) result);
        } else if (opcode == Opcodes.LDC) {
            return new IntInsnNode(Opcodes.SIPUSH, (int) result);
        } else {
            throw new IllegalArgumentException("Invalid opcode: " + opcode);
        }
    }
}
