package net.toshimichi.apg.transformer;

import net.toshimichi.apg.utils.OpcodeUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

import java.util.ArrayList;

public class StringTransformer implements ClassTransformer {

    // 1MB Array Limit
    private static final int ARRAY_SIZE_LIMIT = 1_000_000;

    @Override
    public boolean transform(String path, ClassNode cn) {
        ArrayList<AbstractInsnNode> removed = new ArrayList<>();
        for (MethodNode mn : cn.methods) {
            for (int i = 0; i < mn.instructions.size(); i++) {
                removed.clear();

                // SIPUSH l
                // NEWARRAY B
                // ASTORE storeTo
                AbstractInsnNode ain0 = mn.instructions.get(i);
                if (ain0 == null) continue;
                Long l = OpcodeUtils.toLong(ain0);
                if (l == null) continue;
                if (l > ARRAY_SIZE_LIMIT) continue;
                removed.add(ain0);

                AbstractInsnNode ain1 = ain0.getNext();
                if (ain1 == null) continue;
                if (ain1.getOpcode() != Opcodes.NEWARRAY) continue;
                if (!(ain1 instanceof IntInsnNode iin0)) continue;
                if (iin0.operand != Opcodes.T_BYTE) continue;
                removed.add(ain1);

                AbstractInsnNode ain2 = ain1.getNext();
                if (ain2 == null) continue;
                if (ain2.getOpcode() != Opcodes.ASTORE) continue;
                if (!(ain2 instanceof VarInsnNode iin1)) continue;
                removed.add(ain2);

                byte[] raw = new byte[(int) (long) l];
                int storeTo = iin1.var;

                AbstractInsnNode cursor = ain2.getNext();

                // ALOAD storeTo
                // SIPUSH index
                // SIPUSH value
                // BASTORE
                while (true) {

                    if (cursor == null) break;
                    if (cursor.getOpcode() != Opcodes.ALOAD) break;
                    if (!(cursor instanceof VarInsnNode iin)) continue;
                    if (iin.var != storeTo) break;
                    removed.add(cursor);

                    AbstractInsnNode si0 = cursor.getNext();
                    if (si0 == null) break;
                    Long index = OpcodeUtils.toLong(si0);
                    if (index == null) break;
                    if (index >= l) break;
                    removed.add(si0);

                    AbstractInsnNode si1 = si0.getNext();
                    if (si1 == null) break;
                    Long value = OpcodeUtils.toLong(si1);
                    if (value == null) break;
                    removed.add(si1);

                    AbstractInsnNode ba = si1.getNext();
                    if (ba == null) break;
                    if (ba.getOpcode() != Opcodes.BASTORE) break;
                    removed.add(ba);

                    raw[(int) (long) index] = (byte) (long) value;
                    cursor = ba.getNext();
                }

                // NEW java/lang/String
                // DUP
                // ALOAD 2
                // INVOKESPECIAL java/lang/String.<init>([B)V
                if (cursor == null) continue;
                if (cursor.getOpcode() != Opcodes.NEW) continue;
                if (!(cursor instanceof TypeInsnNode cln)) continue;
                if (!cln.desc.equals("java/lang/String")) continue;
                removed.add(cursor);

                AbstractInsnNode ain4 = cursor.getNext();
                if (ain4 == null) continue;
                if (ain4.getOpcode() != Opcodes.DUP) continue;
                removed.add(ain4);

                AbstractInsnNode ain5 = ain4.getNext();
                if (ain5 == null) continue;
                if (ain5.getOpcode() != Opcodes.ALOAD) continue;
                if (!(ain5 instanceof VarInsnNode iin2)) continue;
                if (iin2.var != storeTo) continue;
                removed.add(ain5);

                AbstractInsnNode ain6 = ain5.getNext();
                if (ain6 == null) continue;
                if (ain6.getOpcode() != Opcodes.INVOKESPECIAL) continue;
                if (!(ain6 instanceof MethodInsnNode min0)) continue;
                if (!(min0.owner + "." + min0.name + min0.desc).equals("java/lang/String.<init>([B)V")) continue;
                removed.add(ain6);

                mn.instructions.insertBefore(ain0, new LdcInsnNode(new String(raw)));
                removed.forEach(mn.instructions::remove);
                return true;
            }
        }
        return false;
    }
}
