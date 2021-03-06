package de.mineformers.investiture.allomancy.impl;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.mineformers.investiture.allomancy.api.Allomancer;
import de.mineformers.investiture.allomancy.api.AllomancyAPI;
import de.mineformers.investiture.allomancy.api.Capabilities;
import de.mineformers.investiture.allomancy.api.MistingFactory;
import de.mineformers.investiture.allomancy.api.metal.Metal;
import de.mineformers.investiture.allomancy.api.metal.MetalMapping;
import de.mineformers.investiture.allomancy.api.metal.Metals;
import de.mineformers.investiture.allomancy.api.metal.stack.MetalStack;
import de.mineformers.investiture.allomancy.api.misting.Inject;
import de.mineformers.investiture.allomancy.api.misting.Misting;
import de.mineformers.investiture.allomancy.api.misting.mental.Rioter;
import de.mineformers.investiture.allomancy.api.misting.mental.Soother;
import de.mineformers.investiture.allomancy.api.misting.physical.Coinshot;
import de.mineformers.investiture.allomancy.api.misting.physical.Lurcher;
import de.mineformers.investiture.allomancy.api.misting.physical.Thug;
import de.mineformers.investiture.allomancy.api.misting.physical.Tineye;
import de.mineformers.investiture.allomancy.api.misting.temporal.Augur;
import de.mineformers.investiture.allomancy.api.misting.temporal.Oracle;
import de.mineformers.investiture.allomancy.api.misting.temporal.Pulser;
import de.mineformers.investiture.allomancy.api.misting.temporal.Slider;
import de.mineformers.investiture.allomancy.impl.misting.mental.RioterImpl;
import de.mineformers.investiture.allomancy.impl.misting.mental.SootherImpl;
import de.mineformers.investiture.allomancy.impl.misting.physical.CoinshotImpl;
import de.mineformers.investiture.allomancy.impl.misting.physical.LurcherImpl;
import de.mineformers.investiture.allomancy.impl.misting.physical.ThugImpl;
import de.mineformers.investiture.allomancy.impl.misting.physical.TineyeImpl;
import de.mineformers.investiture.allomancy.impl.misting.temporal.*;
import de.mineformers.investiture.allomancy.item.MetalItem;
import de.mineformers.investiture.serialisation.Serialisation;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockWorldState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 *
 */
@ParametersAreNonnullByDefault
public class AllomancyAPIImpl implements AllomancyAPI
{
    public static final AllomancyAPIImpl INSTANCE = new AllomancyAPIImpl();
    private static final List<Class<?>> INJECTABLE_TYPES = ImmutableList.of(Allomancer.class, Entity.class);

    public static Optional<Allomancer> getAllomancer(Entity player)
    {
        return INSTANCE.toAllomancer(player);
    }

    Map<Class<? extends Misting>, MistingData> factories = new HashMap<>();
    private Map<Class<?>, BiPredicate<?, ?>> equalities = new HashMap<>();
    private Set<Predicate<ItemStack>> metallicItems = new HashSet<>();
    private Set<Predicate<BlockWorldState>> metallicBlocks = new HashSet<>();
    private Set<Predicate<Entity>> metallicEntities = new HashSet<>();
    private Set<MetalMapping> mappings = new HashSet<>();

    private AllomancyAPIImpl()
    {
    }

