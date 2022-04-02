package wintersteve25.invaders.datagen.client;

import net.minecraft.data.DataGenerator;
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
        add(TranslationHelper.getCommandErrorMessage(InvadersConstants.LangKeys.SPAWN_COMMAND).getKey(), "");
    }
}