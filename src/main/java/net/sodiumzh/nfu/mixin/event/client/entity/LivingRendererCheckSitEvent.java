package net.sodiumzh.nfu.mixin.event.client.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumzh.nfu.event.NFULivingEvent;

/**
 * Posted on {@link LivingEntityRenderer} checking if the mob should be on the sit pose.
 * <p>{@code HasResult}. {@code ALLOW} = always sitting. {@code DEFAULT} = use original value.
 * {@code DENY} = always standing.
 */
@OnlyIn(Dist.CLIENT)
@Event.HasResult
public class LivingRendererCheckSitEvent extends NFULivingEvent<LivingEntity> {

    private final LivingEntityRenderer<?, ?> renderer;
    private final boolean originalSit;
    private final PoseStack poseStack;

    public LivingRendererCheckSitEvent(LivingEntity entity, LivingEntityRenderer<?, ?> renderer, boolean originalSit, PoseStack poseStack) {
        super(entity);
        this.renderer = renderer;
        this.originalSit = originalSit;
        this.poseStack = poseStack;
    }

    public LivingEntityRenderer<?, ?> getRenderer() {
        return renderer;
    }

    public boolean isOriginalSit() {
        return originalSit;
    }

    public PoseStack getPoseStack() {
        return poseStack;
    }
}
