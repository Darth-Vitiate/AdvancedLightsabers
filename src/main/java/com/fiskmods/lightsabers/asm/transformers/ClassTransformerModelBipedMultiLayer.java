package com.fiskmods.lightsabers.asm.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fiskmods.lightsabers.helper.ModelHelper;

import java.util.List;

/**
 * Modernized transformer for custom model class ModelBipedMultiLayer.
 *
 * Injects ModelHelper.renderBipedPre/Post into render() method
 * for multi-layer humanoid rendering, matching Forge 1.20.1+ API.
 */
public class ClassTransformerModelBipedMultiLayer extends ClassTransformerBase implements Opcodes {
    private static final Logger LOGGER = LogManager.getLogger("Lightsabers");

    public static String varPlayer;
    public static String varEntity;

    public ClassTransformerModelBipedMultiLayer() {
        super("fiskfille.heroes.client.model.ModelBipedMultiLayer");
    }

    @Override
    public boolean processMethods(List<MethodNode> methods) {
        boolean modified = false;

        for (MethodNode method : methods) {
            // Target your custom ModelBipedMultiLayer#render method
            if (method.name.equals("render") &&
                    method.desc.equals("(Lnet/minecraft/world/entity/Entity;FFFFFF)V")) {

                InsnList newList = new InsnList();

                for (AbstractInsnNode node : method.instructions) {

                    // When the original renderBipedPre is called, follow it with our own call to ModelHelper.renderBipedPre
                    if (node instanceof MethodInsnNode m &&
                            m.name.equals("renderBipedPre") &&
                            m.desc.equals("(Lnet/minecraft/client/model/HumanoidModel;Lnet/minecraft/world/entity/Entity;FFFFFF)V")) {

                        newList.add(node);
                        newList.add(new VarInsnNode(ALOAD, 0)); // this
                        newList.add(new VarInsnNode(ALOAD, 1)); // entity
                        newList.add(new VarInsnNode(FLOAD, 2)); // limbSwing
                        newList.add(new VarInsnNode(FLOAD, 3)); // limbSwingAmount
                        newList.add(new VarInsnNode(FLOAD, 4)); // ageInTicks
                        newList.add(new VarInsnNode(FLOAD, 5)); // netHeadYaw
                        newList.add(new VarInsnNode(FLOAD, 6)); // headPitch
                        newList.add(new VarInsnNode(FLOAD, 7)); // scale
                        newList.add(new MethodInsnNode(INVOKESTATIC,
                                Type.getInternalName(ModelHelper.class),
                                "renderBipedPre",
                                "(Lnet/minecraft/client/model/HumanoidModel;Lnet/minecraft/world/entity/Entity;FFFFFF)V",
                                false));
                        continue;
                    }

                    // Inject ModelHelper.renderBipedPost before RETURN
                    if (node.getOpcode() == RETURN) {
                        newList.add(new VarInsnNode(ALOAD, 0));
                        newList.add(new VarInsnNode(ALOAD, 1));
                        newList.add(new VarInsnNode(FLOAD, 2));
                        newList.add(new VarInsnNode(FLOAD, 3));
                        newList.add(new VarInsnNode(FLOAD, 4));
                        newList.add(new VarInsnNode(FLOAD, 5));
                        newList.add(new VarInsnNode(FLOAD, 6));
                        newList.add(new VarInsnNode(FLOAD, 7));
                        newList.add(new MethodInsnNode(INVOKESTATIC,
                                Type.getInternalName(ModelHelper.class),
                                "renderBipedPost",
                                "(Lnet/minecraft/client/model/HumanoidModel;Lnet/minecraft/world/entity/Entity;FFFFFF)V",
                                false));
                    }

                    newList.add(node);
                }

                method.instructions.clear();
                method.instructions.add(newList);
                modified = true;
                LOGGER.debug("Injected ModelHelper.renderBipedPre/Post into {}", unobfClass);
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
