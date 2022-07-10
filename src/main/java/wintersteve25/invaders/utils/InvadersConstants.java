package wintersteve25.invaders.utils;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import wintersteve25.invaders.Invaders;
import wintersteve25.invaders.utils.helpers.TranslationHelper;

public class InvadersConstants {
    public static class LangKeys {
        // COMMANDS
        public static final String SPAWN_COMMAND = "spawn.error";
        public static final String SPAWN_COMMAND_EXCEED_MAX = "spawn.exceedMax";
        public static final String SPAWN_COMMAND_HUB_PLACED = "spawn.hubPlaced";
        public static final String SPAWN_COMMAND_SUCCESS = "spawn.success";
        public static final String DUMP_FOREST = "dumpForest";
        
        // TOOLTIPS
        public static final TranslationTextComponent HOLD_SHIFT = new TranslationTextComponent("invaders.tooltips.shiftInfo");

        // BLOCKS
        public static final String HUB = "hub";
        public static final String HUB_OWNER = "hub.owner";
        public static final String HUB_LIST_BTN = "hub.btn.list";

        public static final String HUB_HEALTH = "hub.health";
        
        // MESSAGES
        public static final TranslationTextComponent PLACE_HUB = TranslationHelper.message("place_hub_warning");
        public static final TranslationTextComponent DESTROY_HUB = TranslationHelper.message("destroy_hub");
        public static final TranslationTextComponent FAILED_TO_CLAIM_CHUNK = TranslationHelper.message("failed_to_claim_chunk");
    
        
        public static final TranslationTextComponent INVASION_WAVE_STARTED = TranslationHelper.titles("invasion.waveStarted");
        public static final String INVASION_WAVE_ENEMY_COUNT = "invasion.waveEnemyCount";
        public static final TranslationTextComponent INVASION_SUCCESS = TranslationHelper.titles("invasion.success");
        public static final String INVASION = "invasion";
    }

    public static class Resources {
        public static final ResourceLocation WIDGETS = new ResourceLocation(Invaders.MODID, "textures/gui/widgets.png");
        public static final ResourceLocation BASE_GUI = new ResourceLocation(Invaders.MODID, "textures/gui/base.png");
        public static final TextureElement BUTTON_BASE = TextureElement.createDefault(16, 0);
        public static final TextureElement BUTTON_HOME = TextureElement.createDefault(48, 0);
    }

    public static class PacketType {
        public static final byte SYNC_DATA = 0;
    }

    public static final BlockPos INVALID_POS = new BlockPos(Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE);
}
