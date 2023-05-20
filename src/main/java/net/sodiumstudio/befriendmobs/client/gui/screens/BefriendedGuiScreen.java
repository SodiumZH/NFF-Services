package net.sodiumstudio.befriendmobs.client.gui.screens;

import java.text.DecimalFormat;
import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sodiumstudio.befriendmobs.entity.IBefriendedMob;
import net.sodiumstudio.befriendmobs.inventory.BefriendedInventoryMenu;
import net.sodiumstudio.befriendmobs.util.InfoHelper;
import net.sodiumstudio.befriendmobs.util.math.IntVec2;

@OnlyIn(Dist.CLIENT)
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
	public IntVec2 getTextureSize() 
	{
		return new IntVec2(256, 256);
	}
	
	public BefriendedGuiScreen(BefriendedInventoryMenu pMenu, Inventory pPlayerInventory,
			IBefriendedMob mob) {
		this(pMenu, pPlayerInventory, mob, true);
	}

	public BefriendedGuiScreen(BefriendedInventoryMenu pMenu, Inventory pPlayerInventory,
			IBefriendedMob mob, boolean renderName)
	{
		super(pMenu, pPlayerInventory, renderName ? ((LivingEntity)mob).getName() : InfoHelper.createText(""));
		this.mob = mob;
		this.passEvents = false;
	}

	@Override
	protected void renderBg(PoseStack pPoseStack, float pPartialTick, int pMouseX, int pMouseY) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, getTextureLocation());
	}
	
	/** 
	 * Background, mouse XY and tooltip are rendered here. Do not render them again in subclasses.
	 * <p>背景、鼠标XY位置及窗口名已经在这里渲染过了。子类中不要重复渲染。
	 */
	@Override
	public void render(PoseStack pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
		this.renderBackground(pPoseStack);
		this.xMouse = (float) pMouseX;
		this.yMouse = (float) pMouseY;
		super.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
		this.renderTooltip(pPoseStack, pMouseX, pMouseY);
	}
	
	/**
	 * IntVec2 version of blit
	 */
	public void blit(PoseStack poseStack, IntVec2 xy, IntVec2 uvOffset, IntVec2 uvSize)
	{
		blit(poseStack, xy.x, xy.y, uvOffset.x, uvOffset.y, uvSize.x, uvSize.y);
	}
	
	/**
	 * For adapting non-standard texture size
	 */
	@Override
	public void blit(PoseStack pPoseStack, int pX, int pY, int pUOffset, int pVOffset, int pUWidth, int pVHeight) 
	{
		blit(pPoseStack, pX, pY, this.getBlitOffset(), (float) pUOffset, (float) pVOffset, pUWidth, pVHeight,
				getTextureSize().x, getTextureSize().y);
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
	public void addSlotBg(PoseStack poseStack, int slotIndex, IntVec2 xy, @Nullable IntVec2 uvEmpty, @Nullable IntVec2 uvFilled)
	{
		if (!menu.slots.get(slotIndex).hasItem() && uvEmpty != null)
			blit(poseStack, xy, uvEmpty, IntVec2.valueOf(18));
		else if (menu.slots.get(slotIndex).hasItem() && uvFilled != null)
			blit(poseStack, xy, uvFilled, IntVec2.valueOf(18));
	}

	@Deprecated
	public void addHealthInfo(PoseStack poseStack, IntVec2 position, int color)
	{
		int hp = (int) ((LivingEntity)mob).getHealth();
		int maxHp = (int) ((LivingEntity)mob).getMaxHealth();
		Component info = InfoHelper.createText("HP: " + hp + " / " + maxHp);
		font.draw(poseStack, info, position.x, position.y, color);
	}
	
	@Deprecated
	public void addHealthInfo(PoseStack poseStack, IntVec2 position)
	{
		addHealthInfo(poseStack, position, 0x404040);
	}
	
	/** 
	 * (Preset) Add mob attribute info, including HP/MaxHP, ATK, armor
	 * <p>（预设）添加生物属性信息，包括HP/MaxHP、攻击力、护甲
	 */
	public void addAttributeInfo(PoseStack poseStack, IntVec2 position, int color, int textRowWidth)
	{
		IntVec2 pos = position.copy();
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
		font.draw(poseStack, hpcomp, pos.x, pos.y, color);
		pos.addY(textRowWidth);
		font.draw(poseStack, atkcomp, pos.x, pos.y, color);
		pos.addY(textRowWidth);
		font.draw(poseStack, defcomp, pos.x, pos.y, color);
	}
	
	/**
	 * Add attribute info using default font
	 * <p>添加生物属性信息，使用默认字体
	 */
	public void addAttributeInfo(PoseStack poseStack, IntVec2 position)
	{
		addAttributeInfo(poseStack, position, 0x404040, 11);
	}
}
