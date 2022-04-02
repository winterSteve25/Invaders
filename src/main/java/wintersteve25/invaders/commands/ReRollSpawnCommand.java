package wintersteve25.invaders.commands;

import com.google.common.collect.ImmutableList;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import fictioncraft.wintersteve25.fclib.common.helper.MiscHelper;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;
import wintersteve25.invaders.Invaders;
import wintersteve25.invaders.capabilities.ModPlayerData;
import wintersteve25.invaders.init.InvadersCapabilities;
import wintersteve25.invaders.init.InvadersConfigs;
import wintersteve25.invaders.utils.InvadersConstants;
import wintersteve25.invaders.utils.helpers.TranslationHelper;

import java.util.List;

public class ReRollSpawnCommand implements Command<CommandSource> {
    private static final ReRollSpawnCommand INSTANCE = new ReRollSpawnCommand();
    public static final ImmutableList<String> spawnBiomes = ImmutableList.of(
            Biomes.FOREST.location().toString(),
            Biomes.BIRCH_FOREST.location().toString(),
            Biomes.FLOWER_FOREST.location().toString(),
            Biomes.DARK_FOREST.location().toString()
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
        if (getSpawnBiomes().isEmpty()) return 1;

        int searchRadius = InvadersConfigs.SEARCH_RADIUS_BASE.get();

        LazyOptional<ModPlayerData> playerData = player.getCapability(InvadersCapabilities.PLAYER_DATA);
        if (!playerData.isPresent() || !playerData.resolve().isPresent()) {
            player.sendMessage(TranslationHelper.getCommandErrorMessage(InvadersConstants.LangKeys.SPAWN_COMMAND), player.getUUID());
            return 0;
        }

        ModPlayerData data = playerData.resolve().get();

        if (data.getReRollCount() > 3) {
            player.sendMessage(TranslationHelper.getCommandErrorMessage(InvadersConstants.LangKeys.SPAWN_COMMAND_EXCEED_MAX), player.getUUID());
            return 0;
        }

        Invaders.LOGGER.info("Attempting to spawn player in an appropriate spawning biome");

        data.addReRollCount(1);
        ServerWorld world = (ServerWorld) player.getCommandSenderWorld();
        BlockPos nearestBiome = null;

        int count = 1;

        // keep looking for biome until it finds it
        while (nearestBiome == null) {
            Invaders.LOGGER.info("Attempting to find appropriate biome, trial #" + count);

            ResourceLocation location = new ResourceLocation(getSpawnBiomes().get(MiscHelper.randomInRange(0, getSpawnBiomes().size()-1)));
            ResourceLocation current = world.getBiome(player.blockPosition()).getRegistryName();

            if (current == null) continue;

            if (getSpawnBiomes().contains(current.toString())) {
                nearestBiome = player.blockPosition();
            } else {
                Biome biome = world.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getOptional(location).orElse(null);
                if (biome == null) {
                    throw new IllegalStateException("Tried to spawn player in a biome that can not be found! This should really not be happening!");
                }
                nearestBiome = world.findNearestBiome(biome, player.blockPosition(), searchRadius * count, 8);
            }

            count++;

            if (nearestBiome == null) {
                Invaders.LOGGER.info("Didn't find " + location.toString() + " within " + searchRadius*(count-1) + " blocks, looking within " + searchRadius*count + " blocks");
            } else {
                Invaders.LOGGER.info("Found: " + location.toString());
            }

            if (count > InvadersConfigs.MAX_SEARCH_TRIAL_COUNT.get()) {
                break;
            }
        }

        if (nearestBiome == null) {
            Invaders.LOGGER.warn("Can not find appropriate spawn biome");
            return 0;
        }

        int y = world.getChunkSource().getGenerator().getFirstFreeHeight(nearestBiome.getX(), nearestBiome.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
        nearestBiome = new BlockPos(nearestBiome.getX(), y + 1, nearestBiome.getZ());

        player.setRespawnPosition(world.dimension(), nearestBiome, 0, true, false);
        player.teleportTo(world, nearestBiome.getX(), nearestBiome.getY(), nearestBiome.getZ(), 0, 0);

        return 1;
    }

    public static List<String> getSpawnBiomes() {
        return MiscHelper.isListValid(InvadersConfigs.SPAWN_BIOMES.get()) ? InvadersConfigs.SPAWN_BIOMES.get() : spawnBiomes;
    }
}
