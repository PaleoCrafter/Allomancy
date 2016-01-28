package de.mineformers.investiture.serialisation;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Table;
import de.mineformers.investiture.network.ManualTranslation;
import de.mineformers.investiture.network.Message;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import org.apache.commons.lang3.ClassUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.StreamSupport;

/**
 * Internal handler for all networking serialisation needs.
 * <p>
 * WARNING: This class is an implementation detail and not supposed to be called by anything directly but {@link Message}, so binary incompatible
 * API changes can occur at random.
 */
public class Serialisation
{
    public static final Serialisation INSTANCE = new Serialisation();
    private Map<Class<?>, Translator<?, ?>> translators = new HashMap<>();
    private Multimap<String, FieldData> fields = HashMultimap.create();
    private Table<String, String, Translator<?, ?>> fieldTranslators = HashBasedTable.create();

    /**
     * Constructor that registers default serialisation.
     * <p>
     * It's private because there should only ever be one {@link Serialisation#INSTANCE instance} of this class.
     */
    private Serialisation()
    {
        // String translator
        registerTranslator(String.class, new Translator<String, NBTTagString>()
        {
            @Override
            public void serialiseImpl(String value, ByteBuf buffer)
            {
                ByteBufUtils.writeUTF8String(buffer, value);
            }

            @Override
            public String deserialiseImpl(ByteBuf buffer)
            {
                return ByteBufUtils.readUTF8String(buffer);
            }

            @Override
            public NBTTagString serialiseImpl(String value)
            {
                return new NBTTagString(value);
            }

            @Override
            public String deserialiseImpl(NBTTagString tag)
            {
                return tag.getString();
            }
        });

        // int translator
        registerTranslator(Integer.TYPE, new Translator<Integer, NBTTagInt>()
        {
            @Override
            public void serialiseImpl(Integer value, ByteBuf buffer)
            {
                buffer.writeInt(value);
            }

            @Override
            public Integer deserialiseImpl(ByteBuf buffer)
            {
                return buffer.readInt();
            }

            @Override
            public NBTTagInt serialiseImpl(Integer value)
            {
                return new NBTTagInt(value);
            }

            @Override
            public Integer deserialiseImpl(NBTTagInt tag)
            {
                return tag.getInt();
            }
        });

        // byte translator
        registerTranslator(Byte.TYPE, new Translator<Byte, NBTTagByte>()
        {
            @Override
            public void serialiseImpl(Byte value, ByteBuf buffer)
            {
                buffer.writeByte(value);
            }

            @Override
            public Byte deserialiseImpl(ByteBuf buffer)
            {
                return buffer.readByte();
            }

            @Override
            public NBTTagByte serialiseImpl(Byte value)
            {
                return new NBTTagByte(value);
            }

            @Override
            public Byte deserialiseImpl(NBTTagByte tag)
            {
                return tag.getByte();
            }
        });

        // short translator
        registerTranslator(Short.TYPE, new Translator<Short, NBTTagShort>()
        {
            @Override
            public void serialiseImpl(Short value, ByteBuf buffer)
            {
                buffer.writeShort(value);
            }

            @Override
            public Short deserialiseImpl(ByteBuf buffer)
            {
                return buffer.readShort();
            }

            @Override
            public NBTTagShort serialiseImpl(Short value)
            {
                return new NBTTagShort(value);
            }

            @Override
            public Short deserialiseImpl(NBTTagShort tag)
            {
                return tag.getShort();
            }
        });

        // long translator
        registerTranslator(Long.TYPE, new Translator<Long, NBTTagLong>()
        {
            @Override
            public void serialiseImpl(Long value, ByteBuf buffer)
            {
                buffer.writeLong(value);
            }

            @Override
            public Long deserialiseImpl(ByteBuf buffer)
            {
                return buffer.readLong();
            }

            @Override
            public NBTTagLong serialiseImpl(Long value)
            {
                return new NBTTagLong(value);
            }

            @Override
            public Long deserialiseImpl(NBTTagLong tag)
            {
                return tag.getLong();
            }
        });

        // char translator
        registerTranslator(Character.TYPE, new Translator<Character, NBTTagInt>()
        {
            @Override
            public void serialiseImpl(Character value, ByteBuf buffer)
            {
                buffer.writeChar(value);
            }

            @Override
            public Character deserialiseImpl(ByteBuf buffer)
            {
                return buffer.readChar();
            }

            @Override
            public NBTTagInt serialiseImpl(Character value)
            {
                return new NBTTagInt(value);
            }

            @Override
            public Character deserialiseImpl(NBTTagInt tag)
            {
                return (char) tag.getInt();
            }
        });

        // boolean translator
        registerTranslator(Boolean.TYPE, new Translator<Boolean, NBTTagByte>()
        {
            @Override
            public void serialiseImpl(Boolean value, ByteBuf buffer)
            {
                buffer.writeBoolean(value);
            }

            @Override
            public Boolean deserialiseImpl(ByteBuf buffer)
            {
                return buffer.readBoolean();
            }

            @Override
            public NBTTagByte serialiseImpl(Boolean value)
            {
                return new NBTTagByte((byte) (value ? 1 : 0));
            }

            @Override
            public Boolean deserialiseImpl(NBTTagByte tag)
            {
                return tag.getByte() > 0;
            }
        });

        // float translator
        registerTranslator(Float.TYPE, new Translator<Float, NBTTagFloat>()
        {
            @Override
            public void serialiseImpl(Float value, ByteBuf buffer)
            {
                buffer.writeFloat(value);
            }

            @Override
            public Float deserialiseImpl(ByteBuf buffer)
            {
                return buffer.readFloat();
            }

            @Override
            public NBTTagFloat serialiseImpl(Float value)
            {
                return new NBTTagFloat(value);
            }

            @Override
            public Float deserialiseImpl(NBTTagFloat tag)
            {
                return tag.getFloat();
            }
        });

        // double translator
        registerTranslator(Double.TYPE, new Translator<Double, NBTTagDouble>()
        {
            @Override
            public void serialiseImpl(Double value, ByteBuf buffer)
            {
                buffer.writeDouble(value);
            }

            @Override
            public Double deserialiseImpl(ByteBuf buffer)
            {
                return buffer.readDouble();
            }

            @Override
            public NBTTagDouble serialiseImpl(Double value)
            {
                return new NBTTagDouble(value);
            }

            @Override
            public Double deserialiseImpl(NBTTagDouble tag)
            {
                return tag.getDouble();
            }
        });

        // ItemStack translator
        registerTranslator(ItemStack.class, new Translator<ItemStack, NBTTagCompound>()
        {
            @Override
            public void serialiseImpl(ItemStack value, ByteBuf buffer)
            {
                ByteBufUtils.writeItemStack(buffer, value);
            }

            @Override
            public ItemStack deserialiseImpl(ByteBuf buffer)
            {
                return ByteBufUtils.readItemStack(buffer);
            }

            @Override
            public NBTTagCompound serialiseImpl(ItemStack value)
            {
                NBTTagCompound result = new NBTTagCompound();
                value.writeToNBT(result);
                return result;
            }

            @Override
            public ItemStack deserialiseImpl(NBTTagCompound tag)
            {
                return ItemStack.loadItemStackFromNBT(tag);
            }
        });

        // NBT compound translator
        registerTranslator(NBTTagCompound.class, new Translator<NBTTagCompound, NBTTagCompound>()
        {
            @Override
            public void serialiseImpl(NBTTagCompound value, ByteBuf buffer)
            {
                ByteBufUtils.writeTag(buffer, value);
            }

            @Override
            public NBTTagCompound deserialiseImpl(ByteBuf buffer)
            {
                return ByteBufUtils.readTag(buffer);
            }

            @Override
            public NBTTagCompound serialiseImpl(NBTTagCompound value)
            {
                return value;
            }

            @Override
            public NBTTagCompound deserialiseImpl(NBTTagCompound tag)
            {
                return tag;
            }
        });

        // Direction translator
        registerTranslator(EnumFacing.class, new Translator<EnumFacing, NBTTagByte>()
        {
            @Override
            public void serialiseImpl(EnumFacing value, ByteBuf buffer)
            {
                buffer.writeInt(value.ordinal());
            }

            @Override
            public EnumFacing deserialiseImpl(ByteBuf buffer)
            {
                return EnumFacing.values()[buffer.readInt()];
            }

            @Override
            public NBTTagByte serialiseImpl(EnumFacing value)
            {
                return new NBTTagByte((byte) value.getIndex());
            }

            @Override
            public EnumFacing deserialiseImpl(NBTTagByte tag)
            {
                return EnumFacing.getFront(tag.getByte());
            }
        });

        // Position translator
        registerTranslator(BlockPos.class, new Translator<BlockPos, NBTTagLong>()
        {
            @Override
            public void serialiseImpl(BlockPos value, ByteBuf buffer)
            {
                buffer.writeLong(value.toLong());
            }

            @Override
            public BlockPos deserialiseImpl(ByteBuf buffer)
            {
                return BlockPos.fromLong(buffer.readLong());
            }

            @Override
            public NBTTagLong serialiseImpl(BlockPos value)
            {
                return new NBTTagLong(value.toLong());
            }

            @Override
            public BlockPos deserialiseImpl(NBTTagLong tag)
            {
                return BlockPos.fromLong(tag.getLong());
            }
        });
    }

