package net.sodiumzh.nfu.info;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.sodiumzh.nfu.util.NFUContainerStatics;
import net.sodiumzh.nfu.util.NFUInfoStatics;
import org.spongepowered.asm.mixin.Mutable;

import java.util.ArrayList;

/**
 * A {@code ComponentBuilder} is a builder for simplifying the creation of complex {@link MutableComponent}s. It allows chain coding.
 * <p> Usage example: 
 * <p> {@code MutableComponent comp = ComponentBuilder.create().appendText("Text and ").appendTranslatable("info.nfulib.key").build();} 
 * <p> and add translation in {@code lang} files like: {@code "info.nfulib.key" : "Translation"}, then the result is "Text and Translation". 
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
		components.add(NFUInfoStatics.createText(str));
		return this;
	}
	
	/**
	 * Append a translatable component with key and params.
	 */
	public ComponentBuilder appendTranslatable(String key, Object... params)
	{
		components.add(NFUInfoStatics.createTranslatable(key, params));
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

	public ComponentBuilder insertAt(int index, Component component) {
		if (component instanceof MutableComponent mc)
		{
			components.add(index, mc);
			return this;
		}
		else throw new IllegalArgumentException("ComponentBuilder only supports MutableComponent.");
	}

	public ComponentBuilder appendAtStart(Component component) {
		return insertAt(0, component);
	}

	public MutableComponent getAt(int index) {
		return components.get(index);
	}

	public ComponentBuilder removeAt(int index) {
		components.remove(index);
		return this;
	}

	public List<Integer> getIndexIf(Predicate<MutableComponent> condition) {
		return NFUContainerStatics.toIndexMap(components).entrySet().stream().filter(entry  -> condition.test(entry.getValue()))
			.map(Map.Entry::getKey).toList();
	}

	public ComponentBuilder removeIf(Predicate<MutableComponent> condition) {
		var newList = components.stream().filter(condition).toList();
		components.clear();
		components.addAll(newList);
		return this;
	}

	public ComponentBuilder removeIfEmpty(Predicate<MutableComponent> condition) {
		return removeIf(mc -> mc.getString().isEmpty());
	}

	public ComponentBuilder modifyIf(Predicate<MutableComponent> condition, UnaryOperator<MutableComponent> action) {
		NFUContainerStatics.toIndexMap(components).entrySet().forEach(e -> {
			if (condition.test(e.getValue())) components.set(e.getKey(), action.apply(e.getValue()));
		});
		return this;
	}

	public ComponentBuilder modifyIf(Predicate<MutableComponent> condition, Consumer<MutableComponent> action) {
		return modifyIf(condition, mc -> {
			action.accept(mc);
			return mc;
		});
	}

	/**
	 * Generate MutableComponent from current builder.
	 */
	public MutableComponent build()
	{
		if (components.isEmpty())
			return NFUInfoStatics.createText("");
		MutableComponent res = components.get(0);
		for (int i = 1; i < components.size(); ++i)
		{
			res = res.append(components.get(i));
		}
		return res;
	}
}
