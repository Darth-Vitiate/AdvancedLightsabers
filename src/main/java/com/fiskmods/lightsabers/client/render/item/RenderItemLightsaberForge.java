package com.fiskmods.lightsabers.client.render.item;

import com.fiskmods.lightsabers.common.tileentity.TileEntityLightsaberForge;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * Modern Forge 1.20.1 renderer for the Lightsaber Forge item.
 * Replaces legacy IItemRenderer + GL11 code.
 */
public class RenderItemLightsaberForge extends BlockEntityWithoutLevelRenderer {

    private final TileEntityLightsaberForge tile = new TileEntityLightsaberForge();

    public RenderItemLightsaberForge() {
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

        // --- uniform scale ---
        float scale = 0.65F;
        poseStack.scale(scale, scale, scale);

        // --- apply transforms per display context ---
        switch (context) {
            case GROUND, GUI -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(-90));
                poseStack.translate(0.0F, -0.75F, -0.5F);
            }
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                poseStack.translate(-0.5F, 0.0F, -1.5F);
            }
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                poseStack.translate(0.5F, 0.75F, 0.5F);
            }
            default -> { /* leave default transforms */ }
        }

        // Optional: if your TileEntityForge still stores the block type
        tile.setBlockState(stack.getItem().getDefaultInstance().getItem().getDefaultInstance().getItem().builtInRegistryHolder().value().defaultBlockState());

        // Render the block-entity model
        Minecraft.getInstance().getBlockEntityRenderDispatcher()
                .renderItem(tile, poseStack, buffer, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
