package wintersteve25.invaders.contents.base.functional;

import fictioncraft.wintersteve25.fclib.common.interfaces.IHasGui;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public interface IGui extends IHasGui {
    boolean canOpen(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult rayTraceResult, TileEntity te);
}
