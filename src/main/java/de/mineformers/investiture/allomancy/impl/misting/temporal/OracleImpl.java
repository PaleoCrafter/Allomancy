package de.mineformers.investiture.allomancy.impl.misting.temporal;

import de.mineformers.investiture.allomancy.api.misting.Inject;
import de.mineformers.investiture.allomancy.api.misting.temporal.Oracle;
import de.mineformers.investiture.allomancy.client.particle.FootStep;
import de.mineformers.investiture.allomancy.impl.misting.AbstractMisting;
import de.mineformers.investiture.serialisation.Serialise;
import de.mineformers.investiture.util.PathFinding;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * ${JDOC}
 */
public class OracleImpl extends AbstractMisting implements Oracle, ITickable
{
    @Inject
    private Entity entity;
    @Serialise
    private int spawnDimension;
    @Serialise
    private BlockPos spawnPoint;
    private int timer;
    private Queue<BlockPos> path = new ArrayDeque<>();

    @Override
    public void startBurning()
    {
        spawnPoint = null;
        if (entity instanceof EntityPlayer)
            spawnPoint = ((EntityPlayer) entity).getBedLocation();
        if (spawnPoint == null)
            spawnPoint = entity.world.getSpawnPoint();
        spawnDimension = entity.dimension;
        timer = 0;
        path.clear();
        path.addAll(PathFinding.bresenham(entity, spawnPoint));
    }

    @Override
    public void update()
    {
        if (!entity.world.isRemote || path.isEmpty() || entity.dimension != spawnDimension)
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
            .addEffect(new FootStep(entity.world, new Vec3d(step).addVector(0.3, 0.0001, 0.3), 0.28627452f, 0.7254902f, 0.87058824f));
        Minecraft.getMinecraft().effectRenderer
            .addEffect(new FootStep(entity.world, new Vec3d(step).addVector(0.6, 0.0001, 0.6), 0.28627452f, 0.7254902f, 0.87058824f));
    }

    @Override
    public BlockPos spawnPoint()
    {
        return spawnPoint;
    }
}
