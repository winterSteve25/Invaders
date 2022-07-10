package wintersteve25.invaders.datagen.client;

import fictioncraft.wintersteve25.fclib.common.helper.MiscHelper;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import wintersteve25.invaders.Invaders;
import wintersteve25.invaders.contents.base.ONIBaseDirectional;
import wintersteve25.invaders.contents.base.ONIIRegistryObject;
import wintersteve25.invaders.init.InvadersBlocks;

public class InvadersModelProvider extends ItemModelProvider {
    public InvadersModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, Invaders.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        autoGenModels();
    }

    private ItemModelBuilder builder(ModelFile itemGenerated, String name) {
        return getBuilder(name).parent(itemGenerated).texture("layer0", "item/" + name);
    }

    private ItemModelBuilder builder(String path, String name) {
        return getBuilder(path).parent(getExistingFile(mcLoc("item/generated"))).texture("layer0", "item/" + name);
    }

    private void autoGenModels() {
        for (ONIIRegistryObject<Block> b : InvadersBlocks.blockList.keySet()) {
            Block block = b.get();
            if (b.doModelGen()) {
                if (block instanceof ONIBaseDirectional && ((ONIBaseDirectional) block).getModelFile() != null) {
                    withExistingParent(MiscHelper.langToReg(b.getRegName()), ((ONIBaseDirectional) block).getModelFile().getLocation());
                } else {
                    withExistingParent(MiscHelper.langToReg(b.getRegName()), modLoc("block/" + MiscHelper.langToReg(b.getRegName())));
                }
            }
        }

//        for (ONIIRegistryObject<Item> i : ONIItems.itemRegistryList) {
//            if (i.doModelGen()) {
//                Item item = i.get();
//                if (item instanceof ONIModificationItem) {
//                    ONIModificationItem mod = (ONIModificationItem) item;
//                    String name = i.getRegName();
//                    String processedName = MiscHelper.langToReg(mod.getModType().getName()) + name.charAt(name.length() - 1);
//                    builder(MiscHelper.langToReg(name), "modifications/" + processedName);
//                } else {
//                    String name = MiscHelper.langToReg(i.getRegName());
//                    if (item instanceof ONIIItem) {
//                        builder(name, ((ONIIItem) item).getItemCategory().getPathName() + name);
//                    } else {
//                        builder(name, name);
//                    }
//                }
//            }
//        }
    }
}