package net.sodiumstudio.nautils.nbt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.nbt.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.sodiumstudio.nautils.NbtHelper;
import net.sodiumstudio.nautils.Wrapped;

/**
 * NOT FINISHED!
 * An {@code NBTExplorer} is a wrapped NBT that can be browsed by path e.g. "/foo/bar/data.int" for {@code IntTag}.
 */
public class NBTExplorer
{
	
	private final CompoundTag root;
	
	/**
	 * Create an empty {@code NBTExplorer} instance containing only a root {@code CompoundTag}.
	 */
	public NBTExplorer() {
		root = new CompoundTag();
	}
	
	/**
	 * Create an {@code NBTExplorer} containing existing {@code CompoundTag}.
	 */
	public NBTExplorer(CompoundTag root) {
		if (root == null)
			throw new NullPointerException("NBTExplorer constructor receives only non-null CompoundTag. For nullable values, use NBTExplorer#ofNullable.");
		this.root = root.copy();
	}
	
	/**
	 * Create NBTExplorer from a nullable compound tag. If input is null, it will create an empty one.
	 */
	@Nonnull
	public static NBTExplorer ofNullable(@Nullable CompoundTag root)
	{
		return root == null ? new NBTExplorer() : new NBTExplorer(root);
	}
	
	/**
	 * Get the compound tag.
	 * <p> Note: Operation of the returned tag will also take effect in this NBTExplorer!
	 */
	public CompoundTag getTag()
	{
		return root;
	}
	
	
	private String[] parsePath(String path)
	{
		if (path.isEmpty())
			throw new IllegalArgumentException("NBTExplorer path parsing failed: \"" + path + "\".");
		if (path.charAt(0) != '/')
			throw new IllegalArgumentException("NBTExplorer path parsing failed: \"" + path + "\". A path should start and seperated with '/'.");
		String[] splited = path.split("/");
		return (String[]) Arrays.stream(splited).filter(str -> !str.isEmpty()).toArray();
	}
	
	private String assemblePath(String[] parsedPath)
	{
		Wrapped<String> res = new Wrapped<>("");
		Arrays.stream(parsedPath).forEachOrdered(str -> res.set(res.get() + "/" + str));
		return res.get();
	}
	
	private String getCurrentPath(String[] fullPath, String currentPosition)
	{
		int i = 0;
		for (i = 0; i < fullPath.length; ++i)
		{
			if (fullPath[i] == currentPosition)
				break;
		}
		if (i == fullPath.length)
			throw new IllegalArgumentException("NBTExplorer#getCurrentPath: not existing.");
		String res = "";
		for (int j = 0; j <= i ; ++j)
		{
			res = res + "/" + fullPath[j];
		}
		return res;
	}
	
	/**
	 * Get a tag from a path. This operation will not create path.
	 */
	@Nullable
	public Tag get(String path)
	{
		String[] parsedPath = this.parsePath(path);

		if (parsedPath.length == 0)
			return root;
		CompoundTag ptr = root;
		// Go to the target dir
		for (int i = 0; i < parsedPath.length - 1; ++i)
		{
			// Go into the path if exists
			if (ptr.contains(parsedPath[i], NbtHelper.TAG_COMPOUND_ID))
			{
				ptr = ptr.getCompound(parsedPath[i]);
			}
			// Not found
			else
			{
				
				// If the path name exists but isn't a compound tag, this should be an error
				if (ptr.contains(parsedPath[i]))
				{
					// Assemble current position for exception report
					String currentPos = "";
					for (int j = 0; j <= i; ++j)
					{
						currentPos = currentPos + "/" + parsedPath[j];
					}
					throw new IllegalArgumentException("NBTExplorer error finding path: \"" + currentPos
							+ "\" is a " + getTagTypeString(ptr.get(parsedPath[i])) + ".");
				}
				// Otherwise the tag doesn't exist at all
				return null;
			}
		}
		// Get if exists
		if (ptr.contains(parsedPath[parsedPath.length - 1]))
			return ptr.get(parsedPath[parsedPath.length - 1]);
		else return null;		
	}

