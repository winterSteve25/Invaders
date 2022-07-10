package wintersteve25.invaders.data.worlddata;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import wintersteve25.invaders.init.InvadersConfigs;
import wintersteve25.invaders.network.InvadersNetworking;
import wintersteve25.invaders.network.UpdateClientWorldDataPacket;
import wintersteve25.invaders.utils.InvadersConstants;

import java.util.*;

public class InvadersWorldData extends WorldSavedData {

    public static final String NAME = "invaders_data";

    private Map<UUID, BlockPos> ftbTeamHubPosData;
    private List<String> playerSpawnedBiomes;

    public InvadersWorldData() {
        super(NAME);

        ftbTeamHubPosData = new HashMap<>();
        if (InvadersConfigs.Common.PLAYER_SPAWN_DIFFERENT_BIOMES.get()) {
            playerSpawnedBiomes = new ArrayList<>();
        } else {
            playerSpawnedBiomes = null;
        }
    }

    @Override
    public void load(CompoundNBT compoundNBT) {
        InvadersWorldData data = loadData(compoundNBT);
        ftbTeamHubPosData = data.ftbTeamHubPosData;
        playerSpawnedBiomes = data.playerSpawnedBiomes;
    }

    @Override
    public CompoundNBT save(CompoundNBT compoundNBT) {
        return saveData(compoundNBT, this);
    }

    public static InvadersWorldData loadData(CompoundNBT compoundNBT) {
        INBT tag = compoundNBT.get("invaders_data");

        InvadersWorldData data = new InvadersWorldData();

        if (tag instanceof CompoundNBT) {
            CompoundNBT nbt = (CompoundNBT) tag;

            INBT keysNBT = nbt.get("ftbteams_hubPos_keys");
            INBT valuesNBT = nbt.get("ftbteams_hubPos_values");

            if (keysNBT instanceof ListNBT && valuesNBT instanceof ListNBT) {
                ListNBT keys = (ListNBT) keysNBT;
                ListNBT values = (ListNBT) valuesNBT;

                for (int i = 0; i < keys.size(); i++) {
                    INBT nullablePosNBT = values.get(i);
                    BlockPos pos = NBTUtil.readBlockPos((CompoundNBT) nullablePosNBT);

                    if (pos == InvadersConstants.INVALID_POS) {
                        data.ftbTeamHubPosData.put(NBTUtil.loadUUID(keys.get(i)), null);
                    } else {
                        data.ftbTeamHubPosData.put(NBTUtil.loadUUID(keys.get(i)), pos);
                    }
                }
            }

            if (InvadersConfigs.Common.PLAYER_SPAWN_DIFFERENT_BIOMES.get()) {
                INBT biomesNBT = nbt.get("players_spawnBiomes");

                if (biomesNBT instanceof ListNBT) {
                    ListNBT biomes = (ListNBT) biomesNBT;

                    for (INBT biomeStringNBT : biomes) {
                        data.playerSpawnedBiomes.add(biomeStringNBT.getAsString());
                    }
                }
            }
        }

        return data;
    }

    public static CompoundNBT saveData(CompoundNBT nbt, InvadersWorldData data) {
        CompoundNBT tag = new CompoundNBT();

        ListNBT teams = new ListNBT();
        ListNBT teamHubs = new ListNBT();

        Map<UUID, BlockPos> ftbTeamHubPosData = data.ftbTeamHubPosData;
        List<String> playerSpawnedBiomes = data.playerSpawnedBiomes;

        for (UUID uuid : ftbTeamHubPosData.keySet()) {
            teams.add(NBTUtil.createUUID(uuid));
        }

        for (BlockPos pos : ftbTeamHubPosData.values()) {
            if (pos == null) {
                teamHubs.add(NBTUtil.writeBlockPos(InvadersConstants.INVALID_POS));
            } else {
                teamHubs.add(NBTUtil.writeBlockPos(pos));
            }
        }

        tag.put("ftbteams_hubPos_keys", teams);
        tag.put("ftbteams_hubPos_values", teamHubs);

        if (InvadersConfigs.Common.PLAYER_SPAWN_DIFFERENT_BIOMES.get()) {
            ListNBT biomes = new ListNBT();
            for (String biome : playerSpawnedBiomes) {
                biomes.add(StringNBT.valueOf(biome));
            }
            tag.put("players_spawnBiomes", biomes);
        }

        nbt.put("invaders_data", tag);
        
        return nbt;
    }

    public BlockPos getTeamHubPos(UUID uuid) {
        return ftbTeamHubPosData.get(uuid);
    }

    public boolean hasTeamHubPos(UUID uuid) {
        return ftbTeamHubPosData.containsKey(uuid);
    }

    public void setTeamHubPos(UUID uuid, BlockPos pos) {
        ftbTeamHubPosData.remove(uuid);
        ftbTeamHubPosData.put(uuid, pos);
        setDirty();
    }

    public void removeTeamHubPos(UUID uuid) {
        ftbTeamHubPosData.remove(uuid);
        setDirty();
    }

    public boolean hasSpawnedBiome(String biome) {
        if (!InvadersConfigs.Common.PLAYER_SPAWN_DIFFERENT_BIOMES.get()) {
            return false;
        }
        return playerSpawnedBiomes.contains(biome);
    }

    public boolean hasBeenToAllBiomes(int totalBiomeCount) {
        if (!InvadersConfigs.Common.PLAYER_SPAWN_DIFFERENT_BIOMES.get()) {
            return true;
        }
        return playerSpawnedBiomes.size() >= totalBiomeCount;
    }

    public void addSpawnedBiome(ResourceLocation biome) {
        String biomeStr = biome.toString();

        if (!InvadersConfigs.Common.PLAYER_SPAWN_DIFFERENT_BIOMES.get()) {
            return;
        }

        if (!playerSpawnedBiomes.contains(biomeStr)) {
            playerSpawnedBiomes.add(biomeStr);
        }
        setDirty();
    }
    
    public static InvadersWorldData get(World worldIn) {
        if (!(worldIn instanceof ServerWorld)) {
            throw new RuntimeException("Attempted to get the data from client. This is should not happen");
        }

        if (worldIn.getServer() == null) {
            throw new RuntimeException("Server does not exist. This should not happens");
        }

        ServerWorld world = worldIn.getServer().getLevel(World.OVERWORLD);

        if (world == null) {
            throw new RuntimeException("Overworld does not exist. This should not happens");
        }

        DimensionSavedDataManager storage = world.getDataStorage();
        return storage.computeIfAbsent(InvadersWorldData::new, NAME);
    }

    public static void refreshClient(ServerPlayerEntity player) {
        DimensionSavedDataManager storage = player.getLevel().getDataStorage();
        InvadersWorldData worldData = storage.computeIfAbsent(InvadersWorldData::new, NAME);
        InvadersNetworking.sendToClient(new UpdateClientWorldDataPacket(worldData.save(new CompoundNBT())), player);
    }
}
