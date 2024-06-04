package net.sodiumstudio.nautils.containers;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.mojang.logging.LogUtils;

/**
 * A {@code CompoundSet} is a combination of multiple sets including several "external" parts and a "mutable" part.
 * <p>An "external" part is a reference of another set. It will be counted into the set elements but not changeable in this set.
 * <p>The "mutable" part is an internal part that can be modified with {@code add}, {@code remove} etc. of this set. Please note
 * that "remove" operation doesn't guarantee the element to be removed as it may exist in an external part.
 * <p><b>Note: It's intended to work as static references e.g. registries but not created/modified on runtime.</b>
 * This set is not performance-friendly as it needs to keep a real HashSet and refresh on many operations, 
 * including {@code size}, {@code addAll}, iteration, etc.
 * Also, as it allows to be recursively defined, the HashSet refresh could cause multiple {@code CompoundSet}s to refresh.
 */
public class CompoundSet<E> implements Set<E>
{

	private Set<Set<E>> externalParts = new HashSet<>();
	private HashSet<E> mutablePart = new HashSet<>();
	private HashSet<E> cachedHash = new HashSet<>();
	
	public CompoundSet() {}
	
	public CompoundSet(Set<E> external)
	{
		this.externalParts.add(external);
	}
	
	protected HashSet<E> updateHashSet()
	{
		cachedHash.clear();
		for (var ex: externalParts)
		{
			cachedHash.addAll(ex);
		}
		cachedHash.addAll(mutablePart);
		return cachedHash;
	}
	
	protected boolean containsInExternalParts(Object o)
	{
		for (var ex: externalParts)
		{
			if (ex.contains(o)) return true;
		}
		return false;
	}
	
	protected void cleanMutable()
	{
		this.mutablePart.removeIf(e -> this.containsInExternalParts(e));
	}
	
	@Override
	public int size() {
		return this.updateHashSet().size();
	}

	@Override
	public boolean isEmpty() {
		for (var ex: externalParts)
		{
			if (!ex.isEmpty())
				return false;
		}
		return this.mutablePart.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		for (var ex: externalParts)
		{
			if (ex.contains(o))
				return true;
		}
		return this.mutablePart.contains(o);
	}

	@Override
	public Iterator<E> iterator() {
		return this.updateHashSet().iterator();
	}

	@Override
	public Object[] toArray() {
		return this.updateHashSet().toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.updateHashSet().toArray(a);
	}

	@Override
	public boolean add(E e) {
		if (this.contains(e)) return false;
		return this.mutablePart.add(e);
	}

	@Override
	public boolean remove(Object o) {
		if (this.containsInExternalParts(o))
		{
			LogUtils.getLogger().warn("NaUtils#CompoundSet: attempting to remove an element from the external parts. Skipped and returned false.");
			this.cleanMutable();
			return false;
		}
		else if (this.mutablePart.contains(o))
			return this.mutablePart.remove(o);
		else return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.updateHashSet().containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		int old = this.size();
		this.mutablePart.addAll(c);
		this.cleanMutable();
		return this.updateHashSet().size() != old;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		throw new UnsupportedOperationException("NaUtils#CompoundSet doesn't support retainAll().");
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		throw new UnsupportedOperationException("NaUtils#CompoundSet doesn't support removeAll().");
	}

	@Override
	public void clear() {
		this.externalParts.clear();
		this.mutablePart.clear();
	}
	
	/**
	 * Clear the mutable part only.
	 */
	public void clearMutable()
	{
		this.mutablePart.clear();
	}
	
	public void addExternalSet(Set<E> s)
	{
		this.externalParts.add(s);
	}

	public void removeExternalSet(Set<E> s)
	{
		this.externalParts.remove(s);
	}
	
	/**
	 * Returns a {@code HashSet} as a copy containing all elements of this {@code CompoundSet}.
	 * 
	 */
	public HashSet<E> toHashSet()
	{
		var res = new HashSet<E>();
		res.addAll(this.updateHashSet());
		return res;
	}
	
	/**
	 * Create a new {@code CompoundSet} using {@code this} as an external.
	 */
	public CompoundSet<E> createCompoundSet()
	{
		return new CompoundSet<>(this);
	}

	@Override
	public String toString()
	{
		String res = "CompoundSet { EXTERNAL: \n";
		for (var s: this.externalParts)
			res = res + s.toString() + ",\n" + "*****\n";
		res = res + "MUTABLE:\n" + this.mutablePart.toString() + "\n*****}\n";
		return res;
	}
	
}
