package wintersteve25.invaders.contents.invasion;

import dev.ftb.mods.ftbteams.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.data.Team;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import wintersteve25.invaders.contents.invasion.settings.InvasionSettings;
import wintersteve25.invaders.data.worlddata.InvasionWorldData;
import wintersteve25.invaders.init.InvadersCapabilities;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Mod.EventBusSubscriber
public class InvasionEventHandler {
    
    @SubscribeEvent
    public static void onEntityDespawn(LivingSpawnEvent.AllowDespawn event) {
        AtomicBoolean canDespawn = new AtomicBoolean(true);
        
        // can not despawn if it is an invasion mob
        event.getEntity().getCapability(InvadersCapabilities.INVASION_MOBS).ifPresent(cap -> {
            if (cap.isInvasionMob()) {
                canDespawn.set(false);
            }
        });
        
        if (!canDespawn.get()) {
            event.setResult(Event.Result.DENY);
        }
    }
    
    @SubscribeEvent
    public static void onEntityKilled(LivingDeathEvent event) {
        Entity entity = event.getEntity();
        World world = entity.getCommandSenderWorld();
        if (world.isClientSide()) return;
        entity.getCapability(InvadersCapabilities.INVASION_MOBS).ifPresent(cap -> {
            if (cap.isInvasionMob()) {
                Invasion invasion = InvasionWorldData.get(world).getInvasion(cap.getTargetedTeam());
                if (invasion != null) {
                    invasion.onInvasionMobKilled(entity);
                }
            }
        });
    }
    
    @SubscribeEvent
    public static void onEntityDrop(LivingDropsEvent event) {
        Entity entity = event.getEntity();
        entity.getCapability(InvadersCapabilities.INVASION_MOBS).ifPresent(cap -> {
            event.getDrops().clear();
            if (cap.isInvasionMob()) {
                List<ItemStack> stacks = InvasionSettings.JsonSettings.getLoot(cap.getDifficulty());
                for (ItemStack stack : stacks) {
                    event.getDrops().add(new ItemEntity(entity.getCommandSenderWorld(), entity.getX(), entity.getY(), entity.getZ(), stack));
                }
            }
        });
    }
    
    @SubscribeEvent
    public static void onServerTick(TickEvent.WorldTickEvent event) {
        ServerWorld world = (ServerWorld) event.world;
        if (event.side != LogicalSide.SERVER) return;
        InvasionWorldData data = InvasionWorldData.get(world);
        if (!data.anyOnGoingInvasions()) return;
        for (Invasion invasion : data.getInvasions()) {
            if (!invasion.isCompleted()) {
                invasion.tick(world);
            }
        }
    }

    @SubscribeEvent
    public static void loadInvasions(PlayerEvent.PlayerLoggedInEvent event) {
        Entity entity = event.getEntity();
        if (entity.getCommandSenderWorld().isClientSide()) return;
        for (ServerWorld world : entity.getServer().getAllLevels()) {
            InvasionWorldData data = InvasionWorldData.get(world);
            if (data.anyOnGoingInvasions()) {
                for (Invasion invasion : data.getInvasions()) {
                    if (FTBTeamsAPI.getPlayerTeam(invasion.getTargetedTeam()).getOnlineMembers().isEmpty()) continue;
                    invasion.resume(world);
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void unloadInvasions(PlayerEvent.PlayerLoggedOutEvent event) {
        PlayerEntity player = event.getPlayer();
        World world = player.getCommandSenderWorld();
        if (world.isClientSide()) return;
        ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
        Team team = FTBTeamsAPI.getPlayerTeam(serverPlayerEntity);
        if (team == null) return;
        Invasion invasion = InvasionWorldData.get(world).getInvasion(team.getId());
        if (invasion == null) return;
        if (team.getOnlineMembers().stream().anyMatch(p -> p.getUUID() != serverPlayerEntity.getUUID())) return;
        invasion.pause();
    }

    @SubscribeEvent
    public static void test(BlockEvent.FarmlandTrampleEvent event) {
        Entity entity = event.getEntity();
        World world = entity.getCommandSenderWorld();
        if (world.isClientSide()) return;
        if (entity instanceof ServerPlayerEntity) {
            Invasion invasion = Invasion.tryCreateInvasion(FTBTeamsAPI.getPlayerTeam((ServerPlayerEntity) entity).getId(), (ServerWorld) world);
            if (invasion == null) return;
            invasion.start((ServerWorld) world);
        }
    }
}
