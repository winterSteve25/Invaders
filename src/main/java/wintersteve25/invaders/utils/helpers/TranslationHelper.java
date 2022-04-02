package wintersteve25.invaders.utils.helpers;

import net.minecraft.util.text.TranslationTextComponent;

public class TranslationHelper {
    public static TranslationTextComponent getCommandErrorMessage(String message) {
        return new TranslationTextComponent("invaders.commands.error." + message);
    }
}
