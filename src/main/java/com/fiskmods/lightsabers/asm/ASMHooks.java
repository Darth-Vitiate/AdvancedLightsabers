package com.fiskmods.lightsabers.asm;

import com.fiskmods.lightsabers.common.damage.ALDamageSources;
import com.fiskmods.lightsabers.common.item.ItemLightsaber;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/**
 * ASM hook used by transformers to override vanilla damage behavior.
 *
 * In Forge 1.20.1+, EntityLivingBase → LivingEntity
 * and attackEntityFrom() → hurt()
 */
public class ASMHooks {

    public static boolean attackEntityFrom(Entity entity, DamageSource source, float damage) {
        if (source.getEntity() instanceof LivingEntity attacker) {
            if (attacker.getMainHandItem() != null && attacker.getMainHandItem().getItem() instanceof ItemLightsaber) {
                return entity.hurt(ALDamageSources.causeLightsaberDamage(attacker), damage);
            }
        }

        return entity.hurt(source, damage);
    }
}
