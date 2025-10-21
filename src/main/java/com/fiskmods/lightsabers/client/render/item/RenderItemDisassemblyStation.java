package com.fiskmods.lightsabers.client.render.item;

import com.fiskmods.lightsabers.common.tileentity.TileEntityDisassemblyStation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Modern replacement for the old IItemRenderer version.
 * Works in Forge 1.20.1 using BlockEntityWithoutLevelRenderer.
 */
public class RenderItemDisassemblyStation extends BlockEntityWithoutLevelRenderer {

    private final TileEntityDisassemblyStation tile = new TileEntityDisassemblyStation();

    public RenderItemDisassemblyStation() {
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
        float scale = 0.6F;
        poseStack.scale(scale, scale, scale);

        switch (context) {
            case GROUND, GUI -> {
                poseStack.mulPose(net.minecraft.core.Rotations.YP.rotationDegrees(-90));
                poseStack.translate(0.0F, -0.85F, -0.5F);
            }
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                poseStack.mulPose(net.minecraft.core.Rotations.YP.rotationDegrees(180));
                poseStack.translate(-0.5F, 0.0F, -1.5F);
            }
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                poseStack.translate(0.5F, 0.75F, 0.5F);
            }
            default -> { /* no extra transform */ }
        }

        Minecraft.getInstance().getBlockEntityRenderDispatcher()
                .renderItem(tile, poseStack, buffer, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
