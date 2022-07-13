package wintersteve25.invaders.datagen.client;

import fictioncraft.wintersteve25.fclib.common.helper.MiscHelper;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.data.LanguageProvider;
import wintersteve25.invaders.Invaders;
import wintersteve25.invaders.contents.base.ONIIRegistryObject;
import wintersteve25.invaders.init.InvadersBlocks;
import wintersteve25.invaders.utils.InvadersConstants;
import wintersteve25.invaders.utils.helpers.TranslationHelper;


public class InvadersEngLangProvider extends LanguageProvider {
    public InvadersEngLangProvider(DataGenerator gen) {
        super(gen, Invaders.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        autoGenLang();

        add(InvadersConstants.LangKeys.HOLD_SHIFT.getKey(), TextFormatting.DARK_GRAY + "Hold shift for more information");
        add(InvadersConstants.LangKeys.PLACE_HUB.getKey(), TextFormatting.RED + "Hub can not be broken once placed, and you will not be able to re-roll spawn, shift place to confirm placement");
        add(InvadersConstants.LangKeys.DESTROY_HUB.getKey(), TextFormatting.RED + "Hub can not be broken!");
        add(InvadersConstants.LangKeys.FAILED_TO_CLAIM_CHUNK.getKey(), TextFormatting.RED + "Only chunks in range of the hub can be claimed!");

        add(TranslationHelper.getCommandErrorMessage(InvadersConstants.LangKeys.SPAWN_COMMAND).getKey(), TextFormatting.RED + "If you see this message please open a bug report!");
        add(TranslationHelper.getCommandErrorMessage(InvadersConstants.LangKeys.SPAWN_COMMAND_EXCEED_MAX).getKey(), TextFormatting.RED + "Max re-roll count reached!");
        add(TranslationHelper.getCommandErrorMessage(InvadersConstants.LangKeys.SPAWN_COMMAND_HUB_PLACED).getKey(), TextFormatting.RED + "Can not re-roll once hub is placed!");
        add(TranslationHelper.getCommandSuccessMessage(InvadersConstants.LangKeys.SPAWN_COMMAND_SUCCESS).getKey(), TextFormatting.GREEN + "Starting re-roll spawn, this may take a while.");

        add(TranslationHelper.getCommandSuccessMessage(InvadersConstants.LangKeys.DUMP_FOREST).getKey(), "Information printed in log");

        add(TranslationHelper.guiTitle(InvadersConstants.LangKeys.HUB).getKey(), "The Hub");
        add(TranslationHelper.gui(InvadersConstants.LangKeys.HUB_OWNER).getKey(), "Hub Owner: %s");
        add(TranslationHelper.gui(InvadersConstants.LangKeys.HUB_LIST_BTN).getKey(), "Team List");
        add(TranslationHelper.gui(InvadersConstants.LangKeys.HUB_HEALTH).getKey(), "Hub Health: %s/%s");
        
        add(TranslationHelper.itemTooltip(InvadersConstants.LangKeys.HUB).getKey(), "The very brain of your island");

        add(TranslationHelper.bossBar(InvadersConstants.LangKeys.INVASION).getKey(), "Invasion Waves: %s/%s");
        add(InvadersConstants.LangKeys.INVASION_WAVE_STARTED.getKey(), "Wave Started!");
        add(TranslationHelper.titles(InvadersConstants.LangKeys.INVASION_WAVE_ENEMY_COUNT).getKey(), "Invaders count: %s");
        add(InvadersConstants.LangKeys.INVASION_SUCCESS.getKey(), "Invasion Defended!");
        add(InvadersConstants.LangKeys.INVASION_FAILED.getKey(), TextFormatting.RED + "Invasion Failed!");
        
        add("itemGroup.invaders", "Invaders");
    }

    private void autoGenLang() {
        for (ONIIRegistryObject<Block> b : InvadersBlocks.blockList.keySet()) {
            if (b.doLangGen()) add("block.invaders." + MiscHelper.langToReg(b.getRegName()), b.getRegName());
        }
    }
}