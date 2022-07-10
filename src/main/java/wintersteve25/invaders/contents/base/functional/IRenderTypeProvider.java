package wintersteve25.invaders.contents.base.functional;

import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;

@FunctionalInterface
public interface IRenderTypeProvider {
    BlockRenderType createRenderType(BlockState state);
}
