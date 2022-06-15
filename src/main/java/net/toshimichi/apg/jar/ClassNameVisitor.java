package net.toshimichi.apg.jar;

import lombok.SneakyThrows;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.ClassNode;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;

public class ClassNameVisitor extends JarVisitor {

    private static final String[] SIGNALS = "ణణవౄధనరఫఫభౄఫఫళౄువుళృఫహణబఱహెఫేఱ".split("");
    private static final char[] REPLACED_BY = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    private final HashMap<String, String> mappings = new HashMap<>();
    private final ArrayDeque<Runnable> exec = new ArrayDeque<>();
    private int index = 0;

    public ClassNameVisitor(JarVisitor jv) {
        super(jv);
    }

    public ClassNameVisitor() {
    }

    private String newClassName() {
        int current = ++index;
        StringBuilder builder = new StringBuilder();
        while (current > 0) {
            builder.append(REPLACED_BY[current % REPLACED_BY.length]);
            current /= REPLACED_BY.length;
        }
        return builder.toString();
    }

    @Override
    public void visitSource(String path, ClassNode cn) {
        boolean isObfuscated = false;
        for (String signal : SIGNALS) {
            if (path.contains(signal)) {
                isObfuscated = true;
                break;
            }
        }

        if (isObfuscated) {
            String newClassName = newClassName();

            mappings.put(cn.name, newClassName);
            exec.add(() -> {
                SimpleRemapper simpleRemapper = new SimpleRemapper(mappings);
                ClassNode newClassNode = new ClassNode();

                ClassRemapper classRemapper = new ClassRemapper(newClassNode, simpleRemapper);
                cn.accept(classRemapper);

                String newPath = path.replace(cn.name, newClassName);
                super.visitSource(newPath, newClassNode);
            });
        } else {
            exec.add(() -> {
                SimpleRemapper simpleRemapper = new SimpleRemapper(mappings);
                ClassNode newClassNode = new ClassNode();

                ClassRemapper classRemapper = new ClassRemapper(newClassNode, simpleRemapper);
                cn.accept(classRemapper);

                super.visitSource(path, newClassNode);
            });
        }
    }

    @SneakyThrows
    @Override
    public void visitEnd() {
        exec.forEach(Runnable::run);
        exec.clear();

        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : mappings.entrySet()) {
            builder.append(entry.getKey() + " " + entry.getValue() + "\n");
        }
        Files.writeString(Path.of("build/mappings.txt"), builder.toString());
        mappings.clear();
    }
}
