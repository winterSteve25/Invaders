package wintersteve25.invaders.contents.base.bounding;

import net.minecraft.block.BlockState;

public interface ONIIBoundingBlock {
    void onPlace();

    void onBreak(BlockState oldState);
}

