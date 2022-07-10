package wintersteve25.invaders.utils.helpers;

import fictioncraft.wintersteve25.fclib.common.helper.MiscHelper;
import net.minecraft.util.text.TranslationTextComponent;

public class TranslationHelper {
    public static TranslationTextComponent getCommandErrorMessage(String message, Object... args) {
        return new TranslationTextComponent("invaders.commands.error." + MiscHelper.langToReg(message), args);
    }

    public static TranslationTextComponent getCommandSuccessMessage(String message, Object... args) {
        return new TranslationTextComponent("invaders.commands.success." + MiscHelper.langToReg(message), args);
    }

    public static TranslationTextComponent guiTitle(String message, Object... args) {
        return new TranslationTextComponent("invaders.gui.titles." + MiscHelper.langToReg(message), args);
    }

    public static TranslationTextComponent gui(String message, Object... args) {
        return new TranslationTextComponent("invaders.gui." + MiscHelper.langToReg(message), args);
    }

    public static TranslationTextComponent itemTooltip(String message, Object... args) {
        return new TranslationTextComponent("invaders.tooltips.items." + MiscHelper.langToReg(message), args);
    }

    public static TranslationTextComponent message(String message, Object... args) {
        return new TranslationTextComponent("invaders.messages." + MiscHelper.langToReg(message), args);
    }
    
    public static TranslationTextComponent bossBar(String name, Object... args) {
        return new TranslationTextComponent("invaders.bossbars." + MiscHelper.langToReg(name), args);
    }
    
    public static TranslationTextComponent titles(String name, Object... args) {
        return new TranslationTextComponent("invaders.title." + MiscHelper.langToReg(name), args);
    }
}
