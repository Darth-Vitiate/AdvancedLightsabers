package com.fiskmods.lightsabers.client.render.item;

import com.fiskmods.lightsabers.common.tileentity.TileEntityHolocron;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import com.mojang.math.Axis;

/**
 * Modern Forge 1.20.1 replacement for the old IItemRenderer Holocron renderer.
 */
public class RenderItemHolocron extends BlockEntityWithoutLevelRenderer {

    private final TileEntityHolocron tile = new TileEntityHolocron();

    public RenderItemHolocron() {
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

        // Apply translation based on the display context (old ItemRenderType)
        switch (context) {
            case GROUND -> poseStack.translate(-0.5F, -0.25F, -0.5F);
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND ->
                    poseStack.translate(0.25F, 0.25F, 0.25F);
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND ->
                    poseStack.translate(0.25F, 0.25F, 0.25F);
            case GUI -> { /* skip inventory render, same as old code */ }
            default -> { }
        }

        // Set old "metadata" as item damage or custom model index
        tile.setVariant(stack.getDamageValue()); // <-- replace with your own setter if needed

        // Render the holocron block entity
        Minecraft.getInstance().getBlockEntityRenderDispatcher()
                .renderItem(tile, poseStack, buffer, packedLight, packedOverlay);

        poseStack.popPose();
    }
}
