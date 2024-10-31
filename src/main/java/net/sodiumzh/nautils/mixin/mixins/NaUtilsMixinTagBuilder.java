package net.sodiumzh.nautils.mixin.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.logging.LogUtils;
import net.minecraft.tags.Tag;
import net.sodiumzh.nautils.mixin.NaUtilsMixin;
import net.sodiumzh.nautils.registries.NaUtilsConfigs;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Mixin(Tag.Builder.class)
public class NaUtilsMixinTagBuilder implements NaUtilsMixin<Tag.Builder> {


    @WrapOperation(method = "build(Ljava/util/function/Function;Ljava/util/function/Function;)Lcom/mojang/datafixers/util/Either;",
    at = @At(value = "INVOKE", target = "java/util/List.isEmpty()Z"))
    private <T> boolean fixTag(List<Tag.BuilderEntry> instance, Operation<Boolean> original)
    {
        if (!instance.isEmpty() && NaUtilsConfigs.CACHED_ENABLES_TAG_FIX) {
            String info = "Tag building missing entries ";
            for (Tag.BuilderEntry entry: instance) {
                info = info + String.format("\"%s\"", entry.entry().toString());
            }
            LogUtils.getLogger().warn(info);
            LogUtils.getLogger().info("Note: the tag-missing-entries issue is fixed by NaUtils. Please check the modpack.");
            instance.clear();
        }
        return original.call(instance);
    }

}
