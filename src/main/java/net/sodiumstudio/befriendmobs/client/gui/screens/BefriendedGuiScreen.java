package net.sodiumstudio.befriendmobs.client.gui.screens;

import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.sodiumstudio.befriendmobs.entity.befriended.IBefriendedMob;
import net.sodiumstudio.befriendmobs.inventory.BefriendedInventoryMenu;
import net.sodiumstudio.nautils.InfoHelper;
import net.sodiumstudio.nautils.math.GuiPos;

public abstract class BefriendedGuiScreen extends AbstractContainerScreen<BefriendedInventoryMenu> {

	public IBefriendedMob mob;
	protected float xMouse = 0;
	protected float yMouse = 0;

	/**
	 * Specifies the texture image it uses.
	 * <p>指定所用的贴图。
	 */
	public abstract ResourceLocation getTextureLocation();
	
	/**
	 * If the texture is not a standard 256x256 image, override this value
	 */
	public GuiPos getTextureSize() 
	{
		return new GuiPos(256, 256);
	}

	public BefriendedGuiScreen(BefriendedInventoryMenu pMenu, Inventory pPlayerInventory,
			IBefriendedMob mob, boolean rendersName)
	{
		super(pMenu, pPlayerInventory, rendersName ? ((LivingEntity)mob).getName() : InfoHelper.createText(""));
		this.mob = mob;
	}
	
	public BefriendedGuiScreen(BefriendedInventoryMenu pMenu, Inventory pPlayerInventory,
			IBefriendedMob mob) {
		this(pMenu, pPlayerInventory, mob, true);
	}
	
	public BefriendedGuiScreen(BefriendedInventoryMenu menu)
	{
		this(menu, menu.playerInventory, menu.mob);
	}
	
