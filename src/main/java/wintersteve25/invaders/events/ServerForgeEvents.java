package wintersteve25.invaders.events;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import wintersteve25.invaders.Invaders;
import wintersteve25.invaders.capabilities.ModPlayerData;
import wintersteve25.invaders.capabilities.base.CapabilityProvider;
import wintersteve25.invaders.commands.ReRollSpawnCommand;
import wintersteve25.invaders.init.InvadersCapabilities;

import java.util.concurrent.atomic.AtomicBoolean;

public class ServerForgeEvents {
    public static void registerCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("invaders").then(ReRollSpawnCommand.register()));
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
}
