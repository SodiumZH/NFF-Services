package net.sodiumzh.nfu.mixin.mixin;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.IModStateTransition;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.registries.GameData;
import net.sodiumzh.nfu.mixin.NFUMixin;
import net.sodiumzh.nfu.registry.RegistryObjectPreConstruction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.sql.Time;
import java.util.stream.Stream;

@Mixin(GameData.class)
public class NFUMixinGameData implements NFUMixin<GameData> {

    @Inject(method = "generateRegistryEvents()Ljava/util/stream/Stream;", remap = false,
    at = @At("HEAD"))
    private static void doPreConstruction(CallbackInfoReturnable<Stream<IModStateTransition.EventGenerator<?>>> cir) {
        long time = System.currentTimeMillis();
        ModLoader.get().postEvent(new RegistryObjectPreConstruction.SetupEvent());
        RegistryObjectPreConstruction.Impl.doPreConstruction();
        long cost = System.currentTimeMillis() - time;
        LogUtils.getLogger().info(String.format("NFU Forge registry object pre-construction completed within %d ms.", cost));
    }

}
