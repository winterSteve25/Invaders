package wintersteve25.invaders.init;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import wintersteve25.invaders.commands.ReRollSpawnCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InvadersConfigs {
    public static final String CAT_SPAWN = "spawn";

    public static ForgeConfigSpec COMMON_CONFIG;

    public static ForgeConfigSpec.ConfigValue<List<String>> SPAWN_BIOMES;
    public static ForgeConfigSpec.IntValue SEARCH_RADIUS_BASE;
    public static ForgeConfigSpec.IntValue MAX_SEARCH_TRIAL_COUNT;

    static {
        ForgeConfigSpec.Builder COMMON_BUILDER = new ForgeConfigSpec.Builder();

        List<String> biomes = new ArrayList<>();
        for (Biome biome : ForgeRegistries.BIOMES.getValues().stream().filter(biome -> biome.getBiomeCategory() == Biome.Category.FOREST).collect(Collectors.toList())) {
            if (biome.getRegistryName() == null) continue;
            biomes.add(biome.getRegistryName().toString());
        }

        COMMON_BUILDER.comment("Configs regarding player spawn").push(CAT_SPAWN);
        SPAWN_BIOMES = COMMON_BUILDER
                .comment("The biomes player are allowed to spawn in (chosen by random)")
                .define("spawnBiomes", biomes);
        SEARCH_RADIUS_BASE = COMMON_BUILDER
                .comment("The base search radius for possible biomes for the player to spawn in, will be multiplied by the trial count. A larger number would result in longer search time. So don't make this ridiculously high")
                .defineInRange("searchRadiusBase", 1200, 1, Integer.MAX_VALUE);
        MAX_SEARCH_TRIAL_COUNT = COMMON_BUILDER
                .comment("The max amount of trials it will search for the biome before giving up.")
                .defineInRange("maxSearchTrialCount", 8, 1, 32);
        COMMON_BUILDER.pop();

        COMMON_CONFIG = COMMON_BUILDER.build();
    }
}
