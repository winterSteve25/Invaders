package wintersteve25.invaders.contents.base.bounding;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.client.particle.DiggingParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.chunk.ChunkRenderCache;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootParameters;
import net.minecraft.pathfinding.PathType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import wintersteve25.invaders.Invaders;
import wintersteve25.invaders.contents.base.ONIBaseBlock;
import wintersteve25.invaders.init.InvadersBlocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Modified from https://github.com/mekanism/Mekanism/blob/1.16.x/src/main/java/mekanism/common/block/BlockBounding.java
 * Compatible with MIT License https://github.com/mekanism/Mekanism/blob/1.16.x/LICENSE
 */

@SuppressWarnings("deprecation")
public class ONIBoundingBlock extends ONIBaseBlock {

    @Nullable
    public static BlockPos getMainBlockPos(IBlockReader world, BlockPos thisPos) {
        ONIBoundingTE te = (ONIBoundingTE) world.getBlockEntity(thisPos);
        return te != null && te.receivedCoords && !thisPos.equals(te.getMainPos()) ? te.getMainPos() : null;
    }

    public ONIBoundingBlock() {
        this(AbstractBlock.Properties.of(Material.METAL).strength(3.5F, 4.8F).requiresCorrectToolForDrops().dynamicShape().noOcclusion());
    }

    public ONIBoundingBlock(AbstractBlock.Properties properties) {
        super("Bounding Block", properties);
        setDoModelGen(false);
        setDoLootTableGen(false);
        setDoLangGen(false);
    }

