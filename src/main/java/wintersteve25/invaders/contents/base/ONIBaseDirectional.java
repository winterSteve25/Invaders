package wintersteve25.invaders.contents.base;

import fictioncraft.wintersteve25.fclib.common.helper.VoxelShapeHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DirectionalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.client.model.generators.ModelFile;

import javax.annotation.Nullable;

public class ONIBaseDirectional extends ONIBaseBlock {

    protected static final DirectionProperty FACING = DirectionalBlock.FACING;
    @Nullable
    private ModelFile modelFile;
    private int angelOffset;

    // for the block builder
    private boolean autoRotateShape = false;
    private boolean allowVertical = false;

    public ONIBaseDirectional(int harvestLevel, float hardness, float resistance, String regName) {
        this(harvestLevel, hardness, resistance, regName, SoundType.STONE, Material.STONE);
    }

    public ONIBaseDirectional(int harvestLevel, float hardness, float resistance, String regName, SoundType soundType, Material material) {
        this(regName, Properties.of(material).harvestLevel(harvestLevel).strength(hardness, resistance).sound(soundType));
    }

    public ONIBaseDirectional(String regName, Properties properties) {
        super(regName, properties);

        BlockState state = this.getStateDefinition().any();
        state.setValue(FACING, Direction.NORTH);

        this.registerDefaultState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> stateBuilder) {
        super.createBlockStateDefinition(stateBuilder);
        stateBuilder.add(FACING);
    }

    @Override
    public BlockState rotate(BlockState blockState, Rotation rotation) {
        return blockState.setValue(FACING, rotation.rotate(blockState.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState blockState, Mirror mirror) {
        return blockState.setValue(FACING, mirror.mirror(blockState.getValue(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext blockItemUseContext) {

        BlockState state = super.getStateForPlacement(blockItemUseContext);

        if (state == null) {
            return null;
        }

        state.setValue(FACING, blockItemUseContext.getNearestLookingDirection());

        return allowVertical ? this.defaultBlockState().setValue(FACING, blockItemUseContext.getNearestLookingDirection()) : this.defaultBlockState().setValue(FACING, blockItemUseContext.getHorizontalDirection().getOpposite());
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        if (autoRotateShape && getHitBox() != null) {
            if (allowVertical) {
                switch (state.getValue(FACING)) {
                    case NORTH:
                        return super.getShape(state, worldIn, pos, context);
                    case EAST:
                        return VoxelShapeHelper.rotate(super.getShape(state, worldIn, pos, context), Rotation.CLOCKWISE_90);
                    case SOUTH:
                        return VoxelShapeHelper.rotate(super.getShape(state, worldIn, pos, context), Rotation.CLOCKWISE_180);
                    case WEST:
                        return VoxelShapeHelper.rotate(super.getShape(state, worldIn, pos, context), Rotation.COUNTERCLOCKWISE_90);
                    case UP:
                        return VoxelShapeHelper.rotate(super.getShape(state, worldIn, pos, context), Direction.UP);
                    case DOWN:
                        return VoxelShapeHelper.rotate(super.getShape(state, worldIn, pos, context), Direction.DOWN);
                }
            } else {
                switch (state.getValue(FACING)) {
                    case NORTH:
                        return super.getShape(state, worldIn, pos, context);
                    case EAST:
                        return VoxelShapeHelper.rotate(super.getShape(state, worldIn, pos, context), Rotation.CLOCKWISE_90);
                    case SOUTH:
                        return VoxelShapeHelper.rotate(super.getShape(state, worldIn, pos, context), Rotation.CLOCKWISE_180);
                    case WEST:
                        return VoxelShapeHelper.rotate(super.getShape(state, worldIn, pos, context), Rotation.COUNTERCLOCKWISE_90);
                }
            }
        }
        return super.getShape(state, worldIn, pos, context);
    }

    @Nullable
    public ModelFile getModelFile() {
        return this.modelFile;
    }

    public int getAngelOffset() {
        return angelOffset;
    }

    public void setModelFile(ModelFile modelFile) {
        this.modelFile = modelFile;
    }

    public void setAngelOffset(int angelOffset) {
        this.angelOffset = angelOffset;
    }

    public boolean isAutoRotateShape() {
        return autoRotateShape;
    }

    public void setAutoRotateShape(boolean autoRotateShape) {
        this.autoRotateShape = autoRotateShape;
    }

    public boolean isAllowVertical() {
        return allowVertical;
    }

    public void setAllowVertical(boolean allowVertical) {
        this.allowVertical = allowVertical;
    }
}