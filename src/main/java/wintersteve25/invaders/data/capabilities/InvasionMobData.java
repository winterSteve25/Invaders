package wintersteve25.invaders.data.capabilities;

import net.minecraft.nbt.CompoundNBT;
import wintersteve25.invaders.data.capabilities.base.ICapabilityHolder;

import java.util.UUID;

public class InvasionMobData implements ICapabilityHolder {
    private int difficulty;
    private int wave;
    private UUID targetedTeam;
    private boolean isInvasionMob;
    
    @Override
    public CompoundNBT write() {
        CompoundNBT invasionEnemyTag = new CompoundNBT();
        invasionEnemyTag.putBoolean("isInvasionMob", isInvasionMob);
        if (isInvasionMob) {
            invasionEnemyTag.putInt("difficulty", difficulty);
            invasionEnemyTag.putInt("wave", wave);
            invasionEnemyTag.putUUID("targetedTeam", targetedTeam);
        }
        return invasionEnemyTag;
    }

    @Override
    public void read(CompoundNBT nbt) {
        isInvasionMob = nbt.getBoolean("isInvasionMob");
        if (isInvasionMob) {
            difficulty = nbt.getInt("difficulty");
            wave = nbt.getInt("wave");
            targetedTeam = nbt.getUUID("targetedTeam");
        }
    }

    public int getDifficulty() {
        return difficulty;
    }

    public int getWave() {
        return wave;
    }

    public UUID getTargetedTeam() {
        return targetedTeam;
    }

    public boolean isInvasionMob() {
        return isInvasionMob;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    public void setWave(int wave) {
        this.wave = wave;
    }

    public void setTargetedTeam(UUID targetedTeam) {
        this.targetedTeam = targetedTeam;
    }

    public void setInvasionMob(boolean invasionMob) {
        isInvasionMob = invasionMob;
    }
}
