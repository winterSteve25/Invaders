package wintersteve25.invaders.contents.base;

import fictioncraft.wintersteve25.fclib.common.interfaces.IHasProgress;
import fictioncraft.wintersteve25.fclib.common.interfaces.IHasValidItems;
import fictioncraft.wintersteve25.fclib.common.interfaces.IWorkable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import wintersteve25.invaders.utils.SlotArrangement;

import javax.annotation.Nonnull;
import java.util.function.BiPredicate;

public abstract class ONIBaseContainer extends Container {

    protected ONIBaseTE tileEntity;
    protected PlayerEntity playerEntity;
    protected IItemHandler playerInventory;

    protected ONIBaseContainer(int windowId, World world, BlockPos pos, PlayerInventory playerInventory, PlayerEntity player, ContainerType container) {
        super(container, windowId);

        tileEntity = (ONIBaseTE) (world.getBlockEntity(pos));
        this.playerEntity = player;
        this.playerInventory = new InvWrapper(playerInventory);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.getSlot(index);
        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            itemstack = stack.copy();

            int startPlayerInvIndex = getInvSize();
            int startPlayerHBIndex = getInvSize() + 27;
            int endPlayerInvIndex = slots.size();
            int startMachineIndex = 0;

            if (slot instanceof ONIMachineSlotHandler) {
                if (!this.moveItemStackTo(stack, startPlayerInvIndex, endPlayerInvIndex, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(stack, itemstack);
            } else {
                if (index >= startPlayerHBIndex) {
                    if (!this.moveItemStackTo(stack, startMachineIndex, startPlayerHBIndex, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                if (index >= startPlayerInvIndex && index < endPlayerInvIndex) {
                    if (!this.moveItemStackTo(stack, startMachineIndex, startPlayerInvIndex, false)) {
                        if (!this.moveItemStackTo(stack, startPlayerHBIndex, endPlayerInvIndex, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                }
            }

            if (stack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, stack);
        }

        return itemstack;
    }

    @Override
    public boolean stillValid(PlayerEntity p_75145_1_) {
        if (tileEntity == null) {
            return false;
        }

        if (tileEntity.getLevel() == null) {
            return false;
        }

        return stillValid(IWorldPosCallable.create(tileEntity.getLevel(), tileEntity.getBlockPos()), playerEntity, tileEntity.getLevel().getBlockState(tileEntity.getBlockPos()).getBlock());
    }

    protected void trackPower() {
        addDataSlot(new IntReferenceHolder() {
            @Override
            public int get() {
                return getEnergyStored() & 0xffff;
            }

            @Override
            public void set(int value) {
                tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(h -> {
                    int energyStored = h.getEnergyStored() & 0xffff0000;
                    h.receiveEnergy(energyStored + (value & 0xffff), false);
                });
            }
        });
        addDataSlot(new IntReferenceHolder() {
            @Override
            public int get() {
                return (getEnergyStored() >> 16) & 0xffff;
            }

            @Override
            public void set(int value) {
                tileEntity.getCapability(CapabilityEnergy.ENERGY).ifPresent(h -> {
                    int energyStored = h.getEnergyStored() & 0x0000ffff;
                    h.receiveEnergy(energyStored | (value << 16), false);
                });
            }
        });
    }

    protected void trackProgress() {
        if (tileEntity instanceof IHasProgress) {
            IHasProgress hasProgress = (IHasProgress) tileEntity;
            addDataSlot(new IntReferenceHolder() {
                @Override
                public int get() {
                    return getProgress() & 0xffff;
                }

                @Override
                public void set(int value) {
                    int progressStored = getProgress() & 0xffff0000;
                    hasProgress.setProgress(progressStored + (value & 0xffff));
                }
            });
            addDataSlot(new IntReferenceHolder() {
                @Override
                public int get() {
                    return (getProgress() >> 16) & 0xffff;
                }

                @Override
                public void set(int value) {
                    int progressStored = getProgress() & 0x0000ffff;
                    hasProgress.setProgress(progressStored | (value << 16));
                }
            });
        } else {
            throw new UnsupportedOperationException("Trying to track progress on a machine that does not support progress!");
        }
    }

    protected void trackTotalProgress() {
        if (tileEntity instanceof IHasProgress) {
            IHasProgress hasProgress = (IHasProgress) tileEntity;
            addDataSlot(new IntReferenceHolder() {
                @Override
                public int get() {
                    return getTotalProgress() & 0xffff;
                }

                @Override
                public void set(int value) {
                    int progressStored = getTotalProgress() & 0xffff0000;
                    hasProgress.setTotalProgress(progressStored + (value & 0xffff));
                }
            });
            addDataSlot(new IntReferenceHolder() {
                @Override
                public int get() {
                    return (getTotalProgress() >> 16) & 0xffff;
                }

                @Override
                public void set(int value) {
                    int progressStored = getTotalProgress() & 0x0000ffff;
                    hasProgress.setTotalProgress(progressStored | (value << 16));
                }
            });
        } else {
            throw new UnsupportedOperationException("Trying to track total progress on a machine that does not support progress!");
        }
    }

    protected void trackWorking() {
        if (tileEntity instanceof IWorkable) {
            IWorkable workable = (IWorkable) tileEntity;
            addDataSlot(new IntReferenceHolder() {
                @Override
                public int get() {
                    return getWorking() & 0xffff;
                }

                @Override
                public void set(int value) {
                    int workingStored = getWorking() & 0xffff0000;
                    int cache = workingStored + (value & 0xffff);

                    workable.setWorking(cache == 1);
                }
            });
        } else {
            throw new UnsupportedOperationException("Trying to track working on a machine that does not support progress!");
        }
    }

    public int getEnergyStored() {
        if (tileEntity == null) {
            return 0;
        }
        return tileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    public int getProgress() {
        if (tileEntity instanceof IHasProgress) {
            IHasProgress hasProgress = (IHasProgress) tileEntity;
            return hasProgress.getProgress();
        }
        throw new UnsupportedOperationException("trying to get progress on an tile that does not support progress");
    }

    public int getTotalProgress() {
        if (tileEntity instanceof IHasProgress) {
            IHasProgress hasProgress = (IHasProgress) tileEntity;
            return hasProgress.getTotalProgress();
        }
        throw new UnsupportedOperationException("trying to get total progress on an tile that does not support progress");
    }

    public byte getWorking() {
        if (tileEntity instanceof IWorkable) {
            IWorkable workable = (IWorkable) tileEntity;
            return (byte) (workable.getWorking() ? 1 : 0);
        }
        throw new UnsupportedOperationException("trying to get working on an tile that does not support progress");
    }

    public int getPowerCapacity() {
        if (tileEntity == null) {
            return 0;
        }
        return tileEntity.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0);
    }

    public ONIBaseTE getTileEntity() {
        return tileEntity;
    }

    protected int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    protected int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    protected void addPlayerSlots(int leftCol, int topRow) {
        // Player inventory
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);

        // Hotbar
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

    public BiPredicate<ItemStack, Integer> validItems() {
        if (tileEntity instanceof IHasValidItems) {
            IHasValidItems hasValidItems = (IHasValidItems) tileEntity;
            return hasValidItems.validItems();
        }
        return null;
    }

    public int getInvSize() {
        if (getTileEntity() instanceof ONIBaseInvTE) {
            ONIBaseInvTE invTE = (ONIBaseInvTE) getTileEntity();
            return invTE.getInvSize();
        }
        return 0;
    }

    protected void addMachineSlot(IItemHandler itemHandler, int index, SlotArrangement tuple) {
        addSlot(new ONIMachineSlotHandler(itemHandler, index, tuple.getPixelX(), tuple.getPixelY()));
    }

    public boolean hasProgress() {
        return getTileEntity() instanceof IHasProgress;
    }

    public static class ONIMachineSlotHandler extends SlotItemHandler {
        public ONIMachineSlotHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
            super(itemHandler, index, xPosition, yPosition);
        }

        @Override
        public boolean mayPlace(@Nonnull ItemStack stack) {
            return getItemHandler().isItemValid(this.getSlotIndex(), stack);
        }
    }
}
