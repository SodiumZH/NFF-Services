package net.sodiumzh.nfu.network;

import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.sodiumzh.nfu.NFULibrary;
import net.sodiumzh.nfu.math.*;
import net.sodiumzh.nfu.registry.NFURegistries;
import net.sodiumzh.nfu.registry.NFURegistryEntryCollection;

import java.util.Optional;
import java.util.UUID;

public class NFUDataSerializers {
    public static NFURegistryEntryCollection<NFUDataSerializer<?>> SERIALIZERS =
            NFURegistryEntryCollection.create(NFURegistries.DATA_SERIALIZERS, NFULibrary.MOD_ID);

    public static final NFUDataSerializer<Boolean> BOOLEAN = NFUDataSerializer.create(
            Boolean.class, ByteTag.class,
            FriendlyByteBuf::writeBoolean, FriendlyByteBuf::readBoolean, ByteTag::valueOf, t -> t.getAsByte() != 0);
    public static final NFUDataSerializer<Integer> INT = NFUDataSerializer.create(
            Integer.class, IntTag.class,
            FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt, IntTag::valueOf, IntTag::getAsInt);
    public static final NFUDataSerializer<Long> LONG = NFUDataSerializer.create(
            Long.class, LongTag.class,
            FriendlyByteBuf::writeLong, FriendlyByteBuf::readLong, LongTag::valueOf, LongTag::getAsLong);
    public static final NFUDataSerializer<Double> DOUBLE = NFUDataSerializer.create(
            Double.class, DoubleTag.class,
            FriendlyByteBuf::writeDouble, FriendlyByteBuf::readDouble, DoubleTag::valueOf, DoubleTag::getAsDouble);
    public static final NFUDataSerializer<java.util.UUID> UUID = NFUDataSerializer.create(
            UUID.class, IntArrayTag.class,
            FriendlyByteBuf::writeUUID, FriendlyByteBuf::readUUID, NbtUtils::createUUID, NbtUtils::loadUUID);
    public static final NFUDataSerializer<String> STRING = NFUDataSerializer.create(
            String.class, StringTag.class,
            FriendlyByteBuf::writeUtf, FriendlyByteBuf::readUtf, StringTag::valueOf, StringTag::getAsString);
    public static final NFUDataSerializer<ResourceLocation> RESOURCE_LOCATION = NFUDataSerializer.castTo(
            ResourceLocation.class,
            STRING, ResourceLocation::new, ResourceLocation::toString);
    public static final NFUDataSerializer<int[]> INT_ARRAY = NFUDataSerializer.create(
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
    public static final NFUDataSerializer<double[]> DOUBLE_ARRAY = NFUDataSerializer.create(
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
    public static final NFUDataSerializer<Vec3> VEC3 = NFUDataSerializer.create(
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
    public static final NFUDataSerializer<LinearColor> LINEAR_COLOR = NFUDataSerializer.castTo(
            LinearColor.class, VEC3,
            LinearColor::fromNormalized, c -> new Vec3(c.r, c.g, c.b));
    public static final NFUDataSerializer<ItemStack> ITEM_STACK = NFUDataSerializer.create(
            ItemStack.class, CompoundTag.class,
            FriendlyByteBuf::writeItem, FriendlyByteBuf::readItem,
            (i) -> {CompoundTag res = new CompoundTag(); i.save(res); return res;},
            ItemStack::of);
    public static final NFUDataSerializer<ItemStack> ITEM_STACK_FULL_TAG = NFUDataSerializer.create(
            ItemStack.class, CompoundTag.class,
            (b, i) -> b.writeItemStack(i, false), FriendlyByteBuf::readItem,
            (i) -> {CompoundTag res = new CompoundTag(); i.save(res); return res;},
            ItemStack::of);

    public static final NFUDataSerializer<RangedRandomDouble> RANGED_RANDOM_DOUBLE =
            NFUDataSerializer.castTo(RangedRandomDouble.class, DOUBLE_ARRAY,
                    RangedRandomDouble::fromArrayRepresentation, RangedRandomDouble::toArrayRepresentation);

    public static final NFUDataSerializer<RangedRandomInt> RANGED_RANDOM_INT =
            NFUDataSerializer.castTo(RangedRandomInt.class, DOUBLE_ARRAY,
                RangedRandomInt::fromArrayRepresentation, RangedRandomInt::toArrayRepresentation);

    public static final NFUDataSerializer<AABB> BOUNDING_BOX =
            NFUDataSerializer.castTo(AABB.class, DOUBLE_ARRAY,
                o -> new AABB(o[0], o[1], o[2], o[3], o[4], o[5]),
                o -> new double[]{o.minX, o.minY, o.minZ, o.maxX, o.maxY, o.maxZ});

    public static final NFUDataSerializer<Inequality3D> INEQUALITY_3D =
        NFUDataSerializer.create(Inequality3D.class, CompoundTag.class,
            (b, i) -> {
                b.writeUtf(IInequalityPattern3D.REGISTRY.getKey(i.pattern).toString());
                VEC3.write(b, i.scale);
                VEC3.write(b, i.translation);
                BOUNDING_BOX.write(b, i.defDomain);
            }, b -> new Inequality3D(IInequalityPattern3D.REGISTRY.getValue(new ResourceLocation(b.readUtf())),
                VEC3.read(b), VEC3.read(b), BOUNDING_BOX.read(b)),
            i -> {
                CompoundTag res = new CompoundTag();
                res.putString("pattern", IInequalityPattern3D.REGISTRY.getKey(i.pattern).toString());
                res.put("scale", VEC3.toTag(i.scale));
                res.put("translation", VEC3.toTag(i.translation));
                res.put("defDomain", BOUNDING_BOX.toTag(i.defDomain));
                return res;
            }, t -> new Inequality3D(IInequalityPattern3D.REGISTRY.getValue(new ResourceLocation(t.getString("pattern"))),
                VEC3.fromTag(t.get("scale")), VEC3.fromTag(t.get("translation")), BOUNDING_BOX.fromTag(t.get("defDomain")))
        );

    public static final NFUDataSerializer<Optional<Inequality3D>> OPTIONAL_INEQUALITY_3D =
        NFUDataSerializer.optionalOf(INEQUALITY_3D);

    public static final NFUDataSerializer<Field3D> FIELD_3D =
        NFUDataSerializer.create(Field3D.class, CompoundTag.class,
            (b, i) -> {
                b.writeUtf(IFieldPattern3D.REGISTRY.getKey(i.pattern).toString());
                VEC3.write(b, i.spaceScale);
                VEC3.write(b, i.valueScale);
                VEC3.write(b, i.translation);
                VEC3.write(b, i.valueAddition);
                OPTIONAL_INEQUALITY_3D.write(b, Optional.ofNullable(i.baseDefinitionDomain));
            }, b -> new Field3D(IFieldPattern3D.REGISTRY.getValue(new ResourceLocation(b.readUtf())),
                VEC3.read(b), VEC3.read(b), VEC3.read(b), VEC3.read(b), OPTIONAL_INEQUALITY_3D.read(b).orElse(null)),
            i -> {
                CompoundTag res = new CompoundTag();
                res.putString("pattern", IFieldPattern3D.REGISTRY.getKey(i.pattern).toString());
                res.put("spaceScale", VEC3.toTag(i.spaceScale));
                res.put("valueScale", VEC3.toTag(i.valueScale));
                res.put("translation", VEC3.toTag(i.translation));
                res.put("valueAddition", VEC3.toTag(i.valueAddition));
                res.put("defDomain", OPTIONAL_INEQUALITY_3D.toTag(Optional.ofNullable(i.baseDefinitionDomain)));
                return res;
            }, t -> new Field3D(IFieldPattern3D.REGISTRY.getValue(new ResourceLocation(t.getString("pattern"))),
                VEC3.fromTag(t.get("spaceScale")), VEC3.fromTag(t.get("valueScale")), VEC3.fromTag(t.get("translation")),
                VEC3.fromTag(t.get("valueAddition")), OPTIONAL_INEQUALITY_3D.fromTag(t.get("defDomain")).orElse(null))
    );

    public static final NFUDataSerializer<Optional<Field3D>> OPTIONAL_FIELD_3D =
        NFUDataSerializer.optionalOf(FIELD_3D);

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
        SERIALIZERS.register("bounding_box", () -> BOUNDING_BOX);
        SERIALIZERS.register("inequality_3d", () -> INEQUALITY_3D);
        SERIALIZERS.register("optional_inequality_3d", () -> OPTIONAL_INEQUALITY_3D);
        SERIALIZERS.register("field_3d", () -> FIELD_3D);
        SERIALIZERS.register("optional_field_3d", () -> OPTIONAL_FIELD_3D);
    }
}