    public void init()
    {
        registerEquality(ItemStack.class, ItemStack::areItemStacksEqual);

        registerMisting(Coinshot.class, CoinshotImpl::new);
        registerMisting(Lurcher.class, LurcherImpl::new);
        registerMisting(Tineye.class, TineyeImpl::new);
        registerMisting(Thug.class, ThugImpl::new);

        registerMisting(Soother.class, SootherImpl::new);
        registerMisting(Rioter.class, RioterImpl::new);

        registerMisting(Augur.class, AugurImpl::new);
        registerMisting(Oracle.class, OracleImpl::new);
        registerMisting(Pulser.class, PulserImpl::new);
        registerMisting(Slider.class, SliderImpl::new);

        registerOreDictMapping(Metals.IRON, MetalItem.Type.INGOT, false);
        registerOreDictMapping(Metals.GOLD, MetalItem.Type.INGOT, false);
        registerOreDictMapping(Metals.IRON, MetalItem.Type.NUGGET, false);
        registerOreDictMapping(Metals.GOLD, MetalItem.Type.NUGGET, false);

        registerMetallicItem(stack -> !Metals.getMetalStacks(stack).isEmpty());
        registerMetallicItem(stack ->
                             {
                                 Item item = stack.getItem();
                                 Block block = Block.getBlockFromItem(item);
                                 return isMetallic(block.getDefaultState());
                             });

        registerMetallicBlock(s -> s.getBlockState().getMaterial() == Material.IRON || s.getBlockState().getMaterial() == Material.ANVIL);

        registerMetallicEntity(e -> e instanceof EntityItem && isMetallic(((EntityItem) e).getItem()));
    }

    private void registerOreDictMapping(Metal metal, MetalItem.Type type, boolean nbt)
    {
        String oreName = String.format("%s%s", type.name().toLowerCase(), StringUtils.capitalize(metal.id()));
        registerMetalMapping(
            new MetalMapping.OreDict(oreName, metal,
                                     type.conversion, type.purityRange.hasLowerBound() ? type.purityRange.lowerEndpoint() : 0, type.purityRange,
                                     nbt));
    }

    public Optional<Class<? extends Misting>> getMistingType(String identifier)
    {
        return factories.keySet().stream().filter(c -> c.getSimpleName().equalsIgnoreCase(identifier)).findFirst();
    }

    public Collection<String> getMistingNames()
    {
        List<String> result = factories.keySet().stream().map(c -> c.getSimpleName().toLowerCase()).collect(Collectors.toList());
        result.sort(String::compareToIgnoreCase);
        return result;
    }

    @Nonnull
    public Optional<Allomancer> toAllomancer(Entity entity)
    {
        return Optional.ofNullable(entity.getCapability(Capabilities.ALLOMANCER, null));
    }

    @Override
    public <T extends Misting> void registerMisting(Class<T> type, MistingFactory<? extends T> factory)
    {
        factories.put(type, new MistingData(type, factory));
    }

    @Override
    public <T> void registerEquality(Class<T> type, BiPredicate<T, T> predicate)
    {
        equalities.put(type, predicate);
    }

    @Override
    public void registerMetallicItem(Predicate<ItemStack> predicate)
    {
        metallicItems.add(predicate);
    }

    @Override
    public void registerMetallicBlock(Predicate<BlockWorldState> predicate)
    {
        metallicBlocks.add(predicate);
    }

    @Override
    public void registerMetallicEntity(Predicate<Entity> predicate)
    {
        metallicEntities.add(predicate);
    }

    @Nonnull
    @Override
    public Collection<Predicate<ItemStack>> metallicItems()
    {
        return Collections.unmodifiableSet(metallicItems);
    }

    @Nonnull
    @Override
    public Collection<Predicate<BlockWorldState>> metallicBlocks()
    {
        return Collections.unmodifiableSet(metallicBlocks);
    }

    @Nonnull
    @Override
    public Collection<Predicate<Entity>> metallicEntities()
    {
        return Collections.unmodifiableSet(metallicEntities);
    }

    @Override
    public Iterable<SpeedBubble> speedBubbles(World world)
    {
        return SpeedBubbles.from(world);
    }

    @Override
    public Optional<MetalMapping> getMapping(ItemStack stack)
    {
        return mappings.stream().filter(m -> m.matches(stack)).findFirst();
    }

