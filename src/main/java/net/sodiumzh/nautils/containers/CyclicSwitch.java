package net.sodiumzh.nautils.containers;

import java.util.ArrayList;
import java.util.List;

import com.mojang.logging.LogUtils;

/**
 * A {@code CyclicSwitch} is a ordered collection that can be switched cyclicly, 
 * for example, for s = {1, 2, 3}, then s.next(1) == 2, s.next(3) == 1.
 */
public class CyclicSwitch<T> extends ArrayList<T>
{
	private static final long serialVersionUID = 239244005304038625L;

	public CyclicSwitch(T... elems)
	{
		super();
		for (int i = 0; i < elems.length; ++i)
		{
			this.add(elems[i]);
		}
	}
	
	public CyclicSwitch(List<T> elems)
	{
		super();
		for (int i = 0; i < elems.size(); ++i)
		{
			this.add(elems.get(i));
		}
	}
	
	public T next(T thiz)
	{
		if (this.size() == 0)
		{
			LogUtils.getLogger().warn("CyclicSwitch: empty.");
			return null;
		}
		int i = this.indexOf(thiz);
		if (i < 0)
		{
			LogUtils.getLogger().warn("CyclicSwitch: this object isn't present in the list.");
			return null;
		}
		if (i == this.size() - 1)
			return this.get(0);
		else return this.get(i + 1);
	}
	
	public T last(T thiz)
	{
		if (this.size() == 0)
		{
			LogUtils.getLogger().warn("CyclicSwitch: empty.");
			return null;
		}
		int i = this.indexOf(thiz);
		if (i < 0)
		{
			LogUtils.getLogger().warn("CyclicSwitch: this object isn't present in the list.");
			return null;
		}
		if (i == 0)
			return this.get(this.size() - 1);
		else return this.get(i - 1);
	}

	public CyclicSwitch<T> copy()
	{
		return new CyclicSwitch<>(this);
	}
	
}
