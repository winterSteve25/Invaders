package wintersteve25.invaders.datagen.client;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import wintersteve25.invaders.Invaders;
import wintersteve25.invaders.contents.base.ONIBaseDirectional;
import wintersteve25.invaders.contents.base.ONIIRegistryObject;
import wintersteve25.invaders.init.InvadersBlocks;

public class InvadersStateProvider extends BlockStateProvider {
    public InvadersStateProvider(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, Invaders.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        autoGenStatesAndModels();
    }

    private void autoGenStatesAndModels() {
        for (ONIIRegistryObject<Block> b : InvadersBlocks.blockList.keySet()) {
            if (b.doStateGen()) {
                if (b.get() instanceof ONIBaseDirectional) {
                    ONIBaseDirectional directional = (ONIBaseDirectional) b.get();
                    directionalBlock(directional, directional.getModelFile(), directional.getAngelOffset());
                } else {
                    simpleBlock(b.get());
                }
            }
        }
    }
}