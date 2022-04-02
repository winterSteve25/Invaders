package wintersteve25.invaders.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;
import wintersteve25.invaders.Invaders;
import wintersteve25.invaders.capabilities.ModPlayerData;
import wintersteve25.invaders.init.ModCapabilities;
import wintersteve25.invaders.utils.InvadersConstants;
import wintersteve25.invaders.utils.helpers.MiscHelper;
import wintersteve25.invaders.utils.helpers.TranslationHelper;

public class ReRollSpawnCommand implements Command<CommandSource> {
    private static final ReRollSpawnCommand INSTANCE = new ReRollSpawnCommand();
    private static final ImmutableList<ResourceLocation> spawnBiomes = ImmutableList.of(
            Biomes.FOREST.getLocation(),
            Biomes.BIRCH_FOREST.getLocation(),
            Biomes.FLOWER_FOREST.getLocation(),
            Biomes.DARK_FOREST.getLocation()
    );

    public static ArgumentBuilder<CommandSource, ?> register() {
        return Commands.literal("spawn")
                .executes(INSTANCE);
    }

    @Override
    public int run(CommandContext<CommandSource> context) {
        Entity entity = context.getSource().getEntity();

        if (entity instanceof ServerPlayerEntity) {
            ServerPlayerEntity player = (ServerPlayerEntity) entity;
            return spawnPlayerAtAppropriateBiome(player);
        }

        return 0;
    }

    public static int spawnPlayerAtAppropriateBiome(ServerPlayerEntity player) {
        LazyOptional<ModPlayerData> playerData = player.getCapability(ModCapabilities.PLAYER_DATA);
        if (!playerData.isPresent() || !playerData.resolve().isPresent()) {
            player.sendMessage(TranslationHelper.getCommandErrorMessage(InvadersConstants.LangKeys.SPAWN_COMMAND), player.getUniqueID());
            return 0;
        }

        ModPlayerData data = playerData.resolve().get();

        if (data.getReRollCount() > 3) {
            player.sendMessage(TranslationHelper.getCommandErrorMessage(InvadersConstants.LangKeys.SPAWN_COMMAND_EXCEED_MAX), player.getUniqueID());
            return 0;
        }

        Invaders.LOGGER.info("Attempting to spawn player in an appropriate spawning biome");

        data.addReRollCount(1);
        ServerWorld world = (ServerWorld) player.getEntityWorld();
        BlockPos nearestBiome = null;

        // keep looking for biome until it finds it
        while (nearestBiome == null) {
            if (spawnBiomes.contains(world.getBiome(player.getPosition()).getRegistryName())) {
                nearestBiome = player.getPosition();
            } else {
                Biome biome = world.func_241828_r().getRegistry(Registry.BIOME_KEY).getOptional(spawnBiomes.get((int) MiscHelper.randomInRange(0, spawnBiomes.size()-1))).orElse(null);
                if (biome == null) {
                    throw new IllegalStateException("Tried to spawn player in a biome that can not be found! This should really not be happening!");
                }
                nearestBiome = world.func_241116_a_(biome, player.getPosition(), 6400, 8);
                Invaders.LOGGER.info(nearestBiome);
            }
        }

//        world.getChunkProvider().getChunkGenerator().

        player.func_242111_a(world.getDimensionKey(), nearestBiome, 0, true, false);
        player.teleport(world, nearestBiome.getX(), nearestBiome.getY(), nearestBiome.getZ(), 0, 0);

        return 1;
    }
}
