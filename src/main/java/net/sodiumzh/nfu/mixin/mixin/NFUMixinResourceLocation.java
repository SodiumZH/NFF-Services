package net.sodiumzh.nfu.mixin.mixin;

import net.minecraft.resources.ResourceLocation;
import net.sodiumzh.nfu.mixin.NFUMixin;
import net.sodiumzh.nfu.savedata.redirector.SaveDataLocationRedirectorEventListeners;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ResourceLocation.class)
public class NFUMixinResourceLocation implements NFUMixin<ResourceLocation> {
    @ModifyVariable(method = "<init>([Ljava/lang/String;)V",
        at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private static String[] doPort(String[] value) {
        return SaveDataLocationRedirectorEventListeners.doPortNamespace(value);
    }
}
