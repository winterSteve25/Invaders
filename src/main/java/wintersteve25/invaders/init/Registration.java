package wintersteve25.invaders.init;

import fictioncraft.wintersteve25.fclib.common.helper.MiscHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import wintersteve25.invaders.Invaders;
import wintersteve25.invaders.contents.base.ONIIRegistryObject;
import wintersteve25.invaders.utils.helpers.RegistryHelper;

public class Registration {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Invaders.MODID);
    public static final DeferredRegister<TileEntityType<?>> TE = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Invaders.MODID);
    public static final DeferredRegister<ContainerType<?>> CONTAINER = DeferredRegister.create(ForgeRegistries.CONTAINERS, Invaders.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Invaders.MODID);
    public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, Invaders.MODID);
    public static final DeferredRegister<SoundEvent> SOUND = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Invaders.MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, Invaders.MODID);
    
    public static void init() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        TE.register(eventBus);
        CONTAINER.register(eventBus);
        EFFECTS.register(eventBus);
        SOUND.register(eventBus);
        ENTITIES.register(eventBus);

        InvadersBlocks.register();
        registerBlocks();
        InvadersSounds.register();
        InvadersEntities.register();
        
        Invaders.LOGGER.info("ONIUtils Registration Completed");
    }

    public static void registerBlocks() {
        for (ONIIRegistryObject<Block> b : InvadersBlocks.blockList.keySet()) {
            if (InvadersBlocks.blockList.get(b) != null) {
                RegistryHelper.register(MiscHelper.langToReg(b.getRegName()), b::get, InvadersBlocks.blockList.get(b));
            } else {
                RegistryHelper.register(MiscHelper.langToReg(b.getRegName()), b::get);
            }
        }
    }
    
}