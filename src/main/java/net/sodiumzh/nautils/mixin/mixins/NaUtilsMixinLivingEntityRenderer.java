package net.sodiumzh.nautils.mixin.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.sodiumzh.nautils.mixin.NaUtilsMixin;
import net.sodiumzh.nautils.mixin.events.client.entity.LivingRendererCheckSitEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntityRenderer.class)
public class NaUtilsMixinLivingEntityRenderer implements NaUtilsMixin<LivingEntityRenderer<?, ?>> {

    @ModifyVariable(method = "render(Lnet/minecraft/world/entity/LivingEntity;FFLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;I)V",
    at = @At("STORE"), ordinal = 0)
    private boolean onCheckSit(boolean original, @Local(argsOnly = true) LivingEntity living) {
        LivingRendererCheckSitEvent event = new LivingRendererCheckSitEvent(living, caller(), original);
        return switch (event.getResult()) {
            case ALLOW -> true;
            case DENY -> false;
            case DEFAULT -> original;
        };
    }

}
