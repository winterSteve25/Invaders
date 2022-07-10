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
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.LazyOptional;
import wintersteve25.invaders.Invaders;
import wintersteve25.invaders.data.worlddata.InvadersWorldData;
import wintersteve25.invaders.data.capabilities.ModPlayerData;
import wintersteve25.invaders.init.InvadersBlocks;
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
        if (player.getCapability(InvadersCapabilities.PLAYER_DATA).map(cap -> cap.getHubPos() != null).orElse(false)) {
            player.sendMessage(TranslationHelper.getCommandErrorMessage(InvadersConstants.LangKeys.SPAWN_COMMAND_HUB_PLACED), player.getUUID());
            return 0;
        }

        int searchRadius = InvadersConfigs.Common.SEARCH_RADIUS_BASE.get();

        LazyOptional<ModPlayerData> playerData = player.getCapability(InvadersCapabilities.PLAYER_DATA);
        if (!playerData.isPresent() || !playerData.resolve().isPresent()) {
            player.sendMessage(TranslationHelper.getCommandErrorMessage(InvadersConstants.LangKeys.SPAWN_COMMAND), player.getUUID());
            return 0;
        }

        ModPlayerData data = playerData.resolve().get();

        if (data.getReRollCount() > InvadersConfigs.Common.MAX_RE_ROLL_COUNT.get()) {
            player.sendMessage(TranslationHelper.getCommandErrorMessage(InvadersConstants.LangKeys.SPAWN_COMMAND_EXCEED_MAX), player.getUUID());
            return 0;
        }

        player.sendMessage(TranslationHelper.getCommandSuccessMessage(InvadersConstants.LangKeys.SPAWN_COMMAND_SUCCESS), player.getUUID());
        Invaders.LOGGER.info("Attempting to spawn player in an appropriate spawning biome");

        player.inventory.clearContent();
        player.addItem(new ItemStack(InvadersBlocks.Core.HUB_BLOCK.asItem()));

        data.addReRollCount(1);
        ServerWorld world = (ServerWorld) player.getCommandSenderWorld();
        ResourceLocation current = world.getBiome(player.blockPosition()).getRegistryName();

        InvadersWorldData worldData = InvadersWorldData.get(world);

        BlockPos nearestBiome = null;
        int biomeCount = 0;

        while(nearestBiome == null) {
            biomeCount++;

            int searchCount = 1;
            String randBiome = getSpawnBiomes().get(MiscHelper.randomInRange(0, getSpawnBiomes().size()-1));
            ResourceLocation location = new ResourceLocation(randBiome);
            Biome biome = world.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getOptional(location).orElse(null);

            // if player haven't somehow already spawned at all biomes before
            if (data.getSpawnedBiomes().size() < getSpawnBiomes().size()) {
                // keep setting it to a biome the player haven't been spawned in
                while (data.hasPlayerSpawnedInBiomeBefore(randBiome)) {
                    randBiome = getSpawnBiomes().get(MiscHelper.randomInRange(0, getSpawnBiomes().size()-1));
                }
            }

            // do the same thing for world data
            if (!worldData.hasBeenToAllBiomes(getSpawnBiomes().size())) {
                while (worldData.hasSpawnedBiome(randBiome)) {
                    randBiome = getSpawnBiomes().get(MiscHelper.randomInRange(0, getSpawnBiomes().size()-1));
                }
            }

            // keep looking for biome until it finds it or when it reaches a limit
            while (nearestBiome == null) {
                Invaders.LOGGER.info("Attempting to find " + randBiome + " trial #" + searchCount);

                if (current == null) break;

                if (current.toString().equals(randBiome)) {
                    nearestBiome = player.blockPosition();
                    break;
                } else {
                    if (biome == null) {
                        throw new IllegalStateException("Tried to spawn player in a biome that can not be found! This should really not be happening!");
                    }
                    nearestBiome = world.findNearestBiome(biome, player.blockPosition(), searchRadius * searchCount, 8);
                }

                searchCount++;

                if (nearestBiome == null) {
                    Invaders.LOGGER.info("Didn't find " + location + " within " + searchRadius*(searchCount-1) + " blocks, looking within " + searchRadius*searchCount + " blocks");
                } else {
                    Invaders.LOGGER.info("Found: " + location);
                }

                if (searchCount > InvadersConfigs.Common.MAX_SEARCH_TRIAL_COUNT.get()) {
                    break;
                }
            }

            if (nearestBiome == null) {
                Invaders.LOGGER.warn("Can not find: " + randBiome + " spawn biome, there are " + biomeCount + " biome trials left before giving up");
            }

            if (biomeCount > InvadersConfigs.Common.MAX_BIOME_TRIAL_COUNT.get()) {
                break;
            }
        }

        if (nearestBiome == null) {
            Invaders.LOGGER.warn("Could not find: appropriate spawn biome");
            return 0;
        }

        int y = world.getChunkSource().getGenerator().getFirstFreeHeight(nearestBiome.getX(), nearestBiome.getZ(), Heightmap.Type.WORLD_SURFACE_WG);
        nearestBiome = new BlockPos(nearestBiome.getX(), y + 1, nearestBiome.getZ());

        player.setRespawnPosition(world.dimension(), nearestBiome, 0, true, false);
        player.teleportTo(world, nearestBiome.getX(), nearestBiome.getY(), nearestBiome.getZ(), 0, 0);

        ResourceLocation biome = world.getBiome(nearestBiome).getRegistryName();
        if (biome != null) {
            data.addBiomeSpawned(biome);
            worldData.addSpawnedBiome(biome);
        }

        InvadersWorldData.refreshClient(player);

        return 1;
    }

    private static List<String> getSpawnBiomes() {
        return MiscHelper.isListValid(InvadersConfigs.Common.SPAWN_BIOMES.get()) ? InvadersConfigs.Common.SPAWN_BIOMES.get() : spawnBiomes;
    }
}
