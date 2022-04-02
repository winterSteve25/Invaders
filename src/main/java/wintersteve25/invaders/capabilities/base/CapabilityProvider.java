package wintersteve25.invaders.capabilities.base;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CapabilityProvider<T extends ICapabilityHolder> implements ICapabilitySerializable<CompoundNBT> {
    private final T capabilityData;
    private final Capability<T> capabilityType;
    private final LazyOptional<T> lazyOptional;

    public CapabilityProvider(T capabilityData, Capability<T> capabilityType) {
        this.capabilityData = capabilityData;
        this.capabilityType = capabilityType;
        lazyOptional = LazyOptional.of(() -> capabilityData);
    }

    public void invalidate() {
        lazyOptional.invalidate();
    }

    @Nonnull
    @Override
    public <K> LazyOptional<K> getCapability(@Nonnull Capability<K> cap, @Nullable Direction side) {
        if (cap == capabilityType) {
            return lazyOptional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public CompoundNBT serializeNBT() {
        if (capabilityType == null) {
            return new CompoundNBT();
        } else {
            return (CompoundNBT) capabilityType.writeNBT(capabilityData, null);
        }
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (capabilityType != null) {
            capabilityType.readNBT(capabilityData, null, nbt);
        }
    }
}
