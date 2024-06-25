package net.sodiumstudio.nautils.nbt;

import java.util.HashMap;

import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Tuple;
import net.minecraftforge.common.util.INBTSerializable;

@Deprecated
public class NBTSerializableMap extends HashMap<String, SerializableField<?>> implements INBTSerializable<CompoundTag>
{

	private static final long serialVersionUID = -8107904071739824491L;

	@Override
	public CompoundTag serializeNBT() {
		CompoundTag tag = new CompoundTag();
		for (var entry: entrySet())
		{
			tag.put(entry.getKey(), entry.getValue().serialize());
		}
		return tag;
	}
	
	@Override
	public void deserializeNBT(CompoundTag nbt) {
		this.clear();
		for (String key: nbt.getAllKeys())
		{
			switch (nbt.get(key).getId())
			{
			case Tag.TAG_BYTE: {this.put(key, new SerializableField.Byte(nbt.getByte(key))); break;}
			case Tag.TAG_SHORT: {this.put(key, new SerializableField.Short(nbt.getShort(key))); break;}
			case Tag.TAG_INT: {this.put(key, new SerializableField.Int(nbt.getInt(key))); break;}
			case Tag.TAG_LONG: {this.put(key, new SerializableField.Long(nbt.getLong(key))); break;}
			case Tag.TAG_FLOAT: {this.put(key, new SerializableField.Float(nbt.getShort(key))); break;}
			case Tag.TAG_DOUBLE: {this.put(key, new SerializableField.Double(nbt.getDouble(key))); break;}
			case Tag.TAG_STRING: {this.put(key, new SerializableField.String(nbt.getString(key))); break;}
			case Tag.TAG_INT_ARRAY: {this.put(key, new SerializableField.IntArray(nbt.getIntArray(key))); break;}
			case Tag.TAG_BYTE_ARRAY: {this.put(key, new SerializableField.ByteArray(nbt.getByteArray(key))); break;}
			case Tag.TAG_LONG_ARRAY: {this.put(key, new SerializableField.LongArray(nbt.getLongArray(key))); break;}
			case Tag.TAG_LIST:
			{
				ListTag t = (ListTag)(nbt.get(key));
				switch (t.getElementType())
				{
				case Tag.TAG_FLOAT: {
					float[] arr = new float[t.size()];
					for (int i = 0; i < t.size(); ++i)
						arr[i] = t.getFloat(i);
					this.put(key, new SerializableField.FloatArray(arr));
					break;
				}
				case Tag.TAG_DOUBLE: {
					double[] arr = new double[t.size()];
					for (int i = 0; i < t.size(); ++i)
						arr[i] = t.getDouble(i);
					this.put(key, new SerializableField.DoubleArray(arr));
					break;
				}
				case Tag.TAG_STRING: {
					String[] arr = new String[t.size()];
					for (int i = 0; i < t.size(); ++i)
						arr[i] = t.getString(i);
					this.put(key, new SerializableField.StringArray(arr));
					break;
				}
				default:
					throw new RuntimeException();
				}
			}
			case Tag.TAG_COMPOUND:
			{
				Tuple<String, Tag> spc = SerializableField.getSpecialCompoundType(nbt.getCompound(key));
				if (spc != null)
				{
					switch (spc.getA())
					{
					case "ITEM_STACK": {this.put(key, SerializableField.ItemStack.deserialize(nbt.getCompound(key))); break;}
					case "UUID": {this.put(key, SerializableField.UUID.deserialize(nbt.getCompound(key))); break;}
					default: throw new RuntimeException();
					}
				}
				else {
					NBTSerializableMap map = new NBTSerializableMap();
					map.deserializeNBT(nbt.getCompound(key));
					this.put(key, new SerializableField.Compound(map)); 
				}
				break;
			}
			default: throw new RuntimeException();
			}
		}
	}
	
	public byte getByte(String key)
	{
		if (this.get(key) != null && this.get(key) instanceof SerializableField.Byte b)
			return b.get();
		else return 0;
	}
	
	public boolean getBoolean(String key)
	{
		return getByte(key) != 0;
	}
	
	public short getShort(String key)
	{
		var f = this.get(key);
		if (f != null)
		{
			if (f instanceof SerializableField.Byte fc)
				return fc.get();
			else if (f instanceof SerializableField.Short fc)
				return fc.get();
		}
		return 0;
	}
	
	public int getInt(String key)
	{
		var f = this.get(key);
		if (f != null)
		{
			if (f instanceof SerializableField.Byte fc)
				return fc.get();
			else if (f instanceof SerializableField.Short fc)
				return fc.get();
			else if (f instanceof SerializableField.Int fc)
				return fc.get();
		}
		return 0;
	}
	
	public long getLong(String key)
	{
		var f = this.get(key);
		if (f != null)
		{
			if (f instanceof SerializableField.Byte fc)
				return fc.get();
			else if (f instanceof SerializableField.Short fc)
				return fc.get();
			else if (f instanceof SerializableField.Int fc)
				return fc.get();
			else if (f instanceof SerializableField.Long fc)
				return fc.get();
		}
		return 0;
	}

	public float getFloat(String key)
	{
		var f = this.get(key);
		if (f != null)
		{
			if (f instanceof SerializableField.Float fc)
				return fc.get();
		}
		return 0;
	}
	
	public double getDouble(String key)
	{
		var f = this.get(key);
		if (f != null)
		{
			if (f instanceof SerializableField.Float fc)
				return fc.get();
			else if (f instanceof SerializableField.Double fc)
				return fc.get();
		}
		return 0;
	}
	
	public String getString(String key)
	{
		if (this.get(key) != null && this.get(key) instanceof SerializableField.String b)
			return b.get();
		else return "";
	}
	
	public NBTSerializableMap getMap(String key)
	{
		if (this.get(key) != null && this.get(key) instanceof SerializableField.Compound b)
			return b.get();
		else return new NBTSerializableMap();
	}
	
	public byte[] getByteArray(String key)
	{
		if (this.get(key) != null && this.get(key) instanceof SerializableField.ByteArray b)
			return b.get();
		else return new byte[] {};
	}
	
	public int[] getIntArray(String key)
	{
		if (this.get(key) != null && this.get(key) instanceof SerializableField.IntArray b)
			return b.get();
		else return new int[] {};
	}
	
	public long[] getLongArray(String key)
	{
		if (this.get(key) != null && this.get(key) instanceof SerializableField.LongArray b)
			return b.get();
		else return new long[] {};
	}
	
	public float[] getFloatArray(String key)
	{
		if (this.get(key) != null && this.get(key) instanceof SerializableField.FloatArray b)
			return b.get();
		else return new float[] {};
	}
}
