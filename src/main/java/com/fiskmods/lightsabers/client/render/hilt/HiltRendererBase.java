package com.fiskmods.lightsabers.client.render.hilt;

import net.minecraft.client.model.Model;

/**
 * Simple container implementation for a lightsaber hilt renderer.
 * Modern Forge 1.20.1 version — replaces ModelBase with Model.
 */
public class HiltRendererBase extends HiltRenderer {
    public final Model emitter;
    public final Model switchSection;
    public final Model body;
    public final Model pommel;

    public HiltRendererBase(Model emitter, Model switchSection, Model body, Model pommel) {
        this.emitter = emitter;
        this.switchSection = switchSection;
        this.body = body;
        this.pommel = pommel;
    }

    @Override
    public Model getEmitter() {
        return emitter;
    }

    @Override
    public Model getSwitchSection() {
        return switchSection;
    }

    @Override
    public Model getBody() {
        return body;
    }

    @Override
    public Model getPommel() {
        return pommel;
    }
}
