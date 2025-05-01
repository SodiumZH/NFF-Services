package net.sodiumzh.nfu.container;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import com.mojang.logging.LogUtils;
import net.sodiumzh.nfu.object.DirectedGraphNode;

/**
 * A {@code LinkableSet} is a {@link Set} with attachments to other {@link Set}s. Query of the {@code LinkableSet} will
 * return the union of this set and all attached sets. Modification will always operate this set but never attached sets.
 */
public class LinkableSet<E> implements Set<E>, DirectedGraphNode<LinkableSet<E>>
{

	private Set<Set<E>> attachments = new HashSet<>();
	private HashSet<E> original = new HashSet<>();
	private HashSet<E> cachedHash = new HashSet<>();
	
	public LinkableSet() {}
	
	public LinkableSet(Set<E> external)
	{
		this.attachments.add(external);
	}
	
	protected HashSet<E> updateHashSet()
	{
		cachedHash.clear();
		for (var ex: attachments)
		{
			cachedHash.addAll(ex);
		}
		cachedHash.addAll(original);
		return cachedHash;
	}
	
	protected boolean containsInAttachments(Object o)
	{
		for (var ex: attachments)
		{
			if (ex.contains(o)) return true;
		}
		return false;
	}

	/**
	 *
	 */
	protected void deduplicateOriginal()
	{
		this.original.removeIf(e -> this.containsInAttachments(e));
	}
	
	@Override
	public int size() {
		return this.updateHashSet().size();
	}

	@Override
	public boolean isEmpty() {
		for (var ex: attachments)
		{
			if (!ex.isEmpty())
				return false;
		}
		return this.original.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		for (var ex: attachments)
		{
			if (ex.contains(o))
				return true;
		}
		return this.original.contains(o);
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
		return this.original.add(e);
	}

	@Override
	public boolean remove(Object o) {
		if (this.containsInAttachments(o))
		{
			LogUtils.getLogger().warn("NaUtils#CompoundSet: attempting to remove an element from the external parts. Skipped and returned false.");
			this.deduplicateOriginal();
			return false;
		}
		else if (this.original.contains(o))
			return this.original.remove(o);
		else return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.updateHashSet().containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		int old = this.size();
		this.original.addAll(c);
		this.deduplicateOriginal();
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
		this.attachments.clear();
		this.original.clear();
	}
	
	/**
	 * Clear the mutable part only.
	 */
	public void clearMutable()
	{
		this.original.clear();
	}
	
	public void attach(Set<E> s)
	{
		if (this.getCycle() != null)
			throw new IllegalStateException("LinkableSet: cyclic reference path detected.");
		this.attachments.add(s);
		if (s instanceof LinkableSet<?> && this.getCycle() != null) {
			this.attachments.remove(s);
			throw new IllegalArgumentException("LinkableSet#attach: attachment caused a cyclic reference path.");
		}
	}

	public void removeExternalSet(Set<E> s)
	{
		this.attachments.remove(s);
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
	public LinkableSet<E> createCompoundSet()
	{
		return new LinkableSet<>(this);
	}

	@Override
	public String toString()
	{
		String res = "CompoundSet { EXTERNAL: \n";
		for (var s: this.attachments)
			res = res + s.toString() + ",\n" + "*****\n";
		res = res + "MUTABLE:\n" + this.original.toString() + "\n*****}\n";
		return res;
	}

	@Override
	public Set<LinkableSet<E>> children() {
		return this.attachments.stream().filter(set -> set instanceof LinkableSet<E>)
				.map(set -> (LinkableSet<E>)set).collect(Collectors.toSet());
	}
}
