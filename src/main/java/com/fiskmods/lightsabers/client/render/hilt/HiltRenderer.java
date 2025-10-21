package com.fiskmods.lightsabers.client.render.hilt;

import com.fiskmods.lightsabers.Lightsabers;
import com.fiskmods.lightsabers.common.hilt.Hilt;
import com.fiskmods.lightsabers.common.lightsaber.PartType;
import net.minecraft.client.model.Model;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

/**
 * Base renderer for all lightsaber hilts.
 * Modernized for Forge 1.20.1 — removes FiskRegistryEntry dependency.
 */
public abstract class HiltRenderer {
    private static final Map<String, HiltRenderer> REGISTRY = new HashMap<>();

    public static void register(String key, HiltRenderer renderer) {
        REGISTRY.put(key.toLowerCase(), renderer);
    }

    public static void register(Hilt hilt, HiltRenderer renderer) {
        if (hilt != null && hilt.getId() != null) {
            register(hilt.getId().toString(), renderer);
        }
    }

    public static HiltRenderer get(String key) {
        return REGISTRY.get(key.toLowerCase());
    }

    public static HiltRenderer get(Hilt hilt) {
        if (hilt == null || hilt.getId() == null) return null;
        return get(hilt.getId().toString());
    }

    // ===== MODEL ACCESSORS =====
    public abstract Model getEmitter();
    public abstract Model getSwitchSection();
    public abstract Model getBody();
    public abstract Model getPommel();

    public Model getModel(PartType type) {
        return switch (type) {
            case EMITTER -> getEmitter();
            case SWITCH_SECTION -> getSwitchSection();
            case BODY -> getBody();
            case POMMEL -> getPommel();
        };
    }

    public ResourceLocation getTexture(PartType type) {
        return new ResourceLocation(Lightsabers.MODID,
                String.format("textures/models/lightsaber/%s_%s.png",
                        type.textureName,
                        getRegistryName()));
    }

    public String getRegistryName() {
        // Derived key name used in registration
        return REGISTRY.entrySet()
                .stream()
                .filter(e -> e.getValue() == this)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("unknown");
    }

    public final Hilt getHilt() {
        return Hilt.REGISTRY.get(getRegistryName());
    }
}
