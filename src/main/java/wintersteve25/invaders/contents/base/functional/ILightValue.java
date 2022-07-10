package wintersteve25.invaders.contents.base.functional;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

@FunctionalInterface
public interface ILightValue {
    int getLightLevel(BlockState state, IBlockReader world, BlockPos pos);
}