    /**
     * Internal method for adding a translator to the framework.
     *
     * @param type       the type the translator supports
     * @param translator the translator
     */
    public void registerTranslator(Class<?> type, Translator<?, ?> translator)
    {
        translators.put(type, translator);
    }

    /**
     * Finds a translator for a given type. If there is no direct translation available, the first translator that can handle a super class of the
     * type will be used.
     *
     * @param type the type to find a translator for
     * @return a translator for the given type, either one that directly supports the type or one for a super type
     */
    private Translator<?, ?> findTranslator(Class<?> type)
    {
        // Direct translation available, short circuit
        if (translators.containsKey(type)) return translators.get(type);

        // No direct translation available, we have to find one that fits the type nonetheless
        Optional<Translator<?, ?>> fit = translators.entrySet().stream()
                                                    .filter(e -> e.getKey().isAssignableFrom(type))
                                                    .findFirst()
                                                    .map(Map.Entry::getValue);
        if (fit.isPresent())
        {
            return fit.get();
        }
        else
        // There doesn't seem to be a translator for this type, we can't handle this particular situation gracefully
        {
            throw new RuntimeException("There is no translator for type " + type.getName() + ", consider writing one.");
        }
    }

    /**
     * Registers a message to the serialisation framework. Allows faster serialisation due to caching of the results of intensive reflective
     * operations.
     *
     * @param type the class representing the type of the message
     */
    public void registerClass(Class<?> type, boolean onlyAnnotated)
    {
        StreamSupport
            .stream(ClassUtils.hierarchy(type, ClassUtils.Interfaces.INCLUDE).spliterator(), false)
            .map(Class::getDeclaredFields)
            .flatMap(Arrays::stream)
            .sorted((f1, f2) -> f1.getName().compareTo(f2.getName()))
            .forEach(f -> {
                if (f.getAnnotationsByType(ManualTranslation.class).length != 0)
                    return;
                Serialise serialise = f.getAnnotation(Serialise.class);
                boolean nbt = true;
                boolean net = true;
                if (serialise != null)
                {
                    nbt = serialise.nbt();
                    net = serialise.net();
                }
                else if(onlyAnnotated)
                    return;

                fields.put(type.getName(), new FieldData(f, nbt, net));
                // Cache the translator for each field, prevents disparities between different points in time
                f.setAccessible(true);
                fieldTranslators.put(type.getName(), f.getName(), findTranslator(f.getType()));
            });
    }

