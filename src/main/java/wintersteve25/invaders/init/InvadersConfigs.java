package wintersteve25.invaders.init;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InvadersConfigs {
    public static final String CAT_SPAWN = "spawn";

    public static ForgeConfigSpec COMMON_CONFIG;
    public static ForgeConfigSpec CLIENT_CONFIG;

    public static class Common {
        public static ForgeConfigSpec.ConfigValue<List<String>> SPAWN_BIOMES;
        public static ForgeConfigSpec.IntValue SEARCH_RADIUS_BASE;
        public static ForgeConfigSpec.IntValue MAX_SEARCH_TRIAL_COUNT;
        public static ForgeConfigSpec.IntValue MAX_BIOME_TRIAL_COUNT;
        public static ForgeConfigSpec.IntValue MAX_RE_ROLL_COUNT;
        public static ForgeConfigSpec.BooleanValue PLAYER_SPAWN_DIFFERENT_BIOMES;
        
        public static ForgeConfigSpec.IntValue GLOW_INTERVALS;
        public static ForgeConfigSpec.IntValue WAVE_INTERVALS;
    }

    public static class Client {
        public static ForgeConfigSpec.DoubleValue SHOW_SPEED;
    }

    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

        List<String> biomes = new ArrayList<>();
        for (Biome biome : ForgeRegistries.BIOMES.getValues().stream().filter(biome -> biome.getBiomeCategory() == Biome.Category.FOREST).collect(Collectors.toList())) {
            if (biome.getRegistryName() == null) continue;
            biomes.add(biome.getRegistryName().toString());
        }

        COMMON_BUILDER.comment("Spawn configs").push(CAT_SPAWN);
        Common.SPAWN_BIOMES = COMMON_BUILDER
                .comment("The biomes player are allowed to spawn in (chosen by random)")
                .define("spawnBiomes", biomes);
        Common.SEARCH_RADIUS_BASE = COMMON_BUILDER
                .comment("The base search radius for possible biomes for the player to spawn in, will be multiplied by the trial count. A larger number would result in longer search time. So don't make this ridiculously high")
                .defineInRange("searchRadiusBase", 1200, 1, Integer.MAX_VALUE);
        Common.MAX_SEARCH_TRIAL_COUNT = COMMON_BUILDER
                .comment("The max amount of trials it will search for the biome before giving up.")
                .defineInRange("maxSearchTrialCount", 8, 0, 32);
        Common.MAX_BIOME_TRIAL_COUNT = COMMON_BUILDER
                .comment("The max amount of trials it will search for new biomes if the previous one is not found before giving up.")
                .defineInRange("maxSearchTrialCount", 8, 0, 8);
        Common.MAX_RE_ROLL_COUNT = COMMON_BUILDER
                .comment("How many times can the player reroll and get a new island")
                .defineInRange("maxReRollCount", 3, 0, Integer.MAX_VALUE);
        Common.PLAYER_SPAWN_DIFFERENT_BIOMES = COMMON_BUILDER
                .comment("If the players should all spawn on different biomes")
                .define("playerDiffBiome", true);
        Common.GLOW_INTERVALS = COMMON_BUILDER
                .comment("How long between each glow on all invading enemies")
                .defineInRange("glowInterval", 1000, 100, Integer.MAX_VALUE);
        Common.WAVE_INTERVALS = COMMON_BUILDER
                .comment("How long between each invasion wave")
                .defineInRange("waveInterval", 600, 0, Integer.MAX_VALUE);

        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();

        
        
        ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

        CLIENT_BUILDER.comment("GUI configs").push(CAT_SPAWN);

        Client.SHOW_SPEED = CLIENT_BUILDER
                .comment("How fast is the gui initial animation is going to show up")
                .defineInRange("guiShowSpeed", 0.03, 0.001, 1);

        CLIENT_BUILDER.pop();

        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }
}
