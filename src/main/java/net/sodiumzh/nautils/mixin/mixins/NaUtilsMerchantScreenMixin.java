package net.sodiumzh.nautils.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.MinecraftForge;
import net.sodiumzh.nautils.mixin.NaUtilsMixin;
import net.sodiumzh.nautils.mixin.event.client.entity.MerchantOfferUnavailableInfoEvent;

@Mixin(MerchantScreen.class)
public class NaUtilsMerchantScreenMixin implements NaUtilsMixin<MerchantScreen>
{
	@WrapOperation(method = "render(Lcom/mojang/blaze3d/vertex/PoseStack;IIF)V",
			at = @At(value = "INVOKE", 
			target = "Lnet/minecraft/client/gui/screens/inventory/MerchantScreen;renderTooltip(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/network/chat/Component;II)V"))
	private void renderDeprecatedTooltip(MerchantScreen caller, PoseStack poseStack, Component info, int mouseX, int mouseY, Operation<Void> original)
	{
		var event = new MerchantOfferUnavailableInfoEvent(caller, info);
		MinecraftForge.EVENT_BUS.post(event);
		if (!event.isNoInfo())
			original.call(caller, poseStack, event.getInfo(), mouseX, mouseY);
	}
}
