package net.sodiumzh.nfu.registry;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.eventhandler.NFUEntityEventHandlers;
import net.sodiumzh.nfu.util.NFUTagStatics;

public class NFUTags {

    /**
     * Explosions with cause entity or direct entity with this tag will not break item entities.
     * <p>Implemented through {@link NFUEntityEventHandlers#onItemEntityHurt}.
     */
    public static final TagKey<EntityType<?>> EXPLOSION_NOT_BREAKING_ITEMS =
        NFUTagStatics.createEntityTypeTag(NFULibrary.MOD_ID, "explosion_not_breaking_items");


}
