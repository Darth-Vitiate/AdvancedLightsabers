package com.fiskmods.lightsabers.client.render.hilt;

import com.fiskmods.lightsabers.common.lightsaber.PartType;
import net.minecraft.client.model.Model;
import net.minecraft.resources.ResourceLocation;

/**
 * Variant hilt renderer for one-piece textures (1:1 ratio style).
 * Updated for Forge 1.20.1 – uses Model instead of ModelBase.
 */
public class HiltRendererOneTwelve extends HiltRendererBase {

    public HiltRendererOneTwelve(Model emitter, Model switchSection, Model body, Model pommel) {
        super(emitter, switchSection, body, pommel);
    }

    @Override
    public ResourceLocation getTexture(PartType type) {
        // Uses a single texture instead of one per part type
        return new ResourceLocation("lightsabers",
                String.format("textures/models/lightsaber/%s.png", getRegistryName()));
    }
}
