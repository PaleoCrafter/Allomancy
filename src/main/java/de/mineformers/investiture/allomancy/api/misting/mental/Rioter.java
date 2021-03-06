package de.mineformers.investiture.allomancy.api.misting.mental;

import de.mineformers.investiture.allomancy.api.metal.Metal;
import de.mineformers.investiture.allomancy.api.metal.Metals;
import de.mineformers.investiture.allomancy.api.power.AllomanticPower;

import static de.mineformers.investiture.allomancy.api.power.Category.MENTAL;
import static de.mineformers.investiture.allomancy.api.power.Effect.PUSH;
import static de.mineformers.investiture.allomancy.api.power.Scope.EXTERNAL;

/**
 * ${JDOC}
 */
@AllomanticPower(category = MENTAL, scope = EXTERNAL, effect = PUSH)
public interface Rioter extends EmotionManipulator
{
    @Override
    default Metal baseMetal()
    {
        return Metals.ZINC;
    }
}
