package com.fiskmods.lightsabers.client.render.tile;

import com.fiskmods.lightsabers.client.model.tile.ModelCrystal;
import com.fiskmods.lightsabers.common.tileentity.TileEntityCrystal;
import com.fiskmods.lightsabers.helper.ALRenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.systems.RenderSystem;

import java.util.Random;

public class RenderCrystal implements BlockEntityRenderer<TileEntityCrystal> {

    private final ModelCrystal model = new ModelCrystal();

    public RenderCrystal(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(TileEntityCrystal tile, float partialTicks, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();

        poseStack.translate(0.5F, 1.5F, 0.5F);
        poseStack.scale(1F, -1F, -1F);

        float[] rgb = tile.getColor().getRGB();
        float alpha = 0.6F;

        // Setup luminous look
        ALRenderHelper.setLighting(ALRenderHelper.LIGHTING_LUMINOUS);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();

        // Render the crystal using vertex buffer system
        model.renderToBuffer(poseStack,
                buffer.getBuffer(RenderType.translucent()),
                packedLight,
                packedOverlay,
                rgb[0], rgb[1], rgb[2], alpha);

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        ALRenderHelper.resetLighting();

        poseStack.popPose();
    }

    // Random rotation/position adjustment (if needed in render)
    public static void adjustRotation(PoseStack poseStack, TileEntityCrystal tile, int metadata) {
        if (metadata > 0 && metadata < 5) {
            int[] matrix = {0, 2, 1, 3};
            poseStack.mulPose(Axis.YP.rotationDegrees(matrix[metadata - 1] * 90));
            poseStack.translate(1, 1, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90));
        }

        if (metadata == 0) {
            poseStack.translate(0, 2, 0);
            poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        }

        Random rand = new Random(tile.getBlockPos().getX() + tile.getBlockPos().getY() + tile.getBlockPos().getZ());
        poseStack.mulPose(Axis.YP.rotationDegrees(rand.nextInt(360)));
        poseStack.translate(0, rand.nextInt(10) / 40F, 0);
    }
}
