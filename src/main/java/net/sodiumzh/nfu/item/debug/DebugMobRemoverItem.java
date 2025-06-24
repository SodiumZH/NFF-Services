package net.sodiumzh.nfu.item.debug;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.sodiumzh.nfu.item.NFUItem;
import net.sodiumzh.nfu.util.NFUInfoStatics;

import javax.annotation.Nonnull;
import java.util.UUID;

public class DebugMobRemoverItem extends NFUItem {

    private static final String KEY_IS_DISCARD_MODE = "isDiscardMode";
    private static final String KEY_REMOVING_MOB_UUID = "removingMobUUID";
    private static final UUID EMPTY_UUID = new UUID(0L, 0L);
    public DebugMobRemoverItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult interactLivingEntity(Player player, LivingEntity target, InteractionHand hand) {
        if (!player.level.isClientSide()
                && target instanceof Mob
                && !player.isShiftKeyDown()) {
            ItemStack stack = player.getItemInHand(hand);

            // Check if set ongoing mob
            UUID removingUUID = getOngoingMobUUID(stack);

            // If not set or targeting non-ongoing mob, reset to the target
            if (removingUUID.equals(EMPTY_UUID) || !removingUUID.equals(target.getUUID())) {
                stack.getOrCreateTag().putUUID(KEY_REMOVING_MOB_UUID, target.getUUID());
                NFUInfoStatics.printMessageTranslatable(player, "info.nfulib.item.debug_mob_remover_selected",
                        target.getName().getString(), getModeInfo(stack).getString());
                return InteractionResult.sidedSuccess(player.level.isClientSide);
            }
            // Confirmed, remove
            else {
                NFUInfoStatics.printMessageTranslatable(player, "info.nfulib.item.debug_mob_remover_removed", target.getName().getString());
                if (isDiscardMode(stack)) {
                    target.discard();
                } else {
                    target.kill();
                }
                stack.getOrCreateTag().putUUID(KEY_REMOVING_MOB_UUID, EMPTY_UUID);
                return InteractionResult.sidedSuccess(player.level.isClientSide);
            }

        }
        return InteractionResult.sidedSuccess(player.level.isClientSide);
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level level, Player player, @Nonnull InteractionHand usedHand) {
        ItemStack stack = player.getItemInHand(usedHand);
        if (level.isClientSide)
            return InteractionResultHolder.pass(stack);
        if (player.isShiftKeyDown()) {
            stack.getOrCreateTag().putBoolean(KEY_IS_DISCARD_MODE, !isDiscardMode(stack));
            stack.getOrCreateTag().putUUID(KEY_REMOVING_MOB_UUID, EMPTY_UUID);
            NFUInfoStatics.printMessageTranslatable(player, "info.nfulib.item.debug_mob_remover_mode_switched",
                    getModeInfo(stack).getString());
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }
        else {
            if (!getOngoingMobUUID(stack).equals(EMPTY_UUID)) {
                stack.getOrCreateTag().putUUID(KEY_REMOVING_MOB_UUID, EMPTY_UUID);
                NFUInfoStatics.printMessageTranslatable(player, "info.nfulib.item.debug_mob_remover_reset");
                return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
            }
        }
        return InteractionResultHolder.pass(stack);
    }

    public static boolean isDiscardMode(ItemStack stack) {
        if (!(stack.getItem() instanceof DebugMobRemoverItem))
            throw new ClassCastException("NaUtils#DebugMobRemoverItem: Illegal static method call, not a correct item.");
        CompoundTag nbt = stack.getTag();
        if (nbt == null) return false;
        if (!nbt.contains(KEY_IS_DISCARD_MODE, Tag.TAG_ANY_NUMERIC))
            return false;
        return nbt.getBoolean(KEY_IS_DISCARD_MODE);
    }

    @Nonnull
    public static UUID getOngoingMobUUID(ItemStack stack) {
        if (!(stack.getItem() instanceof DebugMobRemoverItem))
            throw new ClassCastException("NaUtils#DebugMobRemoverItem: Illegal static method call, not a correct item.");
        CompoundTag nbt = stack.getTag();
        if (nbt == null) return EMPTY_UUID;
        if (!nbt.hasUUID(KEY_REMOVING_MOB_UUID))
            return EMPTY_UUID;
        return nbt.getUUID(KEY_REMOVING_MOB_UUID);
    }

    public static Component getModeInfo(ItemStack stack) {
        if (!(stack.getItem() instanceof DebugMobRemoverItem))
            throw new ClassCastException("NaUtils#DebugMobRemoverItem: Illegal static method call, not a correct item.");
        return NFUInfoStatics.createTranslatable(isDiscardMode(stack) ?
                "info.nfulib.item.debug_mob_remover_discard_mode" :
                "info.nfulib.item.debug_mob_remover_kill_mode");
    }

    public static Component getModeDesc(ItemStack stack) {
        if (!(stack.getItem() instanceof DebugMobRemoverItem))
            throw new ClassCastException("NaUtils#DebugMobRemoverItem: Illegal static method call, not a correct item.");
        return NFUInfoStatics.createTranslatable(isDiscardMode(stack) ?
                "info.nfulib.item.debug_mob_remover_discard_mode_desc" :
                "info.nfulib.item.debug_mob_remover_kill_mode_desc");
    }
}
