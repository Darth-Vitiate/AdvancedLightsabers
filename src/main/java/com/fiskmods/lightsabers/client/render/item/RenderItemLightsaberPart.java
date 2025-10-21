package com.fiskmods.lightsabers.client.render.item;

import com.fiskmods.lightsabers.client.render.hilt.HiltRenderer;
import com.fiskmods.lightsabers.common.hilt.Hilt;
import com.fiskmods.lightsabers.common.item.ItemLightsaberPart;
import com.fiskmods.lightsabers.common.lightsaber.PartType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Modern Forge 1.20.1 renderer for Lightsaber Part items.
 * Replaces the legacy IItemRenderer with full PoseStack support.
 */
public class RenderItemLightsaberPart extends BlockEntityWithoutLevelRenderer {

    private final PartType partType;

    public RenderItemLightsaberPart(PartType type) {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
        this.partType = type;
    }

    @Override
    public void renderByItem(ItemStack stack,
                             ItemDisplayContext context,
                             PoseStack poseStack,
                             MultiBufferSource buffer,
                             int packedLight,
                             int packedOverlay) {

        Hilt hilt = ItemLightsaberPart.get(stack);
        HiltRenderer renderer = HiltRenderer.get(hilt);
        if (renderer == null) return;

        // Base scaling/offset
        float scale = 0.4F;
        float height = hilt.getPart(partType).height;
        float offsetY = height * (partType.isLowerPart() ? -1 : 1) / 2 * 0.0625F;

        poseStack.pushPose();
        ResourceLocation tex = renderer.getTexture(partType);
        Minecraft.getInstance().getTextureManager().bindForSetup(tex);

        // Apply transformations based on display context
        switch (context) {
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(-100));
                poseStack.mulPose(Axis.XP.rotationDegrees(-150));
                poseStack.mulPose(Axis.ZP.rotationDegrees(5));
                poseStack.translate(0, 0.15F, 0.9F);
            }
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(-90));
                poseStack.mulPose(Axis.XP.rotationDegrees(-150));
                poseStack.translate(0.1F, 0.15F, 0.475F);
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                poseStack.mulPose(Axis.YP.rotationDegrees(-20));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-10));
            }
            case GROUND -> poseStack.mulPose(Axis.XP.rotationDegrees(180));
            case GUI -> {
                poseStack.mulPose(Axis.ZP.rotationDegrees(-20));
                poseStack.mulPose(Axis.XP.rotationDegrees(-45));
                poseStack.mulPose(Axis.XP.rotationDegrees(180));
                poseStack.translate(0, 0.05F, 0);
                poseStack.mulPose(Axis.YP.rotationDegrees(-110));

                // Scale correction for GUI context
                if (partType == PartType.POMMEL && height <= 4) scale = 2F;
                if (height * scale > 20) scale = 20 / height;
            }
            default -> {}
        }

        // Apply scale and vertical offset
        if (scale != 1) poseStack.scale(scale, scale, scale);
        poseStack.translate(0, offsetY, 0);

        // Render model
        renderer.getModel(partType).renderToBuffer(
                poseStack,
                buffer.getBuffer(net.minecraft.client.renderer.RenderType.entityCutout(tex)),
                packedLight,
                packedOverlay,
                1F, 1F, 1F, 1F
        );

        poseStack.popPose();
    }
}
