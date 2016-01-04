package de.mineformers.investiture.client;

import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;

/**
 * Container class for all key bindings used in any of the Investiture modules
 */
public class KeyBindings
{
    /**
     * Key binding for the metal selection menu
     */
    public static final KeyBinding SHOW_DIAL =
        new KeyBinding("key.showDial", Keyboard.KEY_F, "key.categories.allomancy");
}
