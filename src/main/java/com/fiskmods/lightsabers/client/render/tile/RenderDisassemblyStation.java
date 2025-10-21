package com.fiskmods.lightsabers.client.render.tile;

import com.fiskmods.lightsabers.Lightsabers;
import com.fiskmods.lightsabers.client.model.tile.ModelDisassemblyStation;
import com.fiskmods.lightsabers.common.tileentity.TileEntityDisassemblyStation;
import com.fiskmods.lightsabers.helper.ALRenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class RenderDisassemblyStation implements BlockEntityRenderer<TileEntityDisassemblyStation> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(Lightsabers.MODID, "textures/models/disassembly_station.png");
    private static final ResourceLocation TEXTURE_LIGHTS =
            new ResourceLocation(Lightsabers.MODID, "textures/models/disassembly_station_lights.png");

    private final ModelDisassemblyStation model = new ModelDisassemblyStation();

    public RenderDisassemblyStation(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(TileEntityDisassemblyStation tile, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {

        poseStack.pushPose();

        poseStack.translate(0.5F, 1.5F, 0.5F);
        poseStack.scale(1F, -1F, -1F);

        // Optional rotation logic
        // int rotation = BlockDisassemblyStation.getRotation(tile.getBlockState().getValue(BlockDisassemblyStation.FACING));
        // poseStack.mulPose(Axis.YP.rotationDegrees(rotation * 90F + 180F));

        poseStack.translate(0.5F, 0, 0);

        // Render base texture
        model.renderToBuffer(poseStack,
                buffer.getBuffer(RenderType.entityCutout(TEXTURE)),
                packedLight,
                packedOverlay,
                1, 1, 1, 1);

        // Render lights overlay
        ALRenderHelper.setLighting(ALRenderHelper.LIGHTING_LUMINOUS);
        model.renderToBuffer(poseStack,
                buffer.getBuffer(RenderType.entityTranslucent(TEXTURE_LIGHTS)),
                packedLight,
                packedOverlay,
                1, 1, 1, 1);
        ALRenderHelper.resetLighting();

        poseStack.popPose();
    }
}
