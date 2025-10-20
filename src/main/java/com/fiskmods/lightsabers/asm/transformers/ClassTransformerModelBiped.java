package com.fiskmods.lightsabers.asm.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fiskmods.lightsabers.helper.ModelHelper;

import java.util.List;

/**
 * Modernized version of ClassTransformerModelBiped for Minecraft 1.20.1+.
 *
 * Target: net.minecraft.client.model.HumanoidModel
 * Injects ModelHelper.renderBipedPre/Post calls around HumanoidModel.renderToBuffer().
 */
public class ClassTransformerModelBiped extends ClassTransformerBase implements Opcodes {
    private static final Logger LOGGER = LogManager.getLogger("Lightsabers");

    public static String varPlayer;
    public static String varEntity;

    public ClassTransformerModelBiped() {
        super("net.minecraft.client.model.HumanoidModel");
    }

    @Override
    public boolean processMethods(List<MethodNode> methods) {
        boolean modified = false;

        for (MethodNode method : methods) {

            // Target HumanoidModel.renderToBuffer(PoseStack, VertexConsumer, int, int, float, float, float, float)
            if (method.name.equals("renderToBuffer") &&
                    method.desc.equals("(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V")) {

                InsnList newList = new InsnList();

                for (AbstractInsnNode node : method.instructions) {
                    // Insert pre-hook right before the main rendering occurs
                    if (node instanceof MethodInsnNode m &&
                            m.owner.contains("net/minecraft/client/model/geom/ModelPart") &&
                            m.name.equals("render")) {

                        // Add original call first
                        newList.add(node);

                        // Inject ModelHelper.renderBipedPre()
                        newList.add(new VarInsnNode(ALOAD, 0)); // this
                        newList.add(new VarInsnNode(ALOAD, 1)); // PoseStack
                        newList.add(new VarInsnNode(ALOAD, 2)); // VertexConsumer
                        newList.add(new VarInsnNode(ILOAD, 3)); // packedLight
                        newList.add(new VarInsnNode(ILOAD, 4)); // packedOverlay
                        newList.add(new VarInsnNode(FLOAD, 5)); // red
                        newList.add(new VarInsnNode(FLOAD, 6)); // green
                        newList.add(new VarInsnNode(FLOAD, 7)); // blue
                        newList.add(new VarInsnNode(FLOAD, 8)); // alpha

                        newList.add(new MethodInsnNode(INVOKESTATIC,
                                Type.getInternalName(ModelHelper.class),
                                "renderBipedPre",
                                "(Lnet/minecraft/client/model/HumanoidModel;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V",
                                false));

                        continue;
                    }

                    // Inject post-hook right before RETURN
                    if (node.getOpcode() == RETURN) {
                        newList.add(new VarInsnNode(ALOAD, 0));
                        newList.add(new VarInsnNode(ALOAD, 1));
                        newList.add(new VarInsnNode(ALOAD, 2));
                        newList.add(new VarInsnNode(ILOAD, 3));
                        newList.add(new VarInsnNode(ILOAD, 4));
                        newList.add(new VarInsnNode(FLOAD, 5));
                        newList.add(new VarInsnNode(FLOAD, 6));
                        newList.add(new VarInsnNode(FLOAD, 7));
                        newList.add(new VarInsnNode(FLOAD, 8));

                        newList.add(new MethodInsnNode(INVOKESTATIC,
                                Type.getInternalName(ModelHelper.class),
                                "renderBipedPost",
                                "(Lnet/minecraft/client/model/HumanoidModel;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;IIFFFF)V",
                                false));
                    }

                    newList.add(node);
                }

                method.instructions.clear();
                method.instructions.add(newList);
                LOGGER.debug("Injected ModelHelper.renderBipedPre/Post in {}", unobfClass);
                modified = true;
            }
        }

        return modified;
    }

    @Override
    public boolean processFields(List<FieldNode> fields) {
        return true;
    }

    @Override
    public void setupMappings() {
        varPlayer = "net/minecraft/world/entity/player/Player";
        varEntity = "net/minecraft/world/entity/Entity";
    }
}