    @Override
    public void registerMetalMapping(MetalMapping mapping)
    {
        mappings.add(mapping);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    <T extends Misting> T instantiate(Class<T> type, Allomancer allomancer, Entity entity)
    {
        MistingData data = factories.get(type);
        if (data == null)
            return null;
        T result = (T) data.factory.create();
        data.inject(result, ImmutableMap.of(Allomancer.class, allomancer,
                                            Entity.class, entity));
        return result;
    }

    public void update(Allomancer allomancer, Entity entity)
    {
        for (Class<? extends Misting> type : allomancer.powers())
        {
            allomancer.as(type).ifPresent(m ->
                                          {
                                              if (allomancer.activePowers().contains(type))
                                              {
                                                  if (entity.world.isRemote ||
                                                      (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()))
                                                  {
                                                      if (m instanceof ITickable)
                                                          ((ITickable) m).update();
                                                  }
                                                  else
                                                  {
                                                      Metal metal = m.baseMetal();
                                                      Set<MetalStack> burned = allomancer.storage().burn(metal, metal.burnRate(), false);
                                                      float available = burned.stream()
                                                                              .reduce(0f, (acc, s) -> acc + s.getQuantity(), (a, b) -> a + b);
                                                      float impurities =
                                                          burned
                                                              .stream()
                                                              .reduce(0f, (acc, s) -> acc + s.getQuantity() * Math.max(0, 0.9f - s.getPurity()),
                                                                      (a, b) -> a + b);
                                                      if (available >= metal.burnRate())
                                                      {
                                                          if (m instanceof ITickable)
                                                              ((ITickable) m).update();
                                                      }
                                                      else
                                                      {
                                                          allomancer.deactivate(type);
                                                      }
                                                      if (impurities > 0)
                                                      {
                                                          metal.applyImpurityEffects(entity, impurities);
                                                      }
                                                  }
                                              }
                                              if (!entity.world.isRemote)
                                                  factories.get(type).companion.write(m, entity);
                                          });
        }
    }

    public void read(Entity entity, Class<? extends Misting> type, byte[] data)
    {
        toAllomancer(entity).map(a -> a.grantPower(type)).ifPresent(m -> factories.get(type).companion.read(m, data));
    }

    @SuppressWarnings("unchecked")
    public <T> boolean equals(T a, T b)
    {
        return (a == b) || ((BiPredicate<T, T>) Optional.ofNullable(equalities.get(a.getClass())).orElse(Objects::equals)).test(a, b);
    }

    static class MistingData
    {
        public final Class<? extends Misting> type;
        private final MistingFactory<?> factory;
        final Collection<Field> injectedFields;
        final AllomancerCompanion companion;

        MistingData(Class<? extends Misting> baseType, MistingFactory<?> factory)
        {
            this.factory = factory;
            type = factory.referenceClass();
            injectedFields = StreamSupport
                .stream(ClassUtils.hierarchy(type, ClassUtils.Interfaces.INCLUDE).spliterator(), false)
                .map(Class::getDeclaredFields)
                .flatMap(Arrays::stream)
                .filter(f -> f.getAnnotation(Inject.class) != null &&
                    INJECTABLE_TYPES.stream().anyMatch(t -> t.isAssignableFrom(f.getType())))
                .collect(Collectors.toList());
            injectedFields.forEach(f -> f.setAccessible(true));
            this.companion = new AllomancerCompanion(type, baseType);
            Serialisation.INSTANCE.registerClass(type, true);
        }

        void inject(Misting instance, Map<Class<?>, Object> values)
        {
            for (Field f : injectedFields)
            {
                values.keySet()
                      .stream()
                      .filter(e -> e.isAssignableFrom(f.getType()))
                      .findFirst()
                      .ifPresent(t ->
                                 {
                                     try
                                     {
                                         f.set(instance, values.get(t));
                                     }
                                     catch (IllegalAccessException e)
                                     {
                                         Throwables.propagate(e);
                                     }
                                 });
            }
        }
    }
}
