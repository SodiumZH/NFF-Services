package net.sodiumzh.nautils.info;

import java.util.ArrayList;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.sodiumzh.nautils.statics.NaUtilsInfoStatics;

/**
 * A {@code ComponentBuilder} is a builder for simplifying the creation of complex {@link MutableComponent}s. It allows chain coding.
 * <p> Usage example: 
 * <p> {@code MutableComponent comp = ComponentBuilder.create().appendText("Text and ").appendTranslatable("info.nautils.key").build();} 
 * <p> and add translation in {@code lang} files like: {@code "info.nautils.key" : "Translation"}, then the result is "Text and Translation". 
 */
public class ComponentBuilder
{
	private ArrayList<MutableComponent> components = new ArrayList<>();
	
	private ComponentBuilder() {}
	
	/**
	 * Create a new (empty) ComponentBuilder. Use append methods to add contents.
	 */
	public static ComponentBuilder create()
	{
		return new ComponentBuilder();
	}
	
	/**
	 * Append a plain-text component.
	 */
	public ComponentBuilder appendText(String str)
	{
		components.add(NaUtilsInfoStatics.createText(str));
		return this;
	}
	
	/**
	 * Append a translatable component with key and params.
	 */
	public ComponentBuilder appendTranslatable(String key, Object... params)
	{
		components.add(NaUtilsInfoStatics.createTranslatable(key, params));
		return this;
	}
	
	/**
	 * Append an existing {@link MutableComponent}.
	 * @throws IllegalArgumentException If the input Component isn't a MutableComponent. (This don't happen if using vanilla components)
	 */
	public ComponentBuilder append(Component component)
	{
		if (component instanceof MutableComponent mc)
		{
			components.add(mc);
			return this;
		}
		else throw new IllegalArgumentException("ComponentBuilder only supports MutableComponent.");
	}
	
	/**
	 * Generate MutableComponent from current builder.
	 */
	public MutableComponent build()
	{
		if (components.size() == 0)
			return NaUtilsInfoStatics.createText("");
		MutableComponent res = components.get(0);
		for (int i = 1; i < components.size(); ++i)
		{
			res = res.append(components.get(i));
		}
		return res;
	}
}
