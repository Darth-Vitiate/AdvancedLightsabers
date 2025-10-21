package com.fiskmods.lightsabers.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * Healing-style particle for Advanced Lightsabers.
 * Modern replacement for the 1.12.2 EntityALHealFX.
 */
public class EntityALHealFX extends TextureSheetParticle {

    private final SpriteSet sprites;
    private final float initialScale;

    protected EntityALHealFX(ClientLevel level,
                             double x, double y, double z,
                             double motionX, double motionY, double motionZ,
                             SpriteSet spriteSet) {
        super(level, x, y, z, motionX, motionY, motionZ);
        this.sprites = spriteSet;

        this.initialScale = this.quadSize;            // starting particle scale
        this.rCol = this.gCol = this.bCol = 1.0F;     // white glow
        this.lifetime = (int)(10.0D / (this.random.nextDouble() * 0.25D + 0.75D)) + 10;

        this.setSpriteFromAge(spriteSet);
        this.hasPhysics = true;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.yd += 0.001F;                      // gentle upward motion

        if (this.age++ >= this.lifetime) {
            this.remove();                      // expire when age exceeds lifetime
        }

        this.move(this.xd, this.yd, this.zd);   // apply motion
        this.xd *= 0.96D;
        this.yd *= 0.96D;
        this.zd *= 0.96D;

        if (this.onGround) {                    // damping on contact
            this.xd *= 0.7D;
            this.zd *= 0.7D;
        }

        // shrink slightly with age
        float progress = (float)this.age / (float)this.lifetime;
        this.quadSize = this.initialScale * (1.0F - progress * progress * 0.5F);
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT; // glowing / alpha-based
    }

    /** Always render bright white like the old getBrightnessForRender */
    @Override
    protected int getLightColor(float partialTicks) {
        return 0xF000F0; // 15728880 decimal
    }

    /** Provider for registration in the particle engine */
    public static class Factory implements net.minecraft.client.particle.ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type,
                                       ClientLevel level,
                                       double x, double y, double z,
                                       double motionX, double motionY, double motionZ) {
            return new EntityALHealFX(level, x, y, z, motionX, motionY, motionZ, spriteSet);
        }
    }
}
