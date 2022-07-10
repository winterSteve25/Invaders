package wintersteve25.invaders.contents.base;

import wintersteve25.invaders.contents.base.functional.IGui;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import wintersteve25.invaders.Invaders;

import javax.annotation.Nullable;

public class ONIBaseMachine extends ONIBaseDirectional {

    // block builder properties
    private IGui gui;

    public ONIBaseMachine(int harvestLevel, float hardness, float resistance, String regName, SoundType soundType, Material material) {
        super(harvestLevel, hardness, resistance, regName, soundType, material);
    }

    public ONIBaseMachine(String regName, Properties properties) {
        super(regName, properties);
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
        if (!world.isClientSide()) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            super.use(state, world, pos, player, hand, rayTraceResult);
            if (isCorrectTe(tileEntity)) {
                if (gui != null || this instanceof IGui) {
                    if (gui == null) {
                        gui = (IGui) this;
                    }
                    if (gui.machineName() != null) {
                        INamedContainerProvider containerProvider = new INamedContainerProvider() {
                            @Override
                            public ITextComponent getDisplayName() {
                                return new TranslationTextComponent(gui.machineName());
                            }

                            @Override
                            public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
                                return gui.container(i, world, pos, playerInventory, playerEntity);
                            }
                        };
                        NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, tileEntity.getBlockPos());
                    }
                }
            } else {
                Invaders.LOGGER.warn("Wrong tileEntity type found, failed to create container");
            }
        }
        return ActionResultType.SUCCESS;
    }

    @Nullable
    public IGui getGui() {
        return gui == null ? this instanceof IGui ? (IGui) this : null : gui;
    }

    public void setGui(IGui gui) {
        this.gui = gui;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }
}