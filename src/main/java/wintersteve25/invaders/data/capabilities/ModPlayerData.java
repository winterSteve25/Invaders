package wintersteve25.invaders.data.capabilities;

import net.minecraft.nbt.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import wintersteve25.invaders.data.capabilities.base.ICapabilityHolder;

import java.util.ArrayList;
import java.util.List;

public class ModPlayerData implements ICapabilityHolder {
    private boolean spawned;
    private int reRollCount;
    private BlockPos hubPos;
    private final List<String> spawnedBiomes;

    public ModPlayerData() {
        spawned = false;
        reRollCount = 0;
        hubPos = null;
        spawnedBiomes = new ArrayList<>();
    }

    public void copy(ModPlayerData playerData) {
        spawned = playerData.isSpawned();
        reRollCount = playerData.getReRollCount();
        hubPos = playerData.getHubPos();
        spawnedBiomes.clear();
        spawnedBiomes.addAll(playerData.getSpawnedBiomes());
    }

    public void spawned() {
        spawned = true;
    }

    public boolean isSpawned() {
        return spawned;
    }

    public void addReRollCount(int amount) {
        reRollCount += amount;
    }

    public void resetReRollCount() {
        reRollCount = 0;
    }

    public int getReRollCount() {
        return reRollCount;
    }

    public BlockPos getHubPos() {
        return hubPos;
    }

    public void setHubPos(BlockPos hubPos) {
        this.hubPos = hubPos;
    }

    public List<String> getSpawnedBiomes() {
        return spawnedBiomes;
    }

    public boolean hasPlayerSpawnedInBiomeBefore(String biome) {
        return spawnedBiomes.contains(biome);
    }

    public void addBiomeSpawned(ResourceLocation biome) {
        spawnedBiomes.add(biome.toString());
    }

    @Override
    public CompoundNBT write() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putBoolean("invaders_player_spawned", spawned);
        nbt.putInt("invaders_player_reRollCount", reRollCount);
        if (hubPos != null) {
            nbt.put("invaders_player_hubPos", NBTUtil.writeBlockPos(hubPos));
        }

        ListNBT list = new ListNBT();
        for (String string : spawnedBiomes) {
            list.add(StringNBT.valueOf(string));
        }
        nbt.put("invaders_player_spawnedBiomes", list);

        return nbt;
    }

    @Override
    public void read(CompoundNBT nbt) {
        spawned = nbt.getBoolean("invaders_player_spawned");
        reRollCount = nbt.getInt("invaders_player_reRollCount");

        CompoundNBT pos = (CompoundNBT) nbt.get("invaders_player_hubPos");
        if (pos != null) {
            hubPos = NBTUtil.readBlockPos(pos);
        }

        INBT inbt = nbt.get("invaders_player_spawnedBiomes");
        if (inbt instanceof ListNBT) {
            ListNBT list = (ListNBT) inbt;

            for (INBT value : list) {
                spawnedBiomes.add(value.getAsString());
            }
        }
    }
}
