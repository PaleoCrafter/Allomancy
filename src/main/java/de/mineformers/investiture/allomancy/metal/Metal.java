package de.mineformers.investiture.allomancy.metal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * Interface representing the properties of any allomantic metal.
 * This is to be used like Vanilla's Blocks and Items, not storing any data itself.
 */
public interface Metal
{
    /**
     * @return the metal's internal ID
     */
    String id();

    /**
     * Determines whether the stack can be burned or is impure.
     *
     * @return true if the stack is burnable, false if it is not pure enough
     */
    default boolean canBurn()
    {
        return true;
    }

    default boolean matches(@Nonnull ItemStack stack)
    {
        if (stack.getItem() instanceof MetalHolder) {
            return this.equals(((MetalHolder) stack.getItem()).getMetal(stack));
        }

        return false;
    }

    /**
     * Apply any effects caused by the consumption of impure metals to an entity.
     *
     * @param entity the affected entity
     */
    default void applyImpurityEffects(Entity entity)
    {
        if (entity instanceof EntityLivingBase) {
            ((EntityLivingBase) entity).addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 30, 3));
        }
    }

    /**
     * @return the metal's unlocalised name
     */
    default String unlocalisedName()
    {
        return "allomancy.metals." + id() + ".name";
    }

    /**
     * Basic abstract implementation of metals with an ID which also functions as equality measure.
     */
    abstract class AbstractMetal implements Metal
    {
        private final String _id;

        AbstractMetal(@Nonnull String id)
        {
            this._id = id;
        }

        @Override
        public String id()
        {
            return _id;
        }

        @Override
        public int hashCode()
        {
            return _id.hashCode();
        }

        @Override
        public boolean equals(Object obj)
        {
            if (this == obj) return true;
            if (obj == null) return false;
            if (getClass() != obj.getClass()) return false;
            return Objects.equals(id(), ((Metal) obj).id());
        }
    }
}
