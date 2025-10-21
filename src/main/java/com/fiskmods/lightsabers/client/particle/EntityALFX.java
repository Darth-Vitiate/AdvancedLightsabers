package com.fiskmods.lightsabers.client.particle;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.SimpleParticleType;

/**
 * Modernized base particle for Advanced Lightsabers (1.20.1+)
 *
 * The old 1.12.2 system used World + integer layers;
 * modern particles extend TextureSheetParticle and return a ParticleRenderType.
 */
public class EntityALFX extends TextureSheetParticle {

    protected final SpriteSet sprites;

    // Main constructor (world + position + motion + sprite set)
    protected EntityALFX(ClientLevel level, double x, double y, double z,
                         double motionX, double motionY, double motionZ,
                         SpriteSet spriteSet) {
        super(level, x, y, z, motionX, motionY, motionZ);
        this.sprites = spriteSet;
        this.setSpriteFromAge(spriteSet);   // updates sprite frame each tick if animated
    }

    // Optional simple constructor if you don’t need motion
    protected EntityALFX(ClientLevel level, double x, double y, double z, SpriteSet spriteSet) {
        this(level, x, y, z, 0, 0, 0, spriteSet);
    }

    @Override
    public ParticleRenderType getRenderType() {
        // Layer 9 in old code meant a custom texture;
        // this corresponds to PARTICLE_SHEET_TRANSLUCENT for custom alpha textures.
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    /** Modern equivalent of setParticleTextureIndex */
    public void setTextureIndex(int index) {
        TextureAtlasSprite sprite = this.sprites.get(index, 16); // 16x16 atlas grid
        this.setSprite(sprite);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.sprites);
    }

    /** Provider registration for Forge 1.20.1 */
    public static class Factory implements net.minecraft.client.particle.ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        public Factory(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType type, ClientLevel level,
                                       double x, double y, double z,
                                       double motionX, double motionY, double motionZ) {
            return new EntityALFX(level, x, y, z, motionX, motionY, motionZ, spriteSet);
        }
    }
}
