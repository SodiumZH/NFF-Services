package net.sodiumzh.nautils.mixin.event.client.entity;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.ClientSideMerchant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.eventbus.api.Event;
import net.sodiumzh.nautils.entity.vanillatrade.CVanillaMerchant;
import net.sodiumzh.nautils.statics.NaUtilsMiscStatics;
import net.sodiumzh.nautils.statics.NaUtilsReflectionStatics;

public class MerchantOfferUnavailableInfoEvent extends Event
{
	public final Player player;
	/**
	 * Usually this is a {@link ClientSideMerchant}. To search for the trading
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
	
	/**
	 * Search for merchant using the given vanilla merchant that's trading with the player around the mob.
	 * If not found (not existing or not using this vanilla merchant system e.g. vanilla villager), returns null.
	 */
	@Nullable
	public <T extends CVanillaMerchant> T searchOngoingMerchant(Capability<? extends T> cap, double range)
	{
		List<Entity> list = this.player.level.getEntities(player, player.getBoundingBox().inflate(range)).stream().filter(entity -> 
			NaUtilsMiscStatics.getValueFromCapability(entity, cap, CVanillaMerchant::getTradingPlayer) == player
		).toList();
		return list.isEmpty() ? null : NaUtilsMiscStatics.getValueFromCapability(list.get(0), cap, c -> c);
	}
}
