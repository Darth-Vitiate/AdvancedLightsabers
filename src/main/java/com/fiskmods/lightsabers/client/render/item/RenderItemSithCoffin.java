package com.fiskmods.lightsabers.client.render.item;

import com.fiskmods.lightsabers.common.tileentity.TileEntitySithCoffin;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Modern Forge 1.20.1 renderer for the Sith Coffin item.
 * Replaces legacy IItemRenderer + GL11 pipeline.
 */
public class RenderItemSithCoffin extends BlockEntityWithoutLevelRenderer {

    private final TileEntitySithCoffin tile = new TileEntitySithCoffin();

    public RenderItemSithCoffin() {
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
            case GUI -> {
                poseStack.translate(0.5F, -0.8F, 1.0F);
            }
            case GROUND -> {
                poseStack.translate(0.5F, -0.8F, 1.0F);
            }
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND,
                 FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                poseStack.translate(1.5F, 0.0F, 1.5F);
            }
            default -> { /* leave as is */ }
        }

        // Flip 180° around Y to match the original render
        poseStack.mulPose(Axis.YP.rotationDegrees(180));

        // Render the tile entity model
        Minecraft.getInstance().getBlockEntityRenderDispatcher()
                .renderItem(tile, poseStack, buffer, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
