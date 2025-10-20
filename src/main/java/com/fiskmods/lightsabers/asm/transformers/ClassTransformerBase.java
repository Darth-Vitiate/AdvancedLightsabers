package com.fiskmods.lightsabers.asm.transformers;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Custom base transformer compatible with modern Forge/ModLauncher (1.20.1+)
 * This does NOT use LaunchWrapper or IClassTransformer.
 * Instead, you call transform(...) manually from your TransformationService or ModLauncher hook.
 */
public abstract class ClassTransformerBase implements Opcodes {
    public static final Logger LOGGER = LogManager.getLogger("Lightsabers");

    protected final String classPath;
    protected final String unobfClass;

    public ClassTransformerBase(String path) {
        this.classPath = path;
        this.unobfClass = path.substring(path.lastIndexOf('.') + 1);
    }

    /**
     * Entry point called by your TransformationService or loader.
     */
    public byte[] transform(String name, byte[] bytes) {
        try {
            if (shouldTransform(name)) {
                LOGGER.info("Patching Class {} ({})", unobfClass, name);

                ClassReader reader = new ClassReader(bytes);
                ClassNode node = new ClassNode();
                reader.accept(node, 0);

                setupMappings();
                boolean success = processFields(node.fields) && processMethods(node.methods);
                addInterface(node.interfaces);

                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
                node.accept(writer);

                if (success) {
                    LOGGER.debug("Patching Class {} done", unobfClass);
                } else {
                    LOGGER.error("Patching Class {} FAILED!", unobfClass);
                }

                writeClassFile(writer, unobfClass + "_DEBUG");
                return writer.toByteArray();
            }
        } catch (Exception e) {
            LOGGER.error("Error transforming {}: {}", unobfClass, e);
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * Override to define which classes this transformer applies to.
     */
    protected boolean shouldTransform(String name) {
        return name.replace('/', '.').equals(classPath);
    }

    public void addInterface(List<String> interfaces) {}

    public abstract boolean processMethods(List<MethodNode> methods);

    public abstract boolean processFields(List<FieldNode> fields);

    public abstract void setupMappings();

    public void sendPatchLog(String method) {
        LOGGER.log(Level.INFO, "  Patching method {} in {}", method, unobfClass);
    }

    public static void writeClassFile(ClassWriter cw, String name) {
        try {
            File outDir = new File("debug/");
            outDir.mkdirs();
            File file = new File(outDir, name + ".class");
            try (DataOutputStream dout = new DataOutputStream(new FileOutputStream(file))) {
                dout.write(cw.toByteArray());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getClassName(String className) {
        return "net/minecraft/" + className.replace(".", "/");
    }
}
