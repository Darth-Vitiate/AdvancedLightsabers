package com.fiskmods.lightsabers.client.particle;

import com.fiskmods.lightsabers.Lightsabers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class ALParticles {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Lightsabers.MODID, "textures/particle/particles.png");
    private static final Minecraft MC = Minecraft.getInstance();

    /** Max particle render layers (not used by modern engine, kept for compatibility) */
    public static final int FX_LAYERS_SIZE = 16;

    /**
     * Spawns a custom particle if conditions are met.
     * In 1.20.1, this must be done using ParticleEngine and registered ParticleOptions / Providers.
     */
    public static Particle spawnParticle(ParticleProvider<?> provider, double x, double y, double z, double motionX, double motionY, double motionZ) {
        if (MC == null || MC.level == null || MC.player == null) {
            return null;
        }

        ClientLevel world = MC.level;
        ParticleEngine engine = MC.particleEngine;

        // Optional distance check (matches old 16-block rule)
        Vec3 camPos = MC.gameRenderer.getMainCamera().getPosition();
        double dx = camPos.x - x;
        double dy = camPos.y - y;
        double dz = camPos.z - z;
        double maxDistSq = 16.0D * 16.0D;

        if (dx * dx + dy * dy + dz * dz > maxDistSq) {
            return null;
        }

        // Modern particle creation (the provider is usually registered during client setup)
        try {
            Particle p = engine.createParticle(
                    (net.minecraft.core.particles.ParticleOptions) provider,
                    x, y, z, motionX, motionY, motionZ
            );
            return p;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Returns a total particle count (debug / dev use only). */
    public static int getParticlesInWorld() {
        if (MC.particleEngine == null) return 0;
        // In modern Minecraft, particles are stored in internal lists — not directly accessible.
        // This function can’t iterate old List[] like before, so we return 0 or stub.
        return 0;
    }

    /** Binds the particle texture. Typically only used in manual render layers. */
    public static void bindParticleTextures() {
        MC.getTextureManager().bindForSetup(TEXTURE);
    }
}
