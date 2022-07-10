package wintersteve25.invaders.contents.base;

import fictioncraft.wintersteve25.fclib.common.interfaces.IHasValidItems;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.BiPredicate;

public abstract class ONIBaseInvTE extends ONIBaseTE {

    protected final ItemStackHandler itemHandler = new InvadersInventoryHandler(this);
    protected final LazyOptional<IItemHandler> itemLazyOptional = LazyOptional.of(() -> itemHandler);

    public ONIBaseInvTE(TileEntityType<?> te) {
        super(te);
    }

    public ItemStackHandler getItemHandler() {
        return itemHandler;
    }
    
    public abstract int getInvSize();

    public boolean hasItem() {
        for (int i = 0; i < getInvSize(); i++) {
            if (!itemHandler.getStackInSlot(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        itemHandler.deserializeNBT(tag.getCompound("inv"));

        super.load(state, tag);
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag.put("inv", itemHandler.serializeNBT());

        return super.save(tag);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemLazyOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    public static class InvadersInventoryHandler extends ItemStackHandler {
        protected final ONIBaseInvTE tile;

        public InvadersInventoryHandler(ONIBaseInvTE inv) {
            this(inv.getInvSize(), inv);
        }

        public InvadersInventoryHandler(int size, ONIBaseInvTE inv) {
            super(size);
            this.tile = inv;
        }

        public void onContentsChanged(int slot) {
            this.tile.updateBlock();
        }

        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            if (!(this.tile instanceof IHasValidItems)) {
                return true;
            } else {
                IHasValidItems validItems = (IHasValidItems)this.tile;
                BiPredicate<ItemStack, Integer> valids = validItems.validItems();
                return valids == null || valids.test(stack, slot);
            }
        }

        @Nonnull
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (!(this.tile instanceof IHasValidItems)) {
                return super.insertItem(slot, stack, simulate);
            } else {
                IHasValidItems validItems = (IHasValidItems)this.tile;
                BiPredicate<ItemStack, Integer> valids = validItems.validItems();
                if (valids == null) {
                    return super.insertItem(slot, stack, simulate);
                } else {
                    return valids.test(stack, slot) ? super.insertItem(slot, stack, simulate) : stack;
                }
            }
        }
    }
}
