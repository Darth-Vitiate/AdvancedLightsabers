package com.fiskmods.lightsabers.asm;

import java.util.Iterator;
import java.util.List;

import com.fiskmods.lightsabers.client.sound.MovingSoundHum;
import com.fiskmods.lightsabers.helper.ALRenderHelper;
import com.google.common.collect.Lists;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.world.entity.Entity;

import com.mojang.blaze3d.systems.RenderSystem;

/**
 * Modernized client-side ASM hooks for Forge 1.20.1+.
 * <p>
 * Handles color overrides and lightsaber hum sound tracking.
 */
public class ASMHooksClient {
    private static final Minecraft MC = Minecraft.getInstance();
    public static final List<String> humSounds = Lists.newArrayList();

    /**
     * Updates the internal hum sound cache.
     * In modern Forge, SoundManager → SoundEngine.
     */
    public static void updateAllSounds(SoundEngine soundEngine, List<SoundInstance> list) {
        Iterator<SoundInstance> iterator = list.iterator();
        humSounds.clear();

        while (iterator.hasNext()) {
            SoundInstance instance = iterator.next();
            if (instance instanceof MovingSoundHum sound) {
                if (sound.theEntity != null && sound.theEntity.isAlive()) {
                    humSounds.add(sound.theEntity.getUUID().toString());
                }
            }
        }
    }

    /**
     * Checks if the given entity currently has an active lightsaber hum.
     */
    public static boolean hasHumSound(Entity entity) {
        return humSounds.contains(entity.getUUID().toString());
    }

    // -------------------------------------------------------------------------
    //  Color Override Hooks (RenderSystem-safe replacements)
    // -------------------------------------------------------------------------

    public static void glColor3f(float r, float g, float b) {
        if (!ALRenderHelper.overrideColor) {
            RenderSystem.setShaderColor(r, g, b, 1.0F);
        }
    }

    public static void glColor4f(float r, float g, float b, float a) {
        if (!ALRenderHelper.overrideColor) {
            RenderSystem.setShaderColor(r, g, b, a);
        }
    }

    // Other glColor variants are now redirected to RenderSystem.setShaderColor().
    // Mojang’s modern pipeline handles float precision automatically.
    public static void glColor3b(byte r, byte g, byte b) {
        if (!ALRenderHelper.overrideColor) {
            RenderSystem.setShaderColor((r & 0xFF) / 255F, (g & 0xFF) / 255F, (b & 0xFF) / 255F, 1.0F);
        }
    }

    public static void glColor3d(double r, double g, double b) {
        if (!ALRenderHelper.overrideColor) {
            RenderSystem.setShaderColor((float) r, (float) g, (float) b, 1.0F);
        }
    }

    public static void glColor4d(double r, double g, double b, double a) {
        if (!ALRenderHelper.overrideColor) {
            RenderSystem.setShaderColor((float) r, (float) g, (float) b, (float) a);
        }
    }

    public static void glColor4b(byte r, byte g, byte b, byte a) {
        if (!ALRenderHelper.overrideColor) {
            RenderSystem.setShaderColor((r & 0xFF) / 255F, (g & 0xFF) / 255F, (b & 0xFF) / 255F, (a & 0xFF) / 255F);
        }
    }

    public static void glColor3ub(byte r, byte g, byte b) {
        if (!ALRenderHelper.overrideColor) {
            RenderSystem.setShaderColor((r & 0xFF) / 255F, (g & 0xFF) / 255F, (b & 0xFF) / 255F, 1.0F);
        }
    }

    public static void glColor4ub(byte r, byte g, byte b, byte a) {
        if (!ALRenderHelper.overrideColor) {
            RenderSystem.setShaderColor((r & 0xFF) / 255F, (g & 0xFF) / 255F, (b & 0xFF) / 255F, (a & 0xFF) / 255F);
        }
    }
}
