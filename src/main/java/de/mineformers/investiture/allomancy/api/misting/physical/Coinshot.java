package de.mineformers.investiture.allomancy.api.misting.physical;

import de.mineformers.investiture.allomancy.api.metal.Metal;
import de.mineformers.investiture.allomancy.api.metal.Metals;
import de.mineformers.investiture.allomancy.api.power.AllomanticPower;

import static de.mineformers.investiture.allomancy.api.power.Category.PHYSICAL;
import static de.mineformers.investiture.allomancy.api.power.Effect.PUSH;
import static de.mineformers.investiture.allomancy.api.power.Scope.EXTERNAL;

/**
 * ${JDOC}
 */
@AllomanticPower(category = PHYSICAL, scope = EXTERNAL, effect = PUSH)
public interface Coinshot extends MetalManipulator
{
    @Override
    default Metal baseMetal()
    {
        return Metals.STEEL;
    }
}
