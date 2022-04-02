package wintersteve25.invaders.datagen.client;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.data.LanguageProvider;
import wintersteve25.invaders.Invaders;
import wintersteve25.invaders.utils.InvadersConstants;
import wintersteve25.invaders.utils.helpers.TranslationHelper;


public class InvadersEngLangProvider extends LanguageProvider {
    public InvadersEngLangProvider(DataGenerator gen) {
        super(gen, Invaders.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(TranslationHelper.getCommandErrorMessage(InvadersConstants.LangKeys.SPAWN_COMMAND).getKey(), TextFormatting.RED + "If you see this message please open a bug report!");
        add(TranslationHelper.getCommandErrorMessage(InvadersConstants.LangKeys.SPAWN_COMMAND_EXCEED_MAX).getKey(), TextFormatting.RED + "Max Re-Roll Count Reached!");
    }
}