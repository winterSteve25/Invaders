package wintersteve25.invaders.init;

import fictioncraft.wintersteve25.fclib.common.helper.MiscHelper;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.RegistryObject;
import wintersteve25.invaders.contents.base.ONIBaseItemBlock;
import wintersteve25.invaders.contents.base.ONIBaseMachine;
import wintersteve25.invaders.contents.base.ONIIRegistryObject;
import wintersteve25.invaders.contents.base.bounding.ONIBoundingBlock;
import wintersteve25.invaders.contents.base.bounding.ONIBoundingTE;
import wintersteve25.invaders.contents.base.builders.ONIAbstractContainer;
import wintersteve25.invaders.contents.base.builders.ONIContainerBuilder;
import wintersteve25.invaders.contents.blocks.core.hub.HubBE;
import wintersteve25.invaders.utils.helpers.RegistryHelper;

import java.util.HashMap;
import java.util.Map;

public class InvadersBlocks {
    public static class Misc {
        public static final ONIBoundingBlock BOUNDING_BLOCK = new ONIBoundingBlock();
        public static final RegistryObject<TileEntityType<ONIBoundingTE>> BOUNDING_TE = RegistryHelper.registerTE(MiscHelper.langToReg("Bounding Block"), () -> TileEntityType.Builder.of(ONIBoundingTE::new, BOUNDING_BLOCK.get()).build(null));
    }

    public static class Core {
        public static final Tuple<ONIBaseMachine, ONIBaseItemBlock> HUB_BUILDER = HubBE.createBlock().build();
        public static final ONIBaseMachine HUB_BLOCK = HUB_BUILDER.getA();
        public static final RegistryObject<TileEntityType<HubBE>> HUB_BE = RegistryHelper.registerTE(MiscHelper.langToReg("Hub"), () -> TileEntityType.Builder.of(HubBE::new, HUB_BLOCK).build(null));
        public static final ONIContainerBuilder HUB_CONTAINER_BUILDER = HubBE.createContainer();
        public static final RegistryObject<ContainerType<ONIAbstractContainer>> HUB_CONTAINER = HUB_CONTAINER_BUILDER.getContainerTypeRegistryObject();
    }

    public static Map<ONIIRegistryObject<Block>, Item> blockList = new HashMap<>();

    public static void register() {
        Core.HUB_BLOCK.init(blockList, Core.HUB_BUILDER.getB());
        Misc.BOUNDING_BLOCK.init(blockList, null);
    }
}
