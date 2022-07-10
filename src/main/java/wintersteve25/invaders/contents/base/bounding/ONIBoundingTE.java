package wintersteve25.invaders.contents.base.bounding;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import wintersteve25.invaders.contents.base.ONIBaseTE;
import wintersteve25.invaders.init.InvadersBlocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Modified from https://github.com/mekanism/Mekanism/blob/1.16.x/src/main/java/mekanism/common/tile/TileEntityBoundingBlock.java
 * Compatible with MIT License https://github.com/mekanism/Mekanism/blob/1.16.x/LICENSE
 */
public class ONIBoundingTE extends ONIBaseTE {

    private BlockPos mainPos;
    public boolean receivedCoords;

    public ONIBoundingTE() {
        this(InvadersBlocks.Misc.BOUNDING_TE.get());
    }

    public ONIBoundingTE(TileEntityType<ONIBoundingTE> type) {
        super(type);
        this.mainPos = BlockPos.ZERO;
    }

    public void setMainLocation(BlockPos pos) {
        this.receivedCoords = pos != null;
        if (this.isServer()) {
            this.mainPos = pos;
            sendNBTUpdatePacket();
        }
    }

    public BlockPos getMainPos() {
        if (this.mainPos == null) {
            this.mainPos = BlockPos.ZERO;
        }

        return this.mainPos;
    }

    @Nullable
    public TileEntity getMainTile() {
        return this.receivedCoords ? level.getBlockEntity(getMainPos()) : null;
    }

    @Nullable
    public ONIBaseTE getMainONITile() {
        return this.receivedCoords ? (ONIBaseTE) level.getBlockEntity(getMainPos()) : null;
    }

    @Override
    public void load(@Nonnull BlockState state, @Nonnull CompoundNBT nbtTags) {
        super.load(state, nbtTags);

        INBT nbt = nbtTags.get("main");
        if (nbt != null) {
            this.mainPos = NBTUtil.readBlockPos((CompoundNBT) nbt);
        }

        this.receivedCoords = nbtTags.getBoolean("receivedCoords");
    }

    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT nbtTags) {
        super.save(nbtTags);
        nbtTags.put("main", NBTUtil.writeBlockPos(this.getMainPos()));
        nbtTags.putBoolean("receivedCoords", this.receivedCoords);
        return nbtTags;
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT updateTag = super.getUpdateTag();
        updateTag.put("main", NBTUtil.writeBlockPos(this.getMainPos()));
        updateTag.putBoolean("receivedCoords", this.receivedCoords);
        return updateTag;
    }

    @Override
    public void handleUpdateTag(BlockState state, @Nonnull CompoundNBT tag) {
        super.handleUpdateTag(state, tag);

        INBT nbt = tag.get("main");
        if (nbt != null) {
            this.mainPos = NBTUtil.readBlockPos((CompoundNBT) nbt);
        }

        this.receivedCoords = tag.getBoolean("receivedCoords");
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (getMainTile() != null) {
            return getMainTile().getCapability(cap, side);
        }

        return LazyOptional.empty();
    }
}