	@Nonnull
	public Tag getOrCreate(String path, Supplier<Tag> creatingMethod)
	{
		String[] parsedPath = this.parsePath(path);

		if (parsedPath.length == 0)
			return root;
		CompoundTag ptr = root;
		// Go to the target dir
		for (int i = 0; i < parsedPath.length - 1; ++i)
		{
			// Go into the path if exists
			if (ptr.contains(parsedPath[i], NbtHelper.TAG_COMPOUND_ID))
			{
				ptr = ptr.getCompound(parsedPath[i]);
			}
			// Not found
			else
			{
				// If the path name exists but isn't a compound tag, this should be an error
				if (ptr.contains(parsedPath[i]))
				{
					// Assemble current position for exception report
					String currentPos = "";
					for (int j = 0; j <= i; ++j)
					{
						currentPos = currentPos + "/" + parsedPath[j];
					}
					throw new IllegalArgumentException("NBTExplorer error finding path: \"" + currentPos
							+ "\" is a " + getTagTypeString(ptr.get(parsedPath[i])) + ".");
				}
				else
				{
					ptr.put(parsedPath[i], new CompoundTag());
					ptr = ptr.getCompound(parsedPath[i]);
				}
			}
		}
		// Get if exists
		if (ptr.contains(parsedPath[parsedPath.length - 1]))
			return ptr.get(parsedPath[parsedPath.length - 1]);
		// Otherwise create
		else
		{
			ptr.put(parsedPath[parsedPath.length - 1], creatingMethod.get());
			return ptr.get(parsedPath[parsedPath.length - 1]);
		}
	}
	
	public void put(String path, Tag inTag)
	{
		String[] parsedPath = this.parsePath(path);

		// Invalid path, throw
		if (parsedPath.length == 0)
			throw new IllegalArgumentException("NBTExplorer#put: empty path.");
		CompoundTag ptr = root;
		// Go to the target dir
		for (int i = 0; i < parsedPath.length - 1; ++i)
		{
			// Go into the path if exists
			if (ptr.contains(parsedPath[i], NbtHelper.TAG_COMPOUND_ID))
			{
				ptr = ptr.getCompound(parsedPath[i]);
			}
			// Not found
			else
			{
				
				// If the path name exists but isn't a compound tag, this should be an error
				if (ptr.contains(parsedPath[i]))
				{
					// Assemble current position for exception report
					String currentPos = "";
					for (int j = 0; j <= i; ++j)
					{
						currentPos = currentPos + "/" + parsedPath[j];
					}
					throw new IllegalArgumentException("NBTExplorer error finding path: \"" + currentPos
							+ "\" is a " + getTagTypeString(ptr.get(parsedPath[i])) + ".");
				}
				else
				{
					ptr.put(parsedPath[i], new CompoundTag());
					ptr = ptr.getCompound(parsedPath[i]);
				}
			}
		}
		// Put tag in
		ptr.put(parsedPath[parsedPath.length - 1], inTag);	
	}
	
	public void remove(String path)
	{
		String[] parsedPath = this.parsePath(path);

		// Invalid path, throw
		if (parsedPath.length == 0)
			throw new IllegalArgumentException("NBTExplorer#remove: empty path.");
		CompoundTag ptr = root;
		// Go to the target dir
		for (int i = 0; i < parsedPath.length - 1; ++i)
		{
			// Go into the path if exists
			if (ptr.contains(parsedPath[i], NbtHelper.TAG_COMPOUND_ID))
			{
				ptr = ptr.getCompound(parsedPath[i]);
			}
			// Not found
			else
			{
				// Do nothing
				return;
			}
		}
		// Put tag in
		ptr.remove(parsedPath[parsedPath.length - 1]);	
	}
	
	@Nullable
	public <T extends Tag> T getWithType(String key, Class<T> type)
	{
		Tag tag = this.get(key);
		if (tag == null) return null;
		if (tag.getClass().isAssignableFrom(type))
			return (T) tag;
		else return null;
	}
	
	@Nullable
	public <T extends Tag> T getOrCreateWithType(String key, Supplier<T> creatingMethod, Class<T> type)
	{
		Tag tag = this.getOrCreate(key, () -> (T) (creatingMethod.get()));
		if (tag.getClass().isAssignableFrom(type))
			return (T) tag;
		else return null;
	}

	private static String getTagTypeString(@Nonnull Tag tag)
	{
		if (tag instanceof ByteTag)
			return "ByteTag";
		if (tag instanceof ShortTag)
			return "ShortTag";
		if (tag instanceof IntTag)
			return "IntTag";
		if (tag instanceof FloatTag)
			return "FloatTag";
		if (tag instanceof DoubleTag)
			return "DoubleTag";
		if (tag instanceof LongTag)
			return "LongTag";
		if (tag instanceof StringTag)
			return "StringTag";
		if (tag instanceof ListTag)
			return "ListTag";
		if (tag instanceof CompoundTag)
			return "CompoundTag";
		if (tag instanceof IntArrayTag)
			return "IntArrayTag";
		if (tag instanceof ByteArrayTag)
			return "ByteArrayTag";
		if (tag instanceof LongArrayTag)
			return "LongArrayTag";
		if (tag instanceof EndTag)
			return "EndTag";
		return tag.getClass().toGenericString();	
	}

}
