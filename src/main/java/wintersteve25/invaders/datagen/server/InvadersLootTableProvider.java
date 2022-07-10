package wintersteve25.invaders.datagen.server;

import fictioncraft.wintersteve25.fclib.common.helper.MiscHelper;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import wintersteve25.invaders.contents.base.ONIIRegistryObject;
import wintersteve25.invaders.init.InvadersBlocks;

public class InvadersLootTableProvider extends LootTableBase {
    public InvadersLootTableProvider(DataGenerator p_i50789_1_) {
        super(p_i50789_1_);
    }

    @Override
    protected void addTables() {
        standardTables();
    }

    private void standardTables() {
        for (ONIIRegistryObject<Block> b : InvadersBlocks.blockList.keySet()) {
            if (b.doLootTableGen()) lootTables.putIfAbsent(b.get(), createStandardTable(MiscHelper.langToReg(b.getRegName()), b.get()));
        }
    }
}