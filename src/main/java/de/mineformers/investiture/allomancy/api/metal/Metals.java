package de.mineformers.investiture.allomancy.api.metal;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import de.mineformers.investiture.allomancy.api.Capabilities;
import de.mineformers.investiture.allomancy.api.metal.stack.MetalStack;
import de.mineformers.investiture.allomancy.api.misting.Misting;
import de.mineformers.investiture.allomancy.api.misting.enhancement.AluminiumGnat;
import de.mineformers.investiture.allomancy.api.misting.enhancement.DuraluminGnat;
import de.mineformers.investiture.allomancy.api.misting.enhancement.Leecher;
import de.mineformers.investiture.allomancy.api.misting.enhancement.Nicroburst;
import de.mineformers.investiture.allomancy.api.misting.mental.Rioter;
import de.mineformers.investiture.allomancy.api.misting.mental.Seeker;
import de.mineformers.investiture.allomancy.api.misting.mental.Smoker;
import de.mineformers.investiture.allomancy.api.misting.mental.Soother;
import de.mineformers.investiture.allomancy.api.misting.physical.Coinshot;
import de.mineformers.investiture.allomancy.api.misting.physical.Lurcher;
import de.mineformers.investiture.allomancy.api.misting.physical.Thug;
import de.mineformers.investiture.allomancy.api.misting.physical.Tineye;
import de.mineformers.investiture.allomancy.api.misting.temporal.Augur;
import de.mineformers.investiture.allomancy.api.misting.temporal.Oracle;
import de.mineformers.investiture.allomancy.api.misting.temporal.Pulser;
import de.mineformers.investiture.allomancy.api.misting.temporal.Slider;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * Provides access to all allomantic metals, especially the basic 16 ones provided by the Allomancy module itself.
 */
public final class Metals
{
    private static final Set<Metal> METALS = new HashSet<>();
    private static final Set<MetalAlloy> ALLOYS = new HashSet<>();
    // Base metals
    public static final Metal COPPER = new ItemMetalBurnable("copper", Smoker.class, 1);
    public static final Metal ZINC = new ItemMetalBurnable("zinc", Rioter.class, 1);
    public static final Metal TIN = new ItemMetalBurnable("tin", Tineye.class, 1);
    public static final Metal IRON = new ItemMetalBurnable("iron", Lurcher.class, 1);
    public static final Metal ALUMINIUM = new ItemMetalBurnable("aluminium", AluminiumGnat.class, 1);
    public static final Metal CHROMIUM = new ItemMetalBurnable("chromium", Leecher.class, 1);
    public static final Metal GOLD = new ItemMetalBurnable("gold", Augur.class, 1);
    public static final Metal CADMIUM = new ItemMetalBurnable("cadmium", Pulser.class, 1);
    public static final Metal LEAD = new ItemMetalNonBurnable("lead");
    public static final Metal BISMUTH = new ItemMetalNonBurnable("bismuth");
    public static final Metal SILVER = new ItemMetalNonBurnable("silver");
    public static final Metal NICKEL = new ItemMetalNonBurnable("nickel");
    public static final Metal CARBON = new ItemMetalNonBurnable("carbon");
    // MetalAlloy metals
    public static final MetalAlloy BRONZE = new ItemAlloyBurnable("bronze", Seeker.class, 1,
                                                                  COPPER, 0.75F,
                                                                  TIN, 0.25F);
    public static final MetalAlloy BRASS = new ItemAlloyBurnable("brass", Soother.class, 1,
                                                                 COPPER, 0.65F,
                                                                 ZINC, 0.35F);
    public static final MetalAlloy PEWTER = new ItemAlloyBurnable("pewter", Thug.class, 1,
                                                                  TIN, 0.91F,
                                                                  LEAD, 0.09F);
    public static final MetalAlloy STEEL = new ItemAlloyBurnable("steel", Coinshot.class, 1,
                                                                 IRON, 0.98F,
                                                                 CARBON, 0.02F);
    public static final MetalAlloy DURALUMIN = new ItemAlloyBurnable("duralumin", DuraluminGnat.class, 1,
                                                                     ALUMINIUM, 0.96F,
                                                                     COPPER, 0.04F);
    public static final MetalAlloy NICROSIL = new ItemAlloyBurnable("nicrosil", Nicroburst.class, 1,
                                                                    NICKEL, 0.86F,
                                                                    CHROMIUM, 0.14F);
    public static final MetalAlloy ELECTRUM = new ItemAlloyBurnable("electrum", Oracle.class, 1,
                                                                    GOLD, 0.45F,
                                                                    SILVER, 0.55F);
    public static final MetalAlloy BENDALLOY = new ItemAlloyBurnable("bendalloy", Slider.class, 1,
                                                                     BISMUTH, 0.5F,
                                                                     LEAD, 0.27F,
                                                                     TIN, 0.13F,
                                                                     CADMIUM, 0.1F);

    public static final Set<Metal> BASE_METALS = ImmutableSet.of(COPPER, ZINC, TIN, IRON,
                                                                 ALUMINIUM, CHROMIUM, GOLD, CADMIUM,
                                                                 BRONZE, BRASS, PEWTER, STEEL,
                                                                 DURALUMIN, NICROSIL, ELECTRUM, BENDALLOY);

    /**
     * Register all 16 basic metals
     */
    public static void init()
    {
        METALS.add(BRONZE);
        METALS.add(BRASS);
        METALS.add(COPPER);
        METALS.add(ZINC);
        METALS.add(TIN);
        METALS.add(IRON);
        METALS.add(PEWTER);
        METALS.add(STEEL);
        METALS.add(DURALUMIN);
        METALS.add(NICROSIL);
        METALS.add(ALUMINIUM);
        METALS.add(CHROMIUM);
        METALS.add(GOLD);
        METALS.add(CADMIUM);
        METALS.add(ELECTRUM);
        METALS.add(BENDALLOY);
        METALS.add(LEAD);
        METALS.add(BISMUTH);
        METALS.add(SILVER);
        METALS.add(NICKEL);

        ALLOYS.add(BRONZE);
        ALLOYS.add(BRASS);
        ALLOYS.add(PEWTER);
        ALLOYS.add(STEEL);
        ALLOYS.add(DURALUMIN);
        ALLOYS.add(NICROSIL);
        ALLOYS.add(ELECTRUM);
        ALLOYS.add(BENDALLOY);
    }

    /**
     * @param id the ID of the searched metal
     * @return the requested metal
     * @throws IllegalArgumentException if there is no metal for the given id
     */
    public static Metal get(String id)
    {
        Optional<Metal> result = METALS.stream().filter(m -> m.id().equals(id)).findFirst();
        if (!result.isPresent())
        {
            throw new IllegalArgumentException("Requested metal '" + id + "' does not exist!");
        }
        return result.get();
    }

    /**
     * @return an unmodifiable view of all allomantic metals
     */
    public static Set<Metal> metals()
    {
        return Collections.unmodifiableSet(METALS);
    }

    public static Set<MetalAlloy> alloys()
    {
        return Collections.unmodifiableSet(ALLOYS);
    }

    @SuppressWarnings("unchecked")
    public static List<MetalStack> getMetalStacks(@Nonnull ItemStack stack)
    {
        if (stack.hasCapability(Capabilities.METAL_STACK_PROVIDER, null))
        {
            return stack.getCapability(Capabilities.METAL_STACK_PROVIDER, null).get();
        }
        return ImmutableList.of();
    }

    /**
     * Representation of burnable metal
     */
    private final static class ItemMetalBurnable extends Metal.AbstractMetal
    {
        private final float burnRate;

        ItemMetalBurnable(@Nonnull String id, Class<? extends Misting> mistingType, float burnRate)
        {
            super(id, mistingType);
            this.burnRate = burnRate;
        }

        @Override
        public float burnRate()
        {
            return burnRate;
        }
    }

    /**
     * Representation of non burnable metal
     */
    private final static class ItemMetalNonBurnable extends Metal.AbstractMetal
    {
        ItemMetalNonBurnable(@Nonnull String id)
        {
            super(id, null);
        }

        public boolean canBurn()
        {
            return false;
        }
    }

    /**
     * Representation of burnable alloy
     */
    private final static class ItemAlloyBurnable extends MetalAlloy.AbstractAlloy
    {
        private final float burnRate;

        ItemAlloyBurnable(@Nonnull String id, Class<? extends Misting> mistingType, float burnRate, Object... components)
        {
            super(id, mistingType, components);
            this.burnRate = burnRate;
        }

        @Override
        public float burnRate()
        {
            return burnRate;
        }
    }
}
