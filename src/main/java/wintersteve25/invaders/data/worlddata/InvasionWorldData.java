package wintersteve25.invaders.data.worlddata;

import net.minecraft.nbt.*;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;
import wintersteve25.invaders.contents.invasion.Invasion;
import wintersteve25.invaders.utils.helpers.NBTHelper;

import java.util.*;

public class InvasionWorldData extends WorldSavedData {

    public static final String NAME = "invasions";

    private Map<UUID, Integer> ftbTeamDifficulties;
    private Map<UUID, Invasion> onGoingInvasions;

    public InvasionWorldData() {
        super(NAME);
        ftbTeamDifficulties = new HashMap<>();
        onGoingInvasions = new HashMap<>();
    }

    @Override
    public void load(CompoundNBT nbt) {
        ftbTeamDifficulties = NBTHelper.deserializeMap((CompoundNBT) nbt.get("difficulties"), NBTUtil::loadUUID, tag -> ((IntNBT)tag).getAsInt());
        onGoingInvasions = NBTHelper.deserializeMap((CompoundNBT) nbt.get("invasions"), NBTUtil::loadUUID, tag -> new Invasion((CompoundNBT) tag)); 
    }

    @Override
    public CompoundNBT save(CompoundNBT pCompound) {
        pCompound.put("difficulties", NBTHelper.serializeMap(ftbTeamDifficulties, NBTUtil::createUUID, IntNBT::valueOf));
        pCompound.put("invasions", NBTHelper.serializeMap(onGoingInvasions, NBTUtil::createUUID, Invasion::serializeNBT));
        return pCompound;
    }

    public void initializeTeamDifficulty(UUID teamUUID) {
        ftbTeamDifficulties.putIfAbsent(teamUUID, 1);
        setDirty();
    }

    public void increaseTeamDifficulty(UUID teamUUID) {
        int diff = ftbTeamDifficulties.get(teamUUID);
        ftbTeamDifficulties.remove(teamUUID);
        ftbTeamDifficulties.put(teamUUID, diff + 1);
        setDirty();
    }

    public int getTeamDifficulty(UUID teamUUID) {
        if (!ftbTeamDifficulties.containsKey(teamUUID)) {
            initializeTeamDifficulty(teamUUID);
            return 0;
        }
        return ftbTeamDifficulties.get(teamUUID);
    }

    public Invasion getInvasion(UUID team) {
        return onGoingInvasions.get(team);
    }
    
    public boolean anyOnGoingInvasions() {
        return !onGoingInvasions.isEmpty();
    }
    
    public Collection<Invasion> getInvasions() {
        return onGoingInvasions.values();
    }
    
    public void addInvasion(UUID team, Invasion invasion) {
        onGoingInvasions.putIfAbsent(team, invasion);
        setDirty();
    }
    
    public void removeInvasion(UUID team) {
        onGoingInvasions.remove(team);
        setDirty();
    }
    
    public static InvasionWorldData get(World world) {
        if (!(world instanceof ServerWorld)) {
            throw new RuntimeException("Attempted to get the data from client. This is should not happen");
        }

        if (world.getServer() == null) {
            throw new RuntimeException("Server does not exist. This should not happens");
        }

        DimensionSavedDataManager storage = ((ServerWorld) world).getDataStorage();
        return storage.computeIfAbsent(InvasionWorldData::new, NAME);
    }
}
