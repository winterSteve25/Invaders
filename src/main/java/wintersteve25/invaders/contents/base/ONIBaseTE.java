package wintersteve25.invaders.contents.base;

import fictioncraft.wintersteve25.fclib.common.base.FCLibTE;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.fml.network.PacketDistributor;
import wintersteve25.invaders.network.InvadersNetworking;
import wintersteve25.invaders.network.UpdateClientTEPacket;
import wintersteve25.invaders.utils.InvadersConstants;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ONIBaseTE extends FCLibTE {

    public ONIBaseTE(TileEntityType<?> te) {
        super(te);
    }

    protected void sendNBTUpdatePacket() {
        if (getLevel().isClientSide()) return;
        if (isRemoved()) return;

        if (getLevel() instanceof ServerWorld) {
            ((ServerWorld)getLevel()).getChunkSource().chunkMap.getPlayers(new ChunkPos(getBlockPos()), false).forEach((p) -> {
                if (!(p instanceof FakePlayer)) {
                    InvadersNetworking.sendToClient(new UpdateClientTEPacket(this, InvadersConstants.PacketType.SYNC_DATA, getUpdateTag()), p);
                }
            });
        } else {
            InvadersNetworking.getChannelInstance().send(PacketDistributor.TRACKING_CHUNK.with(() -> getLevel().getChunk(getBlockPos().getX() >> 4, getBlockPos().getZ() >> 4)), new UpdateClientTEPacket(this, InvadersConstants.PacketType.SYNC_DATA, getUpdateTag()));
        }
    }

    public void onHarvested(World world, BlockPos pos, BlockState state, PlayerEntity player) {
    }

    public void onBroken(BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState newState, boolean isMoving) {
    }

    public void onPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
    }

    public void getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
    }

    public void onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult) {
    }

    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, @Nullable Direction side) {
        return world.getBlockState(pos).getBlock().canConnectRedstone(state, world, pos, side);
    }
}
