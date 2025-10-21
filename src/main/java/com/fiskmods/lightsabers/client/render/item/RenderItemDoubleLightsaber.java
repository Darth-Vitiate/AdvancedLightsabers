package com.fiskmods.lightsabers.client.render.item;

import com.fiskmods.lightsabers.common.item.ItemDoubleLightsaber;
import com.fiskmods.lightsabers.common.lightsaber.FocusingCrystal;
import com.fiskmods.lightsabers.common.lightsaber.LightsaberData;
import com.fiskmods.lightsabers.helper.ALRenderHelper;
import com.fiskmods.lightsabers.helper.ModelHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tesselator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import com.mojang.math.Axis;

public class RenderItemDoubleLightsaber extends BlockEntityWithoutLevelRenderer {

    public RenderItemDoubleLightsaber() {
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

        LightsaberData[] array = ItemDoubleLightsaber.get(stack);
        poseStack.pushPose();

        switch (context) {

            // ---------- First Person ----------
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(-100));
                poseStack.mulPose(Axis.XP.rotationDegrees(-150));
                poseStack.mulPose(Axis.ZP.rotationDegrees(95));
                poseStack.mulPose(Axis.ZP.rotationDegrees(180));
                poseStack.translate(-0.1F, -0.2F, 1.1F);

                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null) {
                    Player player = mc.player;
                    float f = player.walkDistO - (player.walkDistO - player.walkDist) * Minecraft.getInstance().getFrameTime();
                    float f1 = Mth.cos((player.walkDist - player.walkDistO * (1 - Minecraft.getInstance().getFrameTime())) * 0.6662F) * 1.4F * f;
                    float f2 = player.getAttackAnim(Minecraft.getInstance().getFrameTime());
                    float f3 = (f2 > 0.5F ? 1 - f2 : f2) * 2;
                    poseStack.mulPose(Axis.ZP.rotationDegrees(90 * f));
                    poseStack.translate(0.2F * f3 + 0.8F * f, 0.5F * f3, 0.4F * f);
                    poseStack.mulPose(Axis.XP.rotationDegrees(30 * f3));
                    poseStack.mulPose(Axis.ZP.rotationDegrees(360 * f2));
                }

                poseStack.scale(0.2F, 0.2F, 0.2F);
                ALRenderHelper.renderLightsaber(array, stack, true);
            }

            // ---------- Third Person ----------
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(-90));
                poseStack.mulPose(Axis.XP.rotationDegrees(-150));
                poseStack.translate(-0.15F + LightsaberData.getHeight(stack) * 0.0015F, 0.14F, 0.75F);

                if (Minecraft.getInstance().player instanceof LivingEntity living)
                    ModelHelper.applyLightsaberItemRotation(living, stack);

                poseStack.scale(0.175F, 0.175F, 0.175F);
                ALRenderHelper.renderLightsaber(array, stack, true);
            }

            // ---------- Entity (Ground / Dropped) ----------
            case GROUND -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(180));
                poseStack.mulPose(Axis.YP.rotationDegrees(180));

                float scale = 0.3F;
                poseStack.scale(scale, scale, scale);
                ALRenderHelper.renderLightsaberHilt(array);
            }

            // ---------- GUI ----------
            case GUI -> renderInventoryIcon(array, stack, poseStack, buffer);

            default -> {
                // Other contexts (HEAD, FIXED, etc.)
                poseStack.scale(0.2F, 0.2F, 0.2F);
                ALRenderHelper.renderLightsaber(array, stack, true);
            }
        }

        poseStack.popPose();
    }

    /** Draws inventory triangles to mimic original colored crystal icon. */
    private void renderInventoryIcon(LightsaberData[] array,
                                     ItemStack stack,
                                     PoseStack poseStack,
                                     MultiBufferSource buffer) {

        poseStack.pushPose();
        float[] rgb1 = array[0].getRGB(stack);
        float[] rgb2 = array[1].getRGB(stack);

        // Use immediate buffer for simple colored triangles
        var immediate = Tesselator.getInstance().getBuilder();
        immediate.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        VertexConsumer vc = buffer.getBuffer(RenderType.solid());

        // First triangle
        drawTriangle(poseStack, vc, 0, 0, 4, 0, 2, 2, rgb1);
        // Second triangle
        drawTriangle(poseStack, vc, 0, 0, 0, 4, 2, 2, rgb2);

        // Optional inverting crystals (black)
        float[] black = {0, 0, 0};
        if (array[0].hasFocusingCrystal(FocusingCrystal.INVERTING))
            drawTriangle(poseStack, vc, 0, 0, 4, 0, 2, 2, black);
        if (array[1].hasFocusingCrystal(FocusingCrystal.INVERTING))
            drawTriangle(poseStack, vc, 0, 0, 0, 4, 2, 2, black);

        poseStack.popPose();
    }

    private void drawTriangle(PoseStack stack, VertexConsumer vc,
                              float x1, float y1, float x2, float y2, float x3, float y3, float[] color) {
        vc.vertex(stack.last().pose(), x1, y1, 0).color(color[0], color[1], color[2], 1.0F).endVertex();
        vc.vertex(stack.last().pose(), x2, y2, 0).color(color[0], color[1], color[2], 1.0F).endVertex();
        vc.vertex(stack.last().pose(), x3, y3, 0).color(color[0], color[1], color[2], 1.0F).endVertex();
    }
}
