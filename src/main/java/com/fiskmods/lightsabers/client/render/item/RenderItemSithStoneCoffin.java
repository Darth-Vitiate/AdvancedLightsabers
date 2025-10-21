package com.fiskmods.lightsabers.client.render.item;

import com.fiskmods.lightsabers.common.tileentity.TileEntitySithStoneCoffin;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Modern Forge 1.20.1 renderer for the Sith Stone Coffin item.
 * Replaces legacy IItemRenderer and GL11 calls with PoseStack transforms.
 */
public class RenderItemSithStoneCoffin extends BlockEntityWithoutLevelRenderer {

    private final TileEntitySithStoneCoffin tile = new TileEntitySithStoneCoffin();

    public RenderItemSithStoneCoffin() {
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
                poseStack.translate(-0.5F, -1.0F, -0.5F);
            }
            case GROUND -> {
                poseStack.translate(-0.5F, -1.0F, -0.5F);
            }
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND,
                 FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                poseStack.translate(0.5F, 0.0F, 0.5F);
            }
            default -> {
                // keep default orientation for other display types
            }
        }

        // Match the original orientation (none of the legacy code rotated it)
        Minecraft.getInstance().getBlockEntityRenderDispatcher()
                .renderItem(tile, poseStack, buffer, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
