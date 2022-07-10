package wintersteve25.invaders.contents.base;

import fictioncraft.wintersteve25.fclib.common.helper.ISHandlerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import wintersteve25.invaders.contents.base.bounding.ONIIBoundingBlock;
import wintersteve25.invaders.contents.base.functional.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ONIBaseBlock extends Block implements ONIIRegistryObject<Block> {

    private final String regName;

    // block builder properties
    private IVoxelShapeProvider hitBox;
    private IRenderTypeProvider renderType;
    private IBreakCondition breakCondition;
    private ILightValue lightLevel;
    private boolean doModelGen = true;
    private boolean doStateGen = false;
    private boolean doLangGen = true;
    private boolean doLootTableGen = true;

    private Class<? extends TileEntity> teClass;
    private ITETypeProvider tileEntityType;

    public ONIBaseBlock(int harvestLevel, float hardness, float resistance, String regName) {
        this(harvestLevel, hardness, resistance, regName, SoundType.STONE);
    }

    public ONIBaseBlock(int harvestLevel, float hardness, float resistance, String regName, SoundType soundType) {
        this(harvestLevel, hardness, resistance, regName, soundType, Material.STONE);
    }

    public ONIBaseBlock(int harvestLevel, float hardness, float resistance, String regName, SoundType soundType, Material material) {
        this(regName, Properties.of(material).harvestLevel(harvestLevel).strength(hardness, resistance).sound(soundType));
    }

    public ONIBaseBlock(String regName, Properties properties) {
        super(properties);
        this.regName = regName;
    }

    @Override
    public PushReaction getPistonPushReaction(@Nonnull BlockState state) {
        return this.hasTileEntity(state) ? PushReaction.BLOCK : super.getPistonPushReaction(state);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return getHitBox() == null ? super.getShape(state, worldIn, pos, context) : getHitBox().createShape(state, worldIn, pos, context);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return getRenderType() == null ? super.getRenderShape(state) : getRenderType().createRenderType(state);
    }

    @Override
    public boolean doModelGen() {
        return doModelGen;
    }

    @Override
    public boolean doStateGen() {
        return doStateGen;
    }

    @Override
    public boolean doLangGen() {
        return doLangGen;
    }

    @Override
    public boolean doLootTableGen() {
        return doLootTableGen;
    }

    public ONIBaseBlock setDoModelGen(boolean doModelGen) {
        this.doModelGen = doModelGen;
        return this;
    }

    public ONIBaseBlock setDoStateGen(boolean doStateGen) {
        this.doStateGen = doStateGen;
        return this;
    }

    public ONIBaseBlock setDoLangGen(boolean doLangGen) {
        this.doLangGen = doLangGen;
        return this;
    }

    public ONIBaseBlock setDoLootTableGen(boolean doLootTableGen) {
        this.doLootTableGen = doLootTableGen;
        return this;
    }

    @Override
    public Block get() {
        return this;
    }

    @Override
    public String getRegName() {
        return regName;
    }

    public IVoxelShapeProvider getHitBox() {
        return hitBox;
    }

    public void setHitBox(IVoxelShapeProvider hitBox) {
        this.hitBox = hitBox;
    }

    public IRenderTypeProvider getRenderType() {
        return renderType;
    }

    public void setRenderType(IRenderTypeProvider renderType) {
        this.renderType = renderType;
    }

    public void setLightLevel(ILightValue lightLevel) {
        this.lightLevel = lightLevel;
    }

    public void setBreakCondition(IBreakCondition breakCondition) {
        this.breakCondition = breakCondition;
    }

    @Override
    public float getDestroyProgress(BlockState state, PlayerEntity player, IBlockReader blockReader, BlockPos pos) {
        return breakCondition == null ? super.getDestroyProgress(state, player, blockReader, pos) : breakCondition.onBreak(state, player, blockReader, pos);
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return lightLevel == null ? super.getLightValue(state, world, pos) : lightLevel.getLightLevel(state, world, pos);
    }

    @Override
    public void onRemove(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        if (state.hasTileEntity() && (!state.is(newState.getBlock()) || !newState.hasTileEntity())) {
            TileEntity tile = world.getBlockEntity(pos);
            if (tile instanceof ONIIBoundingBlock) {
                ((ONIIBoundingBlock) tile).onBreak(state);
            }
            if (isCorrectTe(tile) && tile instanceof ONIBaseTE) {
                ((ONIBaseTE) tile).onBroken(state, world, pos, newState, isMoving);
            }
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }

    @Override
    public void playerWillDestroy(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        if (isCorrectTe(world.getBlockEntity(pos))) {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if (tileEntity instanceof ONIBaseTE) {
                ONIBaseTE baseTE = (ONIBaseTE) tileEntity;
                baseTE.onHarvested(world, pos, state, player);
                if (baseTE instanceof ONIBaseInvTE) {
                    ONIBaseInvTE te = (ONIBaseInvTE) world.getBlockEntity(pos);
                    if (te != null) {
                        if (te.hasItem()) {
                            ISHandlerHelper.dropInventory(te.getItemHandler(), world, state, pos, te.getInvSize());
                        }
                    }
                }
            }
        }
        super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void setPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        ONIBaseTE tile = (ONIBaseTE) worldIn.getBlockEntity(pos);

        if (tile == null) {
            return;
        }

        if (tile instanceof ONIIBoundingBlock) {
            ((ONIIBoundingBlock) tile).onPlace();
        }

        tile.onPlacedBy(worldIn, pos, state, placer, stack);
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        TileEntity tile = world.getBlockEntity(pos);
        if (isCorrectTe(tile) && tile instanceof ONIBaseTE) {
            return ((ONIBaseTE) tile).canConnectRedstone(state, world, pos, side);
        }
        return super.canConnectRedstone(state, world, pos, side);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return getBlockEntityType() == null ? super.createTileEntity(state, world) : tileEntityType.createTEType(state, world).create();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return getBlockEntityType() != null && getTeClass() != null;
    }

    public ITETypeProvider getBlockEntityType() {
        return tileEntityType;
    }

    public void setTileEntityType(ITETypeProvider tileEntityType) {
        this.tileEntityType = tileEntityType;
    }

    public boolean isCorrectTe(TileEntity tile) {
        return getTeClass() != null && getTeClass().isInstance(tile);
    }

    public Class<? extends TileEntity> getTeClass() {
        return teClass;
    }

    public void setTeClass(Class<? extends TileEntity> teClass) {
        this.teClass = teClass;
    }
}