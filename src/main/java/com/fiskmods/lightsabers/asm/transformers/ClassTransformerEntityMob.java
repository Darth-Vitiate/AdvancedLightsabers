package com.fiskmods.lightsabers.asm.transformers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fiskmods.lightsabers.asm.ASMHooks;

import java.util.List;

/**
 * Modern replacement for the 1.12.2 EntityMob transformer.
 * Targets {@link net.minecraft.world.entity.monster.Monster}.
 *
 * Function: replaces calls to Entity#attackEntityFrom with ASMHooks.attackEntityFrom
 * to enable custom lightsaber damage logic.
 */
public class ClassTransformerEntityMob extends ClassTransformerBase implements Opcodes {
    private static final Logger LOGGER = LogManager.getLogger("Lightsabers");

    public static String varPlayer;
    public static String varEntity;

    public ClassTransformerEntityMob() {
        super("net.minecraft.world.entity.monster.Monster");
    }

    @Override
    public boolean processMethods(List<MethodNode> methods) {
        boolean modified = false;

        for (MethodNode method : methods) {
            // Target old "attackEntityAsMob" -> now "doHurtTarget" in modern mappings
            if (method.name.equals("doHurtTarget") && method.desc.equals("(Lnet/minecraft/world/entity/Entity;)Z")) {

                InsnList newList = new InsnList();

                for (AbstractInsnNode node : method.instructions) {
                    if (node instanceof MethodInsnNode m) {
                        // Looking for call to Entity#hurt(DamageSource, float)
                        if (m.getOpcode() == INVOKEVIRTUAL
                                && m.name.equals("hurt")
                                && m.desc.equals("(Lnet/minecraft/world/damagesource/DamageSource;F)Z")) {

                            // Replace with static ASMHooks.attackEntityFrom(Entity, DamageSource, float)
                            newList.add(new MethodInsnNode(INVOKESTATIC,
                                    Type.getInternalName(ASMHooks.class),
                                    "attackEntityFrom",
                                    "(Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/damagesource/DamageSource;F)Z",
                                    false));

                            LOGGER.debug("Patched attackEntityFrom() call in {}", unobfClass);
                            modified = true;
                            continue;
                        }
                    }

                    newList.add(node);
                }

                method.instructions.clear();
                method.instructions.add(newList);
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
        // Updated mappings for 1.20.1
        varPlayer = "net/minecraft/world/entity/player/Player";
        varEntity = "net/minecraft/world/entity/Entity";
    }
}
