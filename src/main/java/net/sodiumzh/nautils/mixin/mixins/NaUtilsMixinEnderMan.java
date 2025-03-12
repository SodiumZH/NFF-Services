package net.sodiumzh.nautils.mixin.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumzh.nautils.mixin.NaUtilsMixin;
import net.sodiumzh.nautils.mixin.event.entity.EnderManStartSettingTargetEvent;
import org.checkerframework.checker.units.qual.A;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderMan.class)
public class NaUtilsMixinEnderMan implements NaUtilsMixin<EnderMan> {

    @Inject(method = "setTarget(Lnet/minecraft/world/entity/LivingEntity;)V",
    at = @At("HEAD"), cancellable = true)
    private void startSettingTarget(LivingEntity ori, CallbackInfo ci, @Local(argsOnly = true) LocalRef<LivingEntity> target){
        EnderManStartSettingTargetEvent event = new EnderManStartSettingTargetEvent(caller(), ori);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) ci.cancel();
        else target.set(event.getNewTarget());
    }

}
