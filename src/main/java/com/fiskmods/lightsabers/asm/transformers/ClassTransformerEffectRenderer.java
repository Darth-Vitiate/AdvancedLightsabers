package com.fiskmods.lightsabers.asm.transformers;

import com.fiskmods.lightsabers.client.particle.ALParticles;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.util.List;

/**
 * Modern equivalent of the 1.12.2 EffectRenderer transformer.
 * Works as a drop-in for use inside your ModLauncher TransformationService.
 *
 * Target class in 1.20.1: net.minecraft.client.particle.ParticleEngine
 */
public class ClassTransformerEffectRenderer extends ClassTransformerBase {
    private static final Logger LOGGER = LogManager.getLogger("Lightsabers");

    public static String varPlayer;
    public static String varEntity;

    public ClassTransformerEffectRenderer() {
        super("net.minecraft.client.particle.ParticleEngine");
    }

    public static int plus(int i) {
        ++i;
        if (i == 3) ++i;
        return i;
    }

    @Override
    public boolean processMethods(List<MethodNode> methods) {
        boolean modified = false;

        for (MethodNode method : methods) {

            // --- Handle constructor (setup mappings) ---
            if (method.name.equals("<init>")) {
                LOGGER.debug("Skipping constructor transformation for {}", unobfClass);
                continue;
            }

            // --- Replace ICONST_3 / ICONST_4 with ALParticles.fxLayersSize ---
            InsnList updatedInstructions = new InsnList();
            boolean replacedConstants = false;

            for (AbstractInsnNode node : method.instructions) {
                if (node instanceof InsnNode insn) {
                    if (insn.getOpcode() == ICONST_3 || insn.getOpcode() == ICONST_4) {
                        updatedInstructions.add(new FieldInsnNode(
                                GETSTATIC,
                                Type.getInternalName(ALParticles.class),
                                "fxLayersSize",
                                "I"
                        ));
                        replacedConstants = true;
                        continue;
                    }
                }
                updatedInstructions.add(node);
            }

            if (replacedConstants) {
                method.instructions.clear();
                method.instructions.add(updatedInstructions);
                modified = true;
            }

            // --- Handle bindParticleTextures injection ---
            if (method.name.equals("render")) { // New name in 1.20.1
                injectParticleBinding(method);
                modified = true;
            }

            // --- Handle getStatistics() patch (if exists) ---
            if (method.name.equals("getStatistics") && method.desc.equals("()Ljava/lang/String;")) {
                rewriteStatisticsMethod(method);
                modified = true;
            }
        }

        return modified;
    }

    /**
     * Injects ALParticles.bindParticleTextures(layer) after Tessellator.startDrawingQuads().
     */
    private void injectParticleBinding(MethodNode method) {
        InsnList newList = new InsnList();
        int currentLine = 0;

        for (AbstractInsnNode node : method.instructions) {
            if (node instanceof LineNumberNode lineNode)
                currentLine = lineNode.line;

            if (node instanceof MethodInsnNode m
                    && m.getOpcode() == INVOKEVIRTUAL
                    && m.owner.equals("com/mojang/blaze3d/vertex/Tesselator")
                    && m.name.equals("begin")) {
                // Inject custom texture binding right after tessellator.begin()
                newList.add(node);
                LabelNode labelNode = new LabelNode();
                newList.add(labelNode);
                newList.add(new LineNumberNode(currentLine + 1, labelNode));
                newList.add(new VarInsnNode(ILOAD, 9)); // layer index variable
                newList.add(new MethodInsnNode(INVOKESTATIC,
                        Type.getInternalName(ALParticles.class),
                        "bindParticleTextures",
                        "(I)V",
                        false));
                continue;
            }

            if (node instanceof IincInsnNode iinc
                    && iinc.var == 8 && iinc.incr == 1) {
                // Replace i++ with i = plus(i)
                newList.add(new VarInsnNode(ILOAD, 8));
                newList.add(new MethodInsnNode(INVOKESTATIC,
                        Type.getInternalName(ClassTransformerEffectRenderer.class),
                        "plus",
                        "(I)I",
                        false));
                newList.add(new VarInsnNode(ISTORE, 8));
                continue;
            }

            newList.add(node);
        }

        method.instructions.clear();
        method.instructions.add(newList);
        LOGGER.debug("Injected ALParticles.bindParticleTextures() into {}", method.name);
    }

    /**
     * Rewrites getStatistics() to include ALParticles.getParticlesInWorld().
     */
    private void rewriteStatisticsMethod(MethodNode method) {
        InsnList newList = new InsnList();
        boolean beforeReturn = true;

        for (AbstractInsnNode node : method.instructions) {
            if (node.getOpcode() == ARETURN) beforeReturn = false;

            if (beforeReturn && node instanceof TypeInsnNode type
                    && node.getOpcode() == NEW
                    && type.desc.equals("java/lang/StringBuilder")) {
                newList.add(new VarInsnNode(ALOAD, 0));
                newList.add(new FieldInsnNode(
                        GETFIELD,
                        "net/minecraft/client/particle/ParticleEngine",
                        "particles",
                        "[Ljava/util/List;"
                ));
                newList.add(new MethodInsnNode(INVOKESTATIC,
                        Type.getInternalName(ALParticles.class),
                        "getParticlesInWorld",
                        "([Ljava/util/List;)I",
                        false));
                newList.add(new MethodInsnNode(INVOKESTATIC,
                        Type.getInternalName(String.class),
                        "valueOf",
                        "(I)Ljava/lang/String;",
                        false));
                continue;
            }

            newList.add(node);
        }

        method.instructions.clear();
        method.instructions.add(newList);
        method.visitMaxs(1, 1);

        LOGGER.debug("Rewrote getStatistics() for {}", unobfClass);
    }

    @Override
    public boolean processFields(List<FieldNode> fields) {
        return true;
    }

    @Override
    public void setupMappings() {
        // Class names are unobfuscated in dev
        varPlayer = "net/minecraft/world/entity/player/Player";
        varEntity = "net/minecraft/world/entity/Entity";
    }
}
