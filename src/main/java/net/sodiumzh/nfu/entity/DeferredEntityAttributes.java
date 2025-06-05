package net.sodiumzh.nfu.entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.util.NFUReflectionStatics;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * An additional table to register entity attributes that will be merged into the Forge attribute registry
 * on server and client start instead of mod loading. This allows to use values that are inaccessible on setup (e.g. config values)
 * for entity attributes.
 * <p>Note that this registration will overwrite the attributes registered in {@link EntityAttributeCreationEvent}
 */
@Mod.EventBusSubscriber(modid = NFULibrary.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DeferredEntityAttributes {

    private static final Map<Supplier<EntityType<? extends LivingEntity>>, Supplier<AttributeSupplier.Builder>> TABLE = new HashMap<>();

    static void put(Supplier<EntityType<? extends LivingEntity>> type, Supplier<AttributeSupplier.Builder> attr)
    {
        TABLE.put(type, attr);
    }

    private static Map<EntityType<? extends LivingEntity>, AttributeSupplier> getForgeAttributeRegistry() {
        return NFUReflectionStatics.forceGet(null, ForgeHooks.class, "FORGE_ATTRIBUTES").cast();
    }

    private static void merge() {
        Map<EntityType<? extends LivingEntity>, AttributeSupplier> reg = getForgeAttributeRegistry();
        for (var entry: TABLE.entrySet())
        {
            reg.put(entry.getKey().get(), entry.getValue().get().build());
        }
    }

    @Mod.EventBusSubscriber(modid = NFULibrary.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientListener {

        public static void mergeClient(FMLClientSetupEvent event) {
            merge();
        }

    }

    @SubscribeEvent
    public static void mergeServer(ServerAboutToStartEvent event)
    {
        merge();
    }

}