    public void serialise(Object object, NBTTagCompound compound)
    {
        String className = object.getClass().getName();
        for (FieldData f : this.fields.get(className))
        {
            Translator<?, ?> translator = fieldTranslators.get(className, f.name);
            // Fields might be private
            f.field.setAccessible(true);
            try
            {
                Optional<? extends NBTBase> value = translator.serialise(f.field.get(object));
                value.ifPresent(v -> compound.setTag(f.name, v));
            }
            catch (IllegalAccessException e)
            {
                // Should never happen
                e.printStackTrace();
            }
        }
    }

    public void deserialise(NBTTagCompound compound, Object object)
    {
        String className = object.getClass().getName();
        for (FieldData f : this.fields.get(className))
        {
            Translator<?, ?> translator = fieldTranslators.get(className, f.name);
            // Fields might be private
            f.field.setAccessible(true);
            try
            {
                f.field.set(object, translator.deserialise(compound.hasKey(f.name) ? Optional.ofNullable(compound.getTag(f.name))
                                                                                  : Optional.empty()));
            }
            catch (IllegalAccessException e)
            {
                // Should never happen
                e.printStackTrace();
            }
        }
    }

    /**
     * Serialises each field of a message to a byte buffer, utilising translators that fit each field's type best.
     *
     * @param message the message to serialise
     * @param buffer  the buffer to serialise the message into
     */
    public void serialiseFrom(Message message, ByteBuf buffer)
    {
        String className = message.getClass().getName();
        for (FieldData f : this.fields.get(className))
        {
            Translator<?, ?> translator = fieldTranslators.get(className, f.name);
            // Fields might be private
            f.field.setAccessible(true);
            try
            {
                translator.serialise(f.field.get(message), buffer);
            }
            catch (IllegalAccessException e)
            {
                // Should never happen
                e.printStackTrace();
            }
        }
    }

    /**
     * Deserialises the contents of a byte buffer into a message, writing each field utilising translators.
     *
     * @param buffer  the buffer to deserialise from
     * @param message the message to deserialise into
     */
    public void deserialiseTo(ByteBuf buffer, Message message)
    {
        String className = message.getClass().getName();
        for (FieldData f : this.fields.get(className))
        {
            Translator<?, ?> translator = fieldTranslators.get(className, f.name);
            // Fields might be private
            f.field.setAccessible(true);
            try
            {
                f.field.set(message, translator.deserialise(buffer));
            }
            catch (IllegalAccessException e)
            {
                // Should never happen
                e.printStackTrace();
            }
        }
    }

    private static class FieldData
    {
        public final Field field;
        public final String name;
        public final boolean nbt;
        public final boolean net;

        private FieldData(Field field, boolean nbt, boolean net)
        {
            this.field = field;
            this.name = field.getName();
            this.nbt = nbt;
            this.net = net;
        }
    }
}
