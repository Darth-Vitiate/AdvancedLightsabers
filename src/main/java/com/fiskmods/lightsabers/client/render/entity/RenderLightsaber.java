package com.fiskmods.lightsabers.client.render.entity;

import com.fiskmods.lightsabers.common.entity.EntityLightsaber;
import com.fiskmods.lightsabers.common.item.ItemDoubleLightsaber;
import com.fiskmods.lightsabers.common.item.ModItems;
import com.fiskmods.lightsabers.common.lightsaber.LightsaberData;
import com.fiskmods.lightsabers.helper.ALRenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

/**
 * Modernized RenderLightsaber (Forge 1.20.1+)
 */
public class RenderLightsaber extends EntityRenderer<EntityLightsaber> {

    public RenderLightsaber(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(EntityLightsaber entity,
                       float entityYaw,
                       float partialTicks,
                       PoseStack poseStack,
                       MultiBufferSource buffer,
                       int packedLight) {

        poseStack.pushPose();

        // === transforms ===
        float interpYaw   = Mth.lerp(partialTicks, entity.yRotO, entity.getYRot());
        float interpPitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
        float spin        = (entity.tickCount + partialTicks) * 40F;
        float scale       = 0.2F;

        poseStack.translate(0.0F, 0.03F, 0.0F);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(interpYaw - 90F));
        poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(interpPitch));
        poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(90F));
        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(spin));
        poseStack.scale(scale, scale, scale);

        renderLightsaber(entity, poseStack, buffer, packedLight, spin, partialTicks);
        poseStack.popPose();
    }

    private void renderLightsaber(EntityLightsaber entity,
                                  PoseStack poseStack,
                                  MultiBufferSource buffer,
                                  int packedLight,
                                  float spin,
                                  float partialTicks) {

        ItemStack stack = entity.getItem();
        if (stack.isEmpty()) return;

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(getTextureLocation(entity)));

        if (stack.getItem() == ModItems.doubleLightsaber.get()) {
            if (entity.data == null) entity.data = ItemDoubleLightsaber.get(stack);
            ALRenderHelper.renderLightsaber((LightsaberData[]) entity.data, stack, poseStack, consumer, packedLight, true);
        } else {
            if (entity.data == null) entity.data = LightsaberData.get(stack);
            ALRenderHelper.renderLightsaber((LightsaberData) entity.data, stack, poseStack, consumer, packedLight, true);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(EntityLightsaber entity) {
        return new ResourceLocation("lightsabers", "textures/entity/lightsaber.png");
    }
}
