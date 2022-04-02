package wintersteve25.invaders.capabilities.base;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class CapabilityStorage<T extends ICapabilityHolder> implements Capability.IStorage<T> {
    @Nullable
    @Override
    public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
        return instance.write();
    }

    @Override
    public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
        instance.read((CompoundNBT) nbt);
    }
}