	@Override
	protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, getTextureLocation());
	}
	
	/** 
	 * Background, mouse XY and tooltip are rendered here. Do not render them again in subclasses.
	 * <p>背景、鼠标XY位置及窗口名已经在这里渲染过了。子类中不要重复渲染。
	 */
	@Override
	public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
		this.renderBackground(pGuiGraphics);
		this.xMouse = (float) pMouseX;
		this.yMouse = (float) pMouseY;
		super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
		this.renderTooltip(pGuiGraphics, pMouseX, pMouseY);
	}
	
	/**
	 * IntVec2 version of blit
	 * @deprecated use {@code drawSprite} instead
	 */
	@Deprecated
	public void blit(GuiGraphics GuiGraphics, GuiPos xy, GuiPos uvOffset, GuiPos uvSize)
	{
		blit(GuiGraphics, xy.x, xy.y, uvOffset.x, uvOffset.y, uvSize.x, uvSize.y);
	}
	
	/**
	 * For adapting non-standard texture size
	 * @deprecated use {@code drawSprite} instead
	 */
	@Deprecated
	public void blit(GuiGraphics graphics, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight) 
	{
		graphics.blit(getTextureLocation(), x, y, getTextureSize().x, getTextureSize().y, uOffset, vOffset,
				uWidth, vHeight, getTextureSize().x, getTextureSize().y);
		/*blit(pGuiGraphics, pX, pY, pGuiGraphics.off, (float) pUOffset, (float) pVOffset, pUWidth, pVHeight,
				getTextureSize().x, getTextureSize().y);*/
	}
	
	/**
	 * Draw a sprite (part of a texture).
	 * @param x X position on screen.
	 * @param y Y position on screen.
	 * @param uOffset U (X) offset on texture (i.e. U position of the top-left corner of the sprite on texture)
	 * @param vOffset V (Y) offset on texture (i.e. V position of the top-left corner of the sprite on texture)
	 * @param uWidth Sprite U (X) width. 
	 * @param vHeight Sprite V (Y) width.
	 */
	public void drawSprite(GuiGraphics graphics, int x, int y, int uOffset, int vOffset, int uWidth, int vHeight)
	{
		graphics.blit(getTextureLocation(), x, y, uWidth, vHeight, uOffset, vOffset,
				getTextureSize().x, getTextureSize().y);
	}
	
	/**
	 * Draw a sprite (part of a texture).
	 * @param xy XY position on the screen
	 * @param uvOffset UV (XY) offset on texture (i.e. UV/XY position of the top-left corner of the sprite on texture)
	 * @param uvSize Sprite UV/XY size.
	 */
	public void drawSprite(GuiGraphics GuiGraphics, GuiPos xy, GuiPos uvOffset, GuiPos uvSize)
	{
		drawSprite(GuiGraphics, xy.x, xy.y, uvSize.x, uvSize.y, uvOffset.x, uvOffset.y);
	}
	
	/**
	 * Add an item slot in the GUI.
	 * @param slotIndex The slot index in inventory menu. <p>在道具栏菜单中的栏位序号。
	 * @param xy Position on the screen. <p>该GUI在屏幕上的位置。
	 * @param uvEmpty Position of the slot image on the texture image when the slot is empty. <p>当道具为空时的栏位贴图背景。
	 * @param uvFilled Position of the slot image on the texture image when the slot is filled. <p>当道具非空时的栏位贴图背景。
	 * <p>========
	 * <p>加入一个道具槽。
	 * @param slotIndex 在道具栏菜单中的栏位序号。
	 * @param xy 该GUI在屏幕上的位置。
	 * @param uvEmpty 当道具为空时的栏位贴图背景，在整张贴图中的位置坐标。
	 * @param uvFilled 当道具非空时的栏位贴图背景，在整张贴图中的位置坐标。
	 */
	public void addSlotBg(GuiGraphics GuiGraphics, int slotIndex, GuiPos xy, @Nullable GuiPos uvEmpty, @Nullable GuiPos uvFilled)
	{
		if (!menu.slots.get(slotIndex).hasItem() && uvEmpty != null)
			drawSprite(GuiGraphics, xy, uvEmpty, GuiPos.valueOf(18));
		else if (menu.slots.get(slotIndex).hasItem() && uvFilled != null)
			drawSprite(GuiGraphics, xy, uvFilled, GuiPos.valueOf(18));
	}

	@Deprecated
	public void addHealthInfo(GuiGraphics graphics, GuiPos position, int color)
	{
		int hp = (int) ((LivingEntity)mob).getHealth();
		int maxHp = (int) ((LivingEntity)mob).getMaxHealth();
		Component info = InfoHelper.createText("HP: " + hp + " / " + maxHp);
		graphics.drawString(font, info, position.x, position.y, color);
	}
	
	@Deprecated
	public void addHealthInfo(GuiGraphics GuiGraphics, GuiPos position)
	{
		addHealthInfo(GuiGraphics, position, 0x404040);
	}
	
	/** 
	 * (Preset) Add mob attribute info, including HP/MaxHP, ATK, armor
	 * <p>（预设）添加生物属性信息，包括HP/MaxHP、攻击力、护甲
	 */
	public void addAttributeInfo(GuiGraphics graphics, GuiPos position, int color, int textRowWidth)
	{
		GuiPos pos = position.copy();
		String hp = Integer.toString(Math.round(mob.asMob().getHealth()));
		String maxHp = Long.toString(Math.round(mob.asMob().getAttributeValue(Attributes.MAX_HEALTH)));
		String atk = Long.toString(Math.round(mob.asMob().getAttributeValue(Attributes.ATTACK_DAMAGE)));
		String def = Long.toString(Math.round(mob.asMob().getAttributeValue(Attributes.ARMOR)));
		Component hpcomp = InfoHelper.createTrans("info.befriendmobs.gui_health")
				.append(InfoHelper.createText(": " + hp + " / " + maxHp));
		Component atkcomp = InfoHelper.createTrans("info.befriendmobs.gui_atk")
				.append(InfoHelper.createText(": " + atk));
		Component defcomp = InfoHelper.createTrans("info.befriendmobs.gui_armor")
				.append(InfoHelper.createText(": " + def));
		graphics.drawString(font, hpcomp, pos.x, pos.y, color, false);
		pos.addY(textRowWidth);
		graphics.drawString(font, atkcomp, pos.x, pos.y, color, false);
		pos.addY(textRowWidth);
		graphics.drawString(font, defcomp, pos.x, pos.y, color, false);
	}
	
	/**
	 * Add attribute info using default font
	 * <p>添加生物属性信息，使用默认字体
	 */
	public void addAttributeInfo(GuiGraphics GuiGraphics, GuiPos position)
	{
		addAttributeInfo(GuiGraphics, position, 0x404040, 11);
	}
}
