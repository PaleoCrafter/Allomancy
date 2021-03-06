package de.mineformers.investiture.allomancy.impl.misting.temporal;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Objects;
import java.util.UUID;

/**
 * ${JDOC}
 */
@ParametersAreNonnullByDefault
public class SpeedBubble
{
    public final UUID owner;
    public final int dimension;
    public final BlockPos position;
    public final double radius;
    public final AxisAlignedBB bounds;

    public SpeedBubble(UUID owner, int dimension, BlockPos position, double radius)
    {
        this.owner = owner;
        this.dimension = dimension;
        this.position = position;
        this.radius = radius;
        BlockPos boundsMin = position.add(-radius, -radius, -radius);
        BlockPos boundsMax = position.add(radius, radius, radius);
        this.bounds = new AxisAlignedBB(boundsMin, boundsMax);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(dimension, position, radius);
    }

    @Override
    public boolean equals(@Nullable Object obj)
    {
        if (obj == null)
            return false;
        if (!obj.getClass().equals(this.getClass()))
            return false;
        SpeedBubble bubble = (SpeedBubble) obj;
        return bubble.dimension == dimension && bubble.position.equals(position) && bubble.radius == radius;
    }
}
