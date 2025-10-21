package com.fiskmods.lightsabers.client.render.item;

import com.fiskmods.lightsabers.common.item.ItemCrystal;
import com.fiskmods.lightsabers.common.tileentity.TileEntityCrystal;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Modern replacement for the old IItemRenderer version.
 * Works in Forge 1.20.1+ using BlockEntityWithoutLevelRenderer.
 */
public class RenderItemCrystal extends BlockEntityWithoutLevelRenderer {

    private final TileEntityCrystal tile = new TileEntityCrystal();

    public RenderItemCrystal() {
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

        // Apply scale / rotation depending on render context
        poseStack.pushPose();
        float scale = 2.5F;

        switch (context) {
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                poseStack.translate(-0.5F, 0.5F, -0.5F);
            }
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                poseStack.mulPose(net.minecraft.util.Mth.YP.rotationDegrees(20));
                poseStack.mulPose(net.minecraft.util.Mth.XP.rotationDegrees(15));
                poseStack.translate(-0.275F, -0.05F, -0.85F);
                scale /= 1.75F;
            }
            case GROUND -> poseStack.translate(-1.25F, -0.5F, -1.25F);
            case GUI -> {
                poseStack.mulPose(net.minecraft.util.Mth.YP.rotationDegrees(180));
                poseStack.translate(-0.5F, -1F, -0.5F);
            }
            default -> {}
        }

        poseStack.scale(scale, scale, scale);

        // Set crystal color
        tile.setColor(ItemCrystal.get(stack));

        // Render the block entity directly
        Minecraft.getInstance().getBlockEntityRenderDispatcher()
                .renderItem(tile, poseStack, buffer, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
