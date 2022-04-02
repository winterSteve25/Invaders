package wintersteve25.invaders.capabilities;

import net.minecraft.nbt.CompoundNBT;
import wintersteve25.invaders.capabilities.base.ICapabilityHolder;

public class ModPlayerData implements ICapabilityHolder {
    private boolean spawned;
    private int reRollCount;

    public ModPlayerData() {
        spawned = false;
        reRollCount = 0;
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

    @Override
    public CompoundNBT write() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putBoolean("invaders_player_spawned", spawned);
        return nbt;
    }

    @Override
    public void read(CompoundNBT nbt) {
        spawned = nbt.getBoolean("invaders_player_spawned");
    }
}
