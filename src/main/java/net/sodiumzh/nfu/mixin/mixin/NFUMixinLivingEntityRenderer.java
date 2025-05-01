package net.sodiumzh.nfu.mixin.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumzh.nfu.mixin.NFUMixin;
import net.sodiumzh.nfu.mixin.event.client.entity.LivingRendererCheckSitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntityRenderer.class)
public class NFUMixinLivingEntityRenderer implements NFUMixin<LivingEntityRenderer<?, ?>> {

    @ModifyVariable(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
    at = @At("STORE"), ordinal = 0)
    private boolean onCheckSit(boolean original, @Local(argsOnly = true) LivingEntity living, @Local(argsOnly = true) PoseStack poseStack) {
        LivingRendererCheckSitEvent event = new LivingRendererCheckSitEvent(living, caller(), original, poseStack);
        MinecraftForge.EVENT_BUS.post(event);
        return switch (event.getResult()) {
            case ALLOW -> true;
            case DENY -> false;
            case DEFAULT -> original;
        };
    }

}
