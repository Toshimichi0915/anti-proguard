package net.toshimichi.apg.transformer;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.Base64;
import java.util.List;

public class FuDecodeTransformer extends PatternTransformer {

    private static final String METHOD = "FU.decode(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;";

    @Override
    public int getSize() {
        return 3;
    }

    public static String decode(String string, String string2) {
        return new String(xorWithKey(Base64.getDecoder().decode(string), string2.getBytes()));
    }

    public static byte[] xorWithKey(byte[] byArray, byte[] byArray2) {
        byte[] byArray3 = new byte[byArray.length];
        for (int i = 0; i < byArray.length; ++i) {
            byArray3[i] = (byte) (byArray[i] ^ byArray2[i % byArray2.length]);
        }
        return byArray3;
    }

    @Override
    public List<? extends AbstractInsnNode> transform(List<? extends AbstractInsnNode> from) {
        AbstractInsnNode ain0 = from.get(0);
        AbstractInsnNode ain1 = from.get(1);
        AbstractInsnNode ain2 = from.get(2);
        if (!(ain0 instanceof LdcInsnNode ldc0)) return null;
        if (!(ain1 instanceof LdcInsnNode ldc1)) return null;
        if (!(ain2 instanceof MethodInsnNode min0)) return null;
        if (!(ldc0.cst instanceof String s0)) return null;
        if (!(ldc1.cst instanceof String s1)) return null;
        if (!(min0.owner + "." + min0.name + min0.desc).equals(METHOD)) return null;

        return List.of(new LdcInsnNode(decode(s0, s1)));
    }
}
