package de.mineformers.investiture.allomancy;

import de.mineformers.investiture.Investiture;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.RangeDouble;

@Config(modid = Investiture.MOD_ID, name = Allomancy.DOMAIN)
public class AllomancyConfig
{
    @Comment("Manipulate individual misting types here")
    public static Mistings mistings = new Mistings();

    public static class Mistings
    {
        public Thug thug = new Thug();

        public static class Thug
        {
            @Comment({
                "The thug's attack boost adds the given amount of damage to all his attacks.",
                "The default value of 6 resembles an iron sword."
            })
            @RangeDouble(min = 0)
            public double attackBoost = 6;
            @Comment({
                "A thug's damage resistance absorbs the given amount of damage whenever the Allomancer is hurt.",
                "The default of 2.5 will reduce the maximum damage made by zombies to 0.5"
            })
            @RangeDouble(min = 0)
            public double damageResistance = 2.5;
        }

        public Tineye tineye = new Tineye();

        public static class Tineye
        {
            @Comment("Specifies whether burning tin should increase the field of view.")
            public boolean fovEnabled = true;
            @Comment({
                "Specifies the amount the Tineye's field of view will be increased when burning tin.",
                "The default value of 40 will effectively change the default \"Normal\" setting to the maximum \"Quake Pro\"."
            })
            public float fovIncrease = 40;
            @Comment({
                "Specifies the desired field of view for the Tineye's zoom ability.",
                "Note that this is an absolute value, not a modifier to the existing value.",
                "The default value of 10 is equal to the minimal FOV setting possible (resulting in an appropriate zoom effect)."
            })
            public float fovZoom = 10;
        }
    }
}
