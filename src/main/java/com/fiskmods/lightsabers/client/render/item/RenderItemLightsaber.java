package com.fiskmods.lightsabers.client.render.item;

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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import com.mojang.math.Axis;

/**
 * Modern Forge 1.20.1 renderer for the standard lightsaber item.
 * Fully replaces legacy IItemRenderer + GL11 pipeline.
 */
public class RenderItemLightsaber extends BlockEntityWithoutLevelRenderer {

    public RenderItemLightsaber() {
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

        LightsaberData data = LightsaberData.get(stack);
        poseStack.pushPose();

        switch (context) {

            // ---------- FIRST PERSON ----------
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                poseStack.mulPose(Axis.YP.rotationDegrees(-100));
                poseStack.mulPose(Axis.XP.rotationDegrees(-150));
                poseStack.mulPose(Axis.ZP.rotationDegrees(5));
                poseStack.translate(0, 0.275F, 0.85F);
                poseStack.scale(0.2F, 0.2F, 0.2F);
                ALRenderHelper.renderLightsaber(stack, false);
            }

            // ---------- THIRD PERSON ----------
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                poseStack.translate(0.7F, 0.3F, 0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(-150));
                poseStack.mulPose(Axis.YP.rotationDegrees(-85));

                Minecraft mc = Minecraft.getInstance();
                if (mc.player instanceof LivingEntity living)
                    ModelHelper.applyLightsaberItemRotation(living, stack);

                poseStack.scale(0.175F, 0.175F, 0.175F);
                ALRenderHelper.renderLightsaber(data, stack, true);
            }

            // ---------- GROUND / ENTITY ----------
            case GROUND -> {
                poseStack.mulPose(Axis.XP.rotationDegrees(180));
                poseStack.mulPose(Axis.YP.rotationDegrees(180));

                float scale = 0.3F;
                poseStack.scale(scale, scale, scale);

                if (stack.hasCustomHoverName()) {
                    String name = stack.getHoverName().getString();
                    if ("Dinnerbone".equals(name) || "Grumm".equals(name))
                        poseStack.mulPose(Axis.XP.rotationDegrees(180));
                }

                ALRenderHelper.renderLightsaberHilt(data);
            }

            // ---------- GUI / INVENTORY ----------
            case GUI -> renderInventoryIcon(data, stack, poseStack, buffer);

            // ---------- FALLBACK ----------
            default -> {
                poseStack.scale(0.2F, 0.2F, 0.2F);
                ALRenderHelper.renderLightsaber(data, stack, true);
            }
        }

        poseStack.popPose();
    }

    /** Draws the inventory-triangle color preview like the original Tessellator-based version. */
    private void renderInventoryIcon(LightsaberData data,
                                     ItemStack stack,
                                     PoseStack poseStack,
                                     MultiBufferSource buffer) {

        poseStack.pushPose();
        float[] rgb = data.getRGB(stack);
        float triangle = 4F;

        var builder = buffer.getBuffer(RenderType.solid());
        var tesselator = Tesselator.getInstance();

        // base colored quad
        drawQuad(poseStack, builder, 0, 0, triangle, triangle, rgb);

        // inverting crystal overlay
        if (data.hasFocusingCrystal(FocusingCrystal.INVERTING)) {
            float[] black = {0, 0, 0};
            float small = triangle / 1.5F;
            poseStack.pushPose();
            poseStack.translate(small / 8, small / 8, 0);
            drawQuad(poseStack, builder, 0, 0, small, small, black);
            poseStack.popPose();
        }

        // 3D hilt miniature render
        poseStack.translate(-2, 3, 0);
        poseStack.scale(10, 10, 10);
        poseStack.translate(1, 0.5F, 1);
        poseStack.scale(1, 1, -1);
        poseStack.mulPose(Axis.XP.rotationDegrees(210));
        poseStack.mulPose(Axis.YP.rotationDegrees(-45));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-20));
        poseStack.mulPose(Axis.XP.rotationDegrees(-45));
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.mulPose(Axis.YP.rotationDegrees(-110));

        float scale = 0.5F;
        poseStack.scale(scale, scale, scale);
        ALRenderHelper.renderLightsaberHilt(data);

        poseStack.popPose();
    }

    private void drawQuad(PoseStack stack,
                          VertexConsumer vc,
                          float x, float y, float w, float h,
                          float[] color) {
        vc.vertex(stack.last().pose(), x, y, 0).color(color[0], color[1], color[2], 1F).endVertex();
        vc.vertex(stack.last().pose(), x + w, y, 0).color(color[0], color[1], color[2], 1F).endVertex();
        vc.vertex(stack.last().pose(), x + w, y + h, 0).color(color[0], color[1], color[2], 1F).endVertex();
        vc.vertex(stack.last().pose(), x, y + h, 0).color(color[0], color[1], color[2], 1F).endVertex();
    }
}
