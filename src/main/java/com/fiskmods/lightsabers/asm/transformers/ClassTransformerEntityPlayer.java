package com.fiskmods.lightsabers.asm.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fiskmods.lightsabers.asm.ASMHooks;

import java.util.List;

/**
 * Modernized version of the old EntityPlayer transformer.
 *
 * Target: net.minecraft.world.entity.player.Player
 * Purpose: replace calls to Entity.hurt(DamageSource, float)
 *          with ASMHooks.attackEntityFrom(Entity, DamageSource, float)
 *          inside Player.attack(Entity).
 */
public class ClassTransformerEntityPlayer extends ClassTransformerBase implements Opcodes {
    private static final Logger LOGGER = LogManager.getLogger("Lightsabers");

    public static String varPlayer;
    public static String varEntity;

    public ClassTransformerEntityPlayer() {
        super("net.minecraft.world.entity.player.Player");
    }

    @Override
    public boolean processMethods(List<MethodNode> methods) {
        boolean modified = false;

        for (MethodNode method : methods) {
            // In modern versions, attackTargetEntityWithCurrentItem() → attack(Lnet/minecraft/world/entity/Entity;)V
            if (method.name.equals("attack") && method.desc.equals("(Lnet/minecraft/world/entity/Entity;)V")) {

                InsnList newInstructions = new InsnList();

                for (AbstractInsnNode node : method.instructions) {
                    if (node instanceof MethodInsnNode m) {
                        // Replacing Entity.hurt(DamageSource, float) with ASMHooks.attackEntityFrom(Entity, DamageSource, float)
                        if (m.getOpcode() == INVOKEVIRTUAL
                                && m.owner.equals("net/minecraft/world/entity/Entity")
                                && m.name.equals("hurt")
                                && m.desc.equals("(Lnet/minecraft/world/damagesource/DamageSource;F)Z")) {

                            newInstructions.add(new MethodInsnNode(INVOKESTATIC,
                                    Type.getInternalName(ASMHooks.class),
                                    "attackEntityFrom",
                                    "(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
                                    false));

                            LOGGER.debug("Replaced hurt() call with ASMHooks.attackEntityFrom() in {}", unobfClass);
                            modified = true;
                            continue;
                        }
                    }

                    newInstructions.add(node);
                }

                method.instructions.clear();
                method.instructions.add(newInstructions);
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
        // Dev names; obfuscated mappings not needed in 1.20+ development
        varPlayer = "net/minecraft/world/entity/player/Player";
        varEntity = "net/minecraft/world/entity/Entity";
    }
}
