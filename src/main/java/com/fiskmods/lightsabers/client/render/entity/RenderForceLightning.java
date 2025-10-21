package com.fiskmods.lightsabers.client.render.entity;

import com.fiskmods.lightsabers.common.entity.EntityForceLightning;
import com.fiskmods.lightsabers.helper.ALRenderHelper;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RenderForceLightning extends EntityRenderer<EntityForceLightning> {

    private static final ResourceLocation LIGHTNING_TEX =
            new ResourceLocation("textures/entity/lightning_bolt.png");

    public RenderForceLightning(EntityRendererProvider.Context ctx) {
        super(ctx);
        this.shadowRadius = 0.0F;
    }

    @Override
    public void render(EntityForceLightning entity,
                       float entityYaw,
                       float partialTicks,
                       PoseStack poseStack,
                       MultiBufferSource buffer,
                       int packedLight) {

        LivingEntity caster = entity.getCaster();
        if (caster == null || !caster.isAlive()) return;

        Minecraft mc = Minecraft.getInstance();
        Vec3 camera = mc.gameRenderer.getMainCamera().getPosition();
        Vec3 src = caster.getEyePosition(partialTicks);

        poseStack.pushPose();
        poseStack.translate(src.x - camera.x, src.y - camera.y, src.z - camera.z);

        VertexConsumer consumer = buffer.getBuffer(RenderType.lightning());
        Random rand = new Random(caster.tickCount * 100000);
        Vec3 color = new Vec3(0, 0, 1); // Blue lightning

        // placeholder targets (you can implement real targeting logic later)
        List<LivingEntity> targets = Collections.emptyList();

        for (LivingEntity target : targets) {
            Vec3 targetVec = target.getEyePosition(partialTicks);
            drawLightningLine(poseStack, consumer, src, targetVec, color, 0.08F, rand);
        }

        poseStack.popPose();
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    private void drawLightningLine(PoseStack poseStack, VertexConsumer buffer,
                                   Vec3 start, Vec3 end, Vec3 color, float width, Random rand) {
        Vec3 diff = end.subtract(start);
        int segments = 10;
        Vec3 prev = start;

        for (int i = 1; i <= segments; ++i) {
            double t = i / (double) segments;
            Vec3 curr = new Vec3(
                    Mth.lerp(t, start.x, end.x) + (rand.nextDouble() - 0.5) * width,
                    Mth.lerp(t, start.y, end.y) + (rand.nextDouble() - 0.5) * width,
                    Mth.lerp(t, start.z, end.z) + (rand.nextDouble() - 0.5) * width
            );
            ALRenderHelper.drawLightningSegment(poseStack, buffer, prev, curr, color, 0.15F);
            prev = curr;
        }
    }

    @Override
    public ResourceLocation getTextureLocation(EntityForceLightning entity) {
        return LIGHTNING_TEX;
    }
}
