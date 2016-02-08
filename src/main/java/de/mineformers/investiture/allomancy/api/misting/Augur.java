package de.mineformers.investiture.allomancy.api.misting;

import de.mineformers.investiture.allomancy.api.metal.Metal;
import de.mineformers.investiture.allomancy.api.metal.Metals;
import de.mineformers.investiture.allomancy.api.power.AllomanticPower;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

import java.util.Optional;

import static de.mineformers.investiture.allomancy.api.power.Category.TEMPORAL;
import static de.mineformers.investiture.allomancy.api.power.Effect.PULL;
import static de.mineformers.investiture.allomancy.api.power.Scope.INTERNAL;

/**
 * ${JDOC}
 */
@AllomanticPower(category = TEMPORAL, scope = INTERNAL, effect = PULL)
public interface Augur extends Misting
{
    @Override
    default Metal baseMetal()
    {
        return Metals.GOLD;
    }

    Optional<Vec3> lastDeathPosition();
}
