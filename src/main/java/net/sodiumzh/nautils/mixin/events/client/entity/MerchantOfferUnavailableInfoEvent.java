package net.sodiumzh.nautils.mixin.events.client.entity;

import javax.annotation.Nullable;

import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.npc.ClientSideMerchant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumzh.nautils.statics.NaUtilsReflectionStatics;

/**
 * Posted on the player's mouse pointing on merchant menu's crossed arrow when the offer is not available.
 * By default it's "Villagers restock up to two times per day." (translation key = "merchant.deprecated").
 */
@OnlyIn(Dist.CLIENT)
public class MerchantOfferUnavailableInfoEvent extends Event
{
	public final Player player;
	/**
	 * Usually this is a {@link ClientSideMerchant}. Note it's probably not a {@code Mob}.
	 */
	public final Merchant tradingMerchant;
	public final MerchantScreen screen;
	public final int activeOfferIndex;
	public final MerchantOffer activeOffer;
	private Component info;
	private boolean noInfo = false;
	public MerchantOfferUnavailableInfoEvent(MerchantScreen screen, Component original)
	{
		this.screen = screen;
		MerchantMenu menu = screen.getMenu();
		this.tradingMerchant = NaUtilsReflectionStatics.forceGet(menu, MerchantMenu.class, "f_40027_").cast() ;	// trader
		this.player = this.tradingMerchant.getTradingPlayer();
		this.activeOfferIndex = NaUtilsReflectionStatics.forceGet(screen, MerchantScreen.class, "f_99117_").cast();	// shopItem
		this.activeOffer = menu.getOffers().get(activeOfferIndex);
		this.info = original;
	}
	
	@Nullable
	public Component getInfo() {
		return info;
	}
	
	public void setInfo(Component info) {
		this.info = info;
	}
	
	public void noInfo()
	{
		this.noInfo = true;
	}
	
	public boolean isNoInfo()
	{
		return this.noInfo;
	}

}
