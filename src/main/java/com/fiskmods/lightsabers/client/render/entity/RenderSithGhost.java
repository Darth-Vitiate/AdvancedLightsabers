package com.fiskmods.lightsabers.client.render.entity;

import com.fiskmods.lightsabers.Lightsabers;
import com.fiskmods.lightsabers.client.model.ModelSithGhost;
import com.fiskmods.lightsabers.common.entity.EntitySithGhost;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.resources.ResourceLocation;

/**
 * Modernized Sith Ghost renderer (Forge 1.20.1+)
 */
public class RenderSithGhost extends HumanoidMobRenderer<EntitySithGhost, HumanoidModel<EntitySithGhost>> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(Lightsabers.MODID, "textures/models/sith_ghost.png");

    public RenderSithGhost(EntityRendererProvider.Context context) {
        // ModelSithGhost must extend HumanoidModel<EntitySithGhost>
        super(context, new ModelSithGhost(context.bakeLayer(ModelSithGhost.LAYER_LOCATION)), 0.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(EntitySithGhost entity) {
        return TEXTURE;
    }
}
