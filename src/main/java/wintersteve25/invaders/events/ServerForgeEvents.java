package wintersteve25.invaders.events;

import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import fictioncraft.wintersteve25.fclib.api.events.JsonConfigEvent;
import fictioncraft.wintersteve25.fclib.api.json.base.IJsonConfig;
import fictioncraft.wintersteve25.fclib.api.json.base.JsonConfigBuilder;
import fictioncraft.wintersteve25.fclib.api.json.objects.SimpleConfigObject;
import fictioncraft.wintersteve25.fclib.api.json.objects.SimpleObjectMap;
import fictioncraft.wintersteve25.fclib.api.json.objects.providers.obj.templates.SimpleEntityProvider;
import fictioncraft.wintersteve25.fclib.api.json.objects.providers.obj.templates.SimpleItemProvider;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import wintersteve25.invaders.Invaders;
import wintersteve25.invaders.contents.invasion.settings.*;
import wintersteve25.invaders.data.capabilities.InvasionMobData;
import wintersteve25.invaders.data.capabilities.ModPlayerData;
import wintersteve25.invaders.data.capabilities.base.CapabilityProvider;
import wintersteve25.invaders.commands.InvadersCommands;
import wintersteve25.invaders.commands.ReRollSpawnCommand;
import wintersteve25.invaders.data.worlddata.InvadersWorldData;
import wintersteve25.invaders.init.InvadersCapabilities;

import java.util.concurrent.atomic.AtomicBoolean;

public class ServerForgeEvents {

    public static void registerJson(JsonConfigEvent.Registration event) {
        Invaders.INVASION_SETTINGS = new JsonConfigBuilder<>(SimpleObjectMap.class, new ResourceLocation(Invaders.MODID, "invasion_settings"))
                .addConfigObjectToList("enemy_milestones", new SimpleConfigObject(new EnemyMilestoneSetting("level_1_enemies", 1, Lists.newArrayList(new WaveBasedEnemy("creepers", new SimpleEntityProvider("minecraft:creeper", false), 2, 1)))), false)
                .addConfigObjectToList("loot_milestones", new SimpleConfigObject(new LootMilestoneSetting("level_1_loot", 1, Lists.newArrayList(new LootDrop("iron", new SimpleItemProvider("minecraft:iron_ingot", 1, "", false), 0.4f, 1, 4)))), false)
                .addConfigObjectToList("wave_milestones", new SimpleConfigObject(new WavesMilestoneSetting("diff_1", 1, 3, 10, 18)), false)
                .build();
    }

    public static void readJson(JsonConfigEvent.Post event) {
        IJsonConfig config = event.getConfig();
        if (config.UID().equals(new ResourceLocation(Invaders.MODID, "invasion_settings"))) {
            SimpleObjectMap obj = config.finishedConfig();
            if (obj == null) return;
            for (SimpleConfigObject configObject : obj.getConfigs().get("enemy_milestones")) {
                EnemyMilestoneSetting setting = (EnemyMilestoneSetting) configObject.getTarget();
                InvasionSettings.JsonSettings.enemyTypeMilestones.put(setting.getDifficulty(), setting.getEntities());
            }

            for (SimpleConfigObject configObject : obj.getConfigs().get("loot_milestones")) {
                LootMilestoneSetting setting = (LootMilestoneSetting) configObject.getTarget();
                InvasionSettings.JsonSettings.lootTypeMilestones.put(setting.getDifficulty(), setting.getLoot());
            }
            
            for (SimpleConfigObject configObject : obj.getConfigs().get("wave_milestones")) {
                WavesMilestoneSetting setting = (WavesMilestoneSetting) configObject.getTarget();
                InvasionSettings.JsonSettings.wavesMilestones.put(setting.getDifficulty(), setting);
            }
        }
    }
    
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
        InvadersCommands.registerCommands(dispatcher);
    }

    public static void playerAttachEvent(AttachCapabilitiesEvent<Entity> event) {
        Entity entity = event.getObject();
        if (entity instanceof PlayerEntity) {
            if (!entity.getCapability(InvadersCapabilities.PLAYER_DATA).isPresent()) {
                CapabilityProvider<ModPlayerData> provider = new CapabilityProvider<>(new ModPlayerData(), InvadersCapabilities.PLAYER_DATA);
                event.addCapability(new ResourceLocation(Invaders.MODID, "player_data"), provider);
                event.addListener(provider::invalidate);
            }
        }

        if (!entity.getCapability(InvadersCapabilities.INVASION_MOBS).isPresent()) {
            CapabilityProvider<InvasionMobData> provider = new CapabilityProvider<>(new InvasionMobData(), InvadersCapabilities.INVASION_MOBS);
            event.addCapability(new ResourceLocation(Invaders.MODID, "invasion_mob"), provider);
            event.addListener(provider::invalidate);
        }
    }

    public static void playerSpawn(EntityJoinWorldEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof ServerPlayerEntity)) return;
        if (entity.getCommandSenderWorld().isClientSide()) return;

        ServerPlayerEntity player = (ServerPlayerEntity) entity;
        AtomicBoolean spawned = new AtomicBoolean(false);
        player.getCapability(InvadersCapabilities.PLAYER_DATA).ifPresent(cap -> spawned.set(cap.isSpawned()));

        if (!spawned.get()) {
            player.getCapability(InvadersCapabilities.PLAYER_DATA).ifPresent(ModPlayerData::spawned);
        }
        if (spawned.get()) return;

        ReRollSpawnCommand.spawnPlayerAtAppropriateBiome(player);
    }

    public static void playerDeath(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            LazyOptional<ModPlayerData> capability = event.getOriginal().getCapability(InvadersCapabilities.PLAYER_DATA);
            capability.ifPresent(oldStore -> event.getPlayer().getCapability(InvadersCapabilities.PLAYER_DATA).ifPresent(newStore -> newStore.copy(oldStore)));
        }
    }

    public static void playerConnect(PlayerEvent.PlayerLoggedInEvent event) {
        if (!event.getPlayer().getCommandSenderWorld().isClientSide()) {
            InvadersWorldData.refreshClient((ServerPlayerEntity) event.getPlayer());
        }
    }
}
