package com.fiskmods.lightsabers.asm.transformers;

import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fiskmods.lightsabers.asm.ASMHooksClient;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Modern replacement for IClassTransformer.
 * This can be registered and called from your ModLauncher TransformationService.
 */
public class ClassTransformerColor implements Opcodes {
    private static final Logger LOGGER = LogManager.getLogger("Lightsabers");

    public ClassTransformerColor() {}

    /**
     * Called manually from your TransformationService when a target class is being transformed.
     *
     * @param className The class name being transformed (e.g., net.minecraft.client.renderer.GameRenderer)
     * @param bytes     Original class bytes
     * @return Modified class bytes
     */
    public byte[] transform(String className, byte[] bytes) {
        try {
            ClassReader reader = new ClassReader(bytes);
            ClassNode node = new ClassNode();
            reader.accept(node, 0);

            boolean success = processMethods(node.methods);

            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            node.accept(writer);

            if (success) {
                LOGGER.debug("Patched OpenGL color calls in {}", className);
            }

            // Optional: output debug .class file
            // writeClassFile(writer, className.replace('.', '_'));
            return writer.toByteArray();
        } catch (Exception e) {
            LOGGER.error("Error transforming {}: {}", className, e);
            e.printStackTrace();
        }

        return bytes;
    }

    public boolean processMethods(List<MethodNode> methods) {
        boolean modified = false;

        for (MethodNode method : methods) {
            InsnList newList = new InsnList();

            for (int j = 0; j < method.instructions.size(); ++j) {
                AbstractInsnNode node = method.instructions.get(j);

                if (node instanceof MethodInsnNode methodNode) {
                    if (node.getOpcode() == INVOKESTATIC
                            && "org/lwjgl/opengl/GL11".equals(methodNode.owner)
                            && (methodNode.name.startsWith("glColor3") || methodNode.name.startsWith("glColor4"))) {

                        // Replace static GL11 call with our custom hook
                        newList.add(new MethodInsnNode(INVOKESTATIC,
                                Type.getInternalName(ASMHooksClient.class),
                                methodNode.name,
                                methodNode.desc,
                                false));
                        modified = true;
                        continue;
                    }
                }

                newList.add(node);
            }

            method.instructions.clear();
            method.instructions.add(newList);
        }

        return modified;
    }

    public static void writeClassFile(ClassWriter cw, String name) {
        try {
            File outDir = new File("debug/glColor/");
            outDir.mkdirs();
            File outFile = new File(outDir, name + ".class");
            try (DataOutputStream dout = new DataOutputStream(new FileOutputStream(outFile))) {
                dout.write(cw.toByteArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
