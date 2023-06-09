package net.sodiumstudio.befriendmobs.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.sodiumstudio.befriendmobs.BefriendMobs;
import net.sodiumstudio.befriendmobs.client.gui.screens.BefriendedGuiScreen;

@Mod.EventBusSubscriber(modid = BefriendMobs.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientEvents 
{

	@SubscribeEvent
	public static void onLevelTick(TickEvent.LevelTickEvent event) {
		
		if (event.side == LogicalSide.CLIENT)
		{
			Minecraft mc = Minecraft.getInstance();
			if (mc.screen != null && mc.screen instanceof BefriendedGuiScreen bgs)
			{
				if (!bgs.mob.asMob().isAlive())
				{
					mc.setScreen(null);
				}
				
			}
		}
	}

}