    @Nonnull
    @Deprecated
    public PushReaction getPushReaction(@Nonnull BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public ActionResultType use(@Nonnull BlockState pState, @Nonnull World pLevel, @Nonnull BlockPos pPos, @Nonnull PlayerEntity pPlayer, @Nonnull Hand pHand, @Nonnull BlockRayTraceResult pHit) {
        BlockPos mainPos = getMainBlockPos(pLevel, pPos);
        if (mainPos == null) {
            return ActionResultType.FAIL;
        } else {
            BlockState state1 = pLevel.getBlockState(mainPos);
            return state1.getBlock().use(state1, pLevel, mainPos, pPlayer, pHand, pHit);
        }
    }

    @Override
    public void onRemove(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockPos mainPos = getMainBlockPos(world, pos);
            if (mainPos != null) {
                BlockState mainState = world.getBlockState(mainPos);
                if (!mainState.isAir(world, mainPos)) {
                    world.removeBlock(mainPos, false);
                }
            }

            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    @Nonnull
    @Override
    public ItemStack getPickBlock(@Nonnull BlockState state, RayTraceResult target, @Nonnull IBlockReader world, @Nonnull BlockPos pos, PlayerEntity player) {
        BlockPos mainPos = getMainBlockPos(world, pos);
        if (mainPos == null) {
            return ItemStack.EMPTY;
        } else {
            BlockState state1 = world.getBlockState(mainPos);
            return state1.getBlock().getPickBlock(state1, target, world, mainPos, player);
        }
    }

    @Override
    public boolean removedByPlayer(@Nonnull BlockState state, World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, boolean willHarvest, FluidState fluidState) {
        if (willHarvest) {
            return true;
        } else {
            BlockPos mainPos = getMainBlockPos(world, pos);
            if (mainPos != null) {
                BlockState mainState = world.getBlockState(mainPos);
                if (!mainState.isAir(world, mainPos)) {
                    mainState.removedByPlayer(world, mainPos, player, false, mainState.getFluidState());
                }
            }

            return super.removedByPlayer(state, world, pos, player, false, fluidState);
        }
    }

    @Override
    public void onBlockExploded(BlockState state, World world, BlockPos pos, Explosion explosion) {
        BlockPos mainPos = getMainBlockPos(world, pos);
        if (mainPos != null) {
            BlockState mainState = world.getBlockState(mainPos);
            if (!mainState.isAir(world, mainPos)) {
                net.minecraft.loot.LootContext.Builder lootContextBuilder = (new net.minecraft.loot.LootContext.Builder((ServerWorld) world)).withRandom(world.random).withParameter(LootParameters.ORIGIN, Vector3d.atCenterOf(mainPos)).withParameter(LootParameters.TOOL, ItemStack.EMPTY).withOptionalParameter(LootParameters.BLOCK_ENTITY, mainState.hasTileEntity() ? world.getBlockEntity(mainPos) : null).withOptionalParameter(LootParameters.THIS_ENTITY, explosion.getExploder());
                if (explosion.blockInteraction == Explosion.Mode.DESTROY) {
                    lootContextBuilder.withParameter(LootParameters.EXPLOSION_RADIUS, explosion.radius);
                }

                mainState.getDrops(lootContextBuilder).forEach((stack) -> {
                    Block.popResource(world, mainPos, stack);
                });
                mainState.onBlockExploded(world, mainPos, explosion);
            }
        }

        super.onBlockExploded(state, world, pos, explosion);
    }

    @Override
    public void playerDestroy(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull BlockPos pos, @Nonnull BlockState state, TileEntity te, @Nonnull ItemStack stack) {
        BlockPos mainPos = getMainBlockPos(world, pos);
        if (mainPos != null) {
            BlockState mainState = world.getBlockState(mainPos);
            mainState.getBlock().playerDestroy(world, player, mainPos, mainState, world.getBlockEntity(pos), stack);
        } else {
            super.playerDestroy(world, player, pos, state, te, stack);
        }

        world.removeBlock(pos, false);
    }

    @Override
    public void neighborChanged(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull Block neighborBlock, @Nonnull BlockPos neighborPos, boolean isMoving) {
        BlockPos mainPos = getMainBlockPos(world, pos);
        if (mainPos != null) {
            world.getBlockState(mainPos).neighborChanged(world, mainPos, neighborBlock, neighborPos, isMoving);
        }
    }

    @Override
    public float getDestroyProgress(@Nonnull BlockState state, @Nonnull PlayerEntity player, @Nonnull IBlockReader world, @Nonnull BlockPos pos) {
        BlockPos mainPos = getMainBlockPos(world, pos);
        return mainPos == null ? super.getDestroyProgress(state, player, world, pos) : world.getBlockState(mainPos).getDestroyProgress(player, world, mainPos);
    }

    @Override
    public float getExplosionResistance(BlockState state, IBlockReader world, BlockPos pos, Explosion explosion) {
        BlockPos mainPos = getMainBlockPos(world, pos);
        return mainPos == null ? super.getExplosionResistance(state, world, pos, explosion) : world.getBlockState(mainPos).getExplosionResistance(world, mainPos, explosion);
    }

    @Nonnull
    @Override
    public BlockRenderType getRenderShape(@Nonnull BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(@Nonnull BlockState state, @Nonnull IBlockReader world) {
        return InvadersBlocks.Misc.BOUNDING_TE.get().create();
    }

    @Nonnull
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        BlockPos mainPos = getMainBlockPos(world, pos);
        if (mainPos == null) {
            return VoxelShapes.empty();
        } else {
            BlockState mainState;
            try {
                mainState = (world).getBlockState(mainPos);
            } catch (ArrayIndexOutOfBoundsException var9) {
                if (!(world instanceof ChunkRenderCache)) {
                    Invaders.LOGGER.error("Error getting bounding block shape, for position {}, with main position {}. World of type {}", pos, mainPos, world.getClass().getName());
                    return VoxelShapes.empty();
                }

                world = ((ChunkRenderCache) world).level;
                mainState = (world).getBlockState(mainPos);
            }

            VoxelShape shape = mainState.getShape(world, mainPos, context);
            BlockPos offset = pos.subtract(mainPos);
            return shape.move(-offset.getX(), -offset.getY(), -offset.getZ());
        }
    }

    @Override
    public boolean isPathfindable(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull PathType type) {
        return false;
    }

    @Override
    public boolean addHitEffects(BlockState state, World world, RayTraceResult target, ParticleManager manager) {
        if (target.getType() == RayTraceResult.Type.BLOCK && target instanceof BlockRayTraceResult) {
            BlockRayTraceResult blockTarget = (BlockRayTraceResult) target;
            BlockPos pos = blockTarget.getBlockPos();
            BlockPos mainPos = getMainBlockPos(world, pos);
            if (mainPos != null) {
                BlockState mainState = world.getBlockState(mainPos);
                if (!mainState.isAir(world, mainPos)) {
                    AxisAlignedBB axisalignedbb = state.getShape(world, pos).bounds();
                    double x = (double) pos.getX() + world.random.nextDouble() * (axisalignedbb.maxX - axisalignedbb.minX - 0.2D) + 0.1D + axisalignedbb.minX;
                    double y = (double) pos.getY() + world.random.nextDouble() * (axisalignedbb.maxY - axisalignedbb.minY - 0.2D) + 0.1D + axisalignedbb.minY;
                    double z = (double) pos.getZ() + world.random.nextDouble() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.2D) + 0.1D + axisalignedbb.minZ;
                    Direction side = blockTarget.getDirection();
                    if (side == Direction.DOWN) {
                        y = (double) pos.getY() + axisalignedbb.minY - 0.1D;
                    } else if (side == Direction.UP) {
                        y = (double) pos.getY() + axisalignedbb.maxY + 0.1D;
                    } else if (side == Direction.NORTH) {
                        z = (double) pos.getZ() + axisalignedbb.minZ - 0.1D;
                    } else if (side == Direction.SOUTH) {
                        z = (double) pos.getZ() + axisalignedbb.maxZ + 0.1D;
                    } else if (side == Direction.WEST) {
                        x = (double) pos.getX() + axisalignedbb.minX - 0.1D;
                    } else if (side == Direction.EAST) {
                        x = (double) pos.getX() + axisalignedbb.maxX + 0.1D;
                    }

                    manager.add((new DiggingParticle((ClientWorld) world, x, y, z, 0.0D, 0.0D, 0.0D, mainState)).init(mainPos).setPower(0.2F).scale(0.6F));
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items) {
    }

    /**
     * Method modified from https://github.com/mekanism/Mekanism/blob/1.16.x/src/main/java/mekanism/common/util/WorldUtils.java#L537
     */
    public static void makeBoundingBlock(@Nullable IWorld world, BlockPos boundingLocation, BlockPos orig) {
        if (world != null) {
            ONIBoundingBlock boundingBlock = (ONIBoundingBlock) InvadersBlocks.Misc.BOUNDING_BLOCK.get();
            BlockState newState = boundingBlock.defaultBlockState();
            world.setBlock(boundingLocation, newState, 3);
            if (!world.isClientSide()) {
                ONIBoundingTE tile = (ONIBoundingTE) world.getBlockEntity(boundingLocation);
                if (tile != null) {
                    tile.setMainLocation(orig);
                } else {
                    Invaders.LOGGER.warn("Unable to find Bounding Block Tile at: {}", boundingLocation);
                }
            }
        }
    }
}
