package de.mineformers.investiture.allomancy.impl.misting.temporal;

import de.mineformers.investiture.allomancy.api.misting.Inject;
import de.mineformers.investiture.allomancy.api.misting.temporal.Augur;
import de.mineformers.investiture.allomancy.client.particle.FootStep;
import de.mineformers.investiture.allomancy.impl.misting.AbstractMisting;
import de.mineformers.investiture.serialisation.Serialise;
import de.mineformers.investiture.util.PathFinding;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

import static de.mineformers.investiture.allomancy.impl.AllomancyAPIImpl.getAllomancer;

/**
 * ${JDOC}
 */
public class AugurImpl extends AbstractMisting implements Augur, ITickable
{
    @Inject
    private Entity entity;
    @Serialise
    private int deathDimension;
    @Serialise
    private Vec3d position;
    private boolean validPath = false;
    private int timer;
    private Queue<BlockPos> path = new ArrayDeque<>();

    @Override
    public void startBurning()
    {
        validPath = false;
        timer = 0;
        path.clear();
        if (position == null)
            return;
        if (entity.dimension == deathDimension)
        {
            path.addAll(PathFinding.bresenham(entity, new BlockPos(position)));
            validPath = true;
        }
    }

    @Override
    public void update()
    {
        if (!entity.world.isRemote || position == null)
            return;
        if (!validPath && entity.dimension == deathDimension)
        {
            path.clear();
            path.addAll(PathFinding.bresenham(entity, new BlockPos(position)));
            timer = 0;
            validPath = true;
        }
        if (!validPath || path.isEmpty())
            return;
        timer++;
        if (timer > 5)
            timer = 0;
        else
            return;
        BlockPos step = path.poll();
        spawnParticles(step);
    }

    @SideOnly(Side.CLIENT)
    private void spawnParticles(BlockPos step)
    {
        Minecraft.getMinecraft().effectRenderer
            .addEffect(new FootStep(entity.world, new Vec3d(step).addVector(0.3, 0.0001, 0.3), 1f, 0, 0));
        Minecraft.getMinecraft().effectRenderer
            .addEffect(new FootStep(entity.world, new Vec3d(step).addVector(0.6, 0.0001, 0.6), 1f, 0, 0));
    }

    @Override
    public Optional<Vec3d> lastDeathPosition()
    {
        return Optional.ofNullable(position);
    }

    public static class EventHandler
    {
        @SubscribeEvent
        public void onDeath(LivingDeathEvent event)
        {
            Entity entity = event.getEntity();
            if (entity.world.isRemote)
                return;
            getAllomancer(entity).flatMap(a -> a.as(Augur.class))
                                 .ifPresent(a ->
                                            {
                                                if (a instanceof AugurImpl)
                                                {
                                                    ((AugurImpl) a).deathDimension = entity.dimension;
                                                    ((AugurImpl) a).position = entity.getPositionVector();
                                                }
                                            });
        }
    }
}
