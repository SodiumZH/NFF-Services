package net.sodiumzh.nautils.network;

import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.sodiumzh.nautils.NaUtils;
import net.sodiumzh.nautils.math.LinearColor;
import net.sodiumzh.nautils.math.RangedRandomDouble;
import net.sodiumzh.nautils.math.RangedRandomInt;
import net.sodiumzh.nautils.registries.NaUtilsRegistries;
import net.sodiumzh.nautils.registries.RegistryEntryCollection;

import java.util.UUID;

public class NaUtilsDataSerializers {
    public static RegistryEntryCollection<NaUtilsDataSerializer<?>> SERIALIZERS =
            RegistryEntryCollection.create(NaUtilsRegistries.DATA_SERIALIZERS, NaUtils.MOD_ID);

    public static final NaUtilsDataSerializer<Boolean> BOOLEAN = NaUtilsDataSerializer.create(
            Boolean.class, ByteTag.class,
            FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean, ByteTag::valueOf, t -> t.getAsByte() != 0);
    public static final NaUtilsDataSerializer<Integer> INT = NaUtilsDataSerializer.create(
            Integer.class, IntTag.class,
            FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt, IntTag::valueOf, IntTag::getAsInt);
    public static final NaUtilsDataSerializer<Long> LONG = NaUtilsDataSerializer.create(
            Long.class, LongTag.class,
            FriendlyByteBuf::writeLong, FriendlyByteBuf::readLong, LongTag::valueOf, LongTag::getAsLong);
    public static final NaUtilsDataSerializer<Double> DOUBLE = NaUtilsDataSerializer.create(
            Double.class, DoubleTag.class,
            FriendlyByteBuf::writeDouble, FriendlyByteBuf::readDouble, DoubleTag::valueOf, DoubleTag::getAsDouble);
    public static final NaUtilsDataSerializer<java.util.UUID> UUID = NaUtilsDataSerializer.create(
            UUID.class, IntArrayTag.class,
            FriendlyByteBuf::writeUUID, FriendlyByteBuf::readUUID, NbtUtils::createUUID, NbtUtils::loadUUID);
    public static final NaUtilsDataSerializer<String> STRING = NaUtilsDataSerializer.create(
            String.class, StringTag.class,
            FriendlyByteBuf::writeUtf, FriendlyByteBuf::readUtf, StringTag::valueOf, StringTag::getAsString);
    public static final NaUtilsDataSerializer<ResourceLocation> RESOURCE_LOCATION = NaUtilsDataSerializer.castTo(
            ResourceLocation.class,
            STRING, ResourceLocation::new, ResourceLocation::toString);
    public static final NaUtilsDataSerializer<int[]> INT_ARRAY = NaUtilsDataSerializer.create(
            int[].class, IntArrayTag.class,
            (b, o) -> {
                b.writeInt(o.length);
                for (int i = 0; i < o.length; ++i)
                    b.writeInt(o[i]);
            }, b -> {
                int l = b.readInt();
                int[] res = new int[l];
                for (int i = 0; i < l; ++i)
                    res[i] = b.readInt();
                return res;
            }, IntArrayTag::new, IntArrayTag::getAsIntArray);
    public static final NaUtilsDataSerializer<double[]> DOUBLE_ARRAY = NaUtilsDataSerializer.create(
            double[].class, ListTag.class,
            (b, o) -> {
                b.writeInt(o.length);
                for (int i = 0; i < o.length; ++i)
                    b.writeDouble(o[i]);
            }, b -> {
                int l = b.readInt();
                double[] res = new double[l];
                for (int i = 0; i < l; ++i)
                    res[i] = b.readDouble();
                return res;
            }, o -> {
                ListTag tag = new ListTag();
                for (int i = 0; i < o.length; ++i)
                    tag.add(DoubleTag.valueOf(o[i]));
                return tag;
            }, t -> {
                double[] res = new double[t.size()];
                for (int i = 0; i < t.size(); ++i)
                    res[i] = t.getDouble(i);
                return res;
            });
    public static final NaUtilsDataSerializer<Vec3> VEC3 = NaUtilsDataSerializer.create(
            Vec3.class, ListTag.class,
            (b, o) -> {b.writeDouble(o.x); b.writeDouble(o.y); b.writeDouble(o.z);},
            (b) -> new Vec3(b.readDouble(), b.readDouble(), b.readDouble()),
            (o) -> {
                ListTag listtag = new ListTag();
                listtag.add(DoubleTag.valueOf(o.x));
                listtag.add(DoubleTag.valueOf(o.y));
                listtag.add(DoubleTag.valueOf(o.z));
                return listtag;
            }, (t) -> new Vec3(t.getDouble(0), t.getDouble(1), t.getDouble(2)));
    public static final NaUtilsDataSerializer<LinearColor> LINEAR_COLOR = NaUtilsDataSerializer.castTo(
            LinearColor.class, VEC3,
            LinearColor::fromNormalized, c -> new Vec3(c.r, c.g, c.b));
    public static final NaUtilsDataSerializer<ItemStack> ITEM_STACK = NaUtilsDataSerializer.create(
            ItemStack.class, CompoundTag.class,
            FriendlyByteBuf::writeItem, FriendlyByteBuf::readItem,
            (i) -> {CompoundTag res = new CompoundTag(); i.save(res); return res;},
            ItemStack::of);
    public static final NaUtilsDataSerializer<ItemStack> ITEM_STACK_FULL_TAG = NaUtilsDataSerializer.create(
            ItemStack.class, CompoundTag.class,
            (b, i) -> b.writeItemStack(i, false), FriendlyByteBuf::readItem,
            (i) -> {CompoundTag res = new CompoundTag(); i.save(res); return res;},
            ItemStack::of);

    public static final NaUtilsDataSerializer<RangedRandomDouble> RANGED_RANDOM_DOUBLE =
            NaUtilsDataSerializer.castTo(RangedRandomDouble.class, DOUBLE_ARRAY,
                    RangedRandomDouble::fromArrayRepresentation, RangedRandomDouble::toArrayRepresentation);

    public static final NaUtilsDataSerializer<RangedRandomInt> RANGED_RANDOM_INT =
            NaUtilsDataSerializer.castTo(RangedRandomInt.class, DOUBLE_ARRAY,
                RangedRandomInt::fromArrayRepresentation, RangedRandomInt::toArrayRepresentation);

    static {
        SERIALIZERS.register("boolean", () -> BOOLEAN);
        SERIALIZERS.register("int", () -> INT);
        SERIALIZERS.register("long", () -> LONG);
        SERIALIZERS.register("double", () -> DOUBLE);
        SERIALIZERS.register("uuid", () -> UUID);
        SERIALIZERS.register("string", () -> STRING);
        SERIALIZERS.register("resource_location", () -> RESOURCE_LOCATION);
        SERIALIZERS.register("int_array", () -> INT_ARRAY);
        SERIALIZERS.register("double_array", () -> DOUBLE_ARRAY);
        SERIALIZERS.register("vec3", () -> VEC3);
        SERIALIZERS.register("linear_color", () -> LINEAR_COLOR);
        SERIALIZERS.register("item_stack", () -> ITEM_STACK);
        SERIALIZERS.register("item_stack_full_tag", () -> ITEM_STACK_FULL_TAG);
        SERIALIZERS.register("ranged_random_double", () -> RANGED_RANDOM_DOUBLE);
        SERIALIZERS.register("ranged_random_int", () -> RANGED_RANDOM_INT);
    }
}
