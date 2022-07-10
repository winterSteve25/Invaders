package wintersteve25.invaders.contents.base.functional;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

@FunctionalInterface
public interface IBreakCondition {
    float onBreak(BlockState state, PlayerEntity player, IBlockReader blockReader, BlockPos pos);
}
