package net.sodiumstudio.nautils.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.sodiumstudio.nautils.math.GuiPos;

public class NaGuiUtils
{
	public static void setActiveTexture(ResourceLocation loc)
	{
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, loc);
	}
	
	/**
	 * Draw a sprite (part of a texture).
	 * @param xy XY position on the screen
	 * @param uvOffset UV (XY) offset on texture (i.e. UV/XY position of the top-left corner of the sprite on texture)
	 * @param uvSize Sprite UV/XY size.
	 */
	public static void drawSprite(PoseStack GuiGraphics, GuiPos xy, int blitOffset, GuiPos uvOffset, GuiPos uvSize, GuiPos textureSize)
	{
		GuiComponent.blit(GuiGraphics, xy.x, xy.y, blitOffset, uvOffset.x, uvOffset.y, uvSize.x, uvSize.y, textureSize.x, textureSize.y);
	}
}
