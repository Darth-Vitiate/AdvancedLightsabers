package com.fiskmods.lightsabers.client.render.item;

import com.fiskmods.lightsabers.common.tileentity.TileEntityLightsaberStand;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Modern Forge 1.20.1 renderer for the Lightsaber Stand item.
 * Replaces legacy IItemRenderer + GL11 pipeline.
 */
public class RenderItemLightsaberStand extends BlockEntityWithoutLevelRenderer {

    private final TileEntityLightsaberStand tile = new TileEntityLightsaberStand();

    public RenderItemLightsaberStand() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(ItemStack stack,
                             ItemDisplayContext context,
                             PoseStack poseStack,
                             MultiBufferSource buffer,
                             int packedLight,
                             int packedOverlay) {
        poseStack.pushPose();

        float scale = 2.0F;

        switch (context) {
            case GUI -> {
                poseStack.scale(scale, scale, scale);
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                poseStack.translate(-0.5F, -0.125F, -0.5F);
            }
            case GROUND -> {
                poseStack.scale(scale, scale, scale);
                poseStack.translate(-0.5F, 0.0F, -0.5F);
            }
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND,
                 FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                poseStack.translate(-0.5F, 0.25F, -0.5F);
                poseStack.scale(scale, scale, scale);
            }
            default -> poseStack.scale(scale, scale, scale);
        }

        try {
            Minecraft.getInstance().getBlockEntityRenderDispatcher()
                    .renderItem(tile, poseStack, buffer, packedLight, packedOverlay);
        } catch (Exception e) {
            e.printStackTrace();
        }

        poseStack.popPose();
    }
}
