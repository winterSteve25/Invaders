package wintersteve25.invaders;

import fictioncraft.wintersteve25.fclib.api.json.base.IJsonConfig;
import fictioncraft.wintersteve25.fclib.api.json.objects.providers.obj.ObjProviderType;
import net.minecraft.data.DataGenerator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.GlobalEntityTypeAttributes;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import wintersteve25.invaders.contents.entities.HubEntity;
import wintersteve25.invaders.contents.invasion.InvasionEventHandler;
import wintersteve25.invaders.contents.invasion.settings.*;
import wintersteve25.invaders.datagen.client.InvadersEngLangProvider;
import wintersteve25.invaders.datagen.client.InvadersModelProvider;
import wintersteve25.invaders.datagen.client.InvadersStateProvider;
import wintersteve25.invaders.datagen.server.InvadersLootTableProvider;
import wintersteve25.invaders.events.ArchitecturyEvents;
import wintersteve25.invaders.events.ServerForgeEvents;
import wintersteve25.invaders.init.*;
import wintersteve25.invaders.network.InvadersNetworking;

@Mod(Invaders.MODID)
public class Invaders {
    public static final String MODID = "invaders";
    public static final Logger LOGGER = LogManager.getLogger(MODID);
    public static final ItemGroup ITEM_GROUP = new ItemGroup("invaders") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(InvadersBlocks.Core.HUB_BLOCK.asItem());
        }
    };
    
    public static IJsonConfig INVASION_SETTINGS;

    public static ObjProviderType ENEMY_MILESTONE;
    public static ObjProviderType WAVE_BASED_ENEMY;
    public static ObjProviderType LOOT_MILESTONE;
    public static ObjProviderType LOOT;
    public static ObjProviderType WAVE_MILESTONE;
    
    public Invaders() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, InvadersConfigs.COMMON_CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, InvadersConfigs.CLIENT_CONFIG);
        Registration.init();
        
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(Invaders::commonSetup);
        modEventBus.addListener(Invaders::gatherData);
        modEventBus.addListener(Invaders::entityAttributes);
    }
    
    private static void commonSetup(FMLCommonSetupEvent event) {
        ENEMY_MILESTONE = new ObjProviderType("EnemyMilestone", null, null, null, EnemyMilestoneSetting.class);
        WAVE_BASED_ENEMY = new ObjProviderType("WaveBasedEnemy", null, null, null, WaveBasedEnemy.class);
        LOOT_MILESTONE = new ObjProviderType("LootMilestone", null, null, null, LootMilestoneSetting.class);
        LOOT = new ObjProviderType("Loot", null, null, null, LootDrop.class);
        WAVE_MILESTONE = new ObjProviderType("WaveMilestone", null, null, null, WavesMilestoneSetting.class);
        
        final IEventBus forgeEventBus = MinecraftForge.EVENT_BUS;
        InvadersCapabilities.register();
        InvadersNetworking.registerMessages();

        forgeEventBus.addGenericListener(Entity.class, ServerForgeEvents::playerAttachEvent);
        forgeEventBus.addListener(ServerForgeEvents::playerSpawn);
        forgeEventBus.addListener(ServerForgeEvents::playerDeath);
        forgeEventBus.addListener(ServerForgeEvents::playerConnect);
        forgeEventBus.addListener(ServerForgeEvents::registerCommands);
        forgeEventBus.addListener(ServerForgeEvents::registerJson);
        forgeEventBus.addListener(ServerForgeEvents::readJson);

        ArchitecturyEvents.onClaimChunk();
        ArchitecturyEvents.onPlayerJoinTeam();
        ArchitecturyEvents.onPlayerLeaveTeam();
    }

    private static void entityAttributes(EntityAttributeCreationEvent event) {
        event.put(InvadersEntities.HUB.get(), HubEntity.createAttribute().build());
    }
    
    private static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        if (event.includeClient()) {
            gen.addProvider(new InvadersEngLangProvider(gen));
            gen.addProvider(new InvadersStateProvider(gen, event.getExistingFileHelper()));
            gen.addProvider(new InvadersModelProvider(gen, event.getExistingFileHelper()));
        }

        if (event.includeServer()) {
            gen.addProvider(new InvadersLootTableProvider(gen));
        }
    }
}
