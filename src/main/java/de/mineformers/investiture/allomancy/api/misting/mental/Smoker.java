package de.mineformers.investiture.allomancy.api.misting.mental;

import de.mineformers.investiture.allomancy.api.metal.Metal;
import de.mineformers.investiture.allomancy.api.metal.Metals;
import de.mineformers.investiture.allomancy.api.misting.Misting;
import de.mineformers.investiture.allomancy.api.power.AllomanticPower;

import static de.mineformers.investiture.allomancy.api.power.Category.MENTAL;
import static de.mineformers.investiture.allomancy.api.power.Effect.PULL;
import static de.mineformers.investiture.allomancy.api.power.Scope.INTERNAL;

/**
 * ${JDOC}
 */
@AllomanticPower(category = MENTAL, scope = INTERNAL, effect = PULL)
public interface Smoker extends Misting
{
    @Override
    default Metal baseMetal()
    {
        return Metals.COPPER;
    }
}
