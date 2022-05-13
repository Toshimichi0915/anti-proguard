package net.toshimichi.apg.transformer;

import net.toshimichi.apg.jar.JarVisitor;
import org.objectweb.asm.tree.ClassNode;

import java.util.List;

public class TransformerVisitor extends JarVisitor {

    private final List<? extends ClassTransformer> transformers;

    public TransformerVisitor(JarVisitor jv, List<? extends ClassTransformer> transformers) {
        super(jv);
        this.transformers = List.copyOf(transformers);
    }

    @Override
    public void visitSource(String path, ClassNode cn) {

        while (true) {
            boolean changed = false;
            for (ClassTransformer transformer : transformers) {
                changed |= transformer.transform(path, cn);
            }

            if (!changed) break;
        }

        super.visitSource(path, cn);
    }
}
