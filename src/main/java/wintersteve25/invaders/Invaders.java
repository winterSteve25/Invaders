package wintersteve25.invaders;

import net.minecraft.data.DataGenerator;
import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wintersteve25.invaders.datagen.client.InvadersEngLangProvider;
import wintersteve25.invaders.events.ServerForgeEvents;
import wintersteve25.invaders.init.ModCapabilities;

@Mod(Invaders.MODID)
public class Invaders {
    public static final String MODID = "invaders";
    public static final Logger LOGGER = LogManager.getLogger(MODID);

    public Invaders() {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(Invaders::commonSetup);
        modEventBus.addListener(Invaders::gatherData);
    }

    public static void commonSetup(FMLCommonSetupEvent event) {
        final IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;

        ModCapabilities.register();

        forgeEventBus.addGenericListener(Entity.class, ServerForgeEvents::playerAttachEvent);
        forgeEventBus.addListener(ServerForgeEvents::playerSpawn);
        forgeEventBus.addListener(ServerForgeEvents::registerCommands);
    }

    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        if (event.includeClient()) {
            //en_US
            gen.addProvider(new InvadersEngLangProvider(gen));
        }
    }
}
