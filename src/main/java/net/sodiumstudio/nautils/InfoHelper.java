package net.sodiumstudio.nautils;

import java.util.ArrayList;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.sodiumstudio.nautils.math.LinearColor;

public class InfoHelper
{
	// Create a component with plain text content, equals to TextComponent in 1.18.2
	public static MutableComponent createText(String str)
	{
		return MutableComponent.create(new LiteralContents(str));
	}
	
	// Create a component with translatable content, equals to TranslatableComponent in 1.18.2
	public static MutableComponent createTrans(String key, Object... params)
	{
		return Component.translatable(key, params);
	}
	
	public static ComponentBuilder builder()
	{
		return new ComponentBuilder();
	}
	
	public static class ComponentBuilder
	{
		private ArrayList<MutableComponent> components = new ArrayList<>();
		
		private ComponentBuilder() {}
		
		public ComponentBuilder putText(String str)
		{
			components.add(createText(str));
			return this;
		}
		
		public ComponentBuilder putTrans(String key, Object... params)
		{
			components.add(createTrans(key, params));
			return this;
		}
		
		public MutableComponent build()
		{
			if (components.size() == 0)
				return createText("");
			MutableComponent res = components.get(0);
			for (int i = 1; i < components.size(); ++i)
			{
				res = res.append(components.get(i));
			}
			return res;
		}
		
	}
	
	
}
