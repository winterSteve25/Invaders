package wintersteve25.invaders.events;

import com.google.common.collect.Sets;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import dev.ftb.mods.ftbchunks.data.ClaimResult;
import dev.ftb.mods.ftbchunks.event.ClaimedChunkEvent;
import dev.ftb.mods.ftbteams.event.TeamEvent;
import me.shedaniel.architectury.event.CompoundEventResult;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import wintersteve25.invaders.Invaders;
import wintersteve25.invaders.contents.blocks.core.hub.HubBE;
import wintersteve25.invaders.data.worlddata.InvadersWorldData;
import wintersteve25.invaders.init.InvadersBlocks;
import wintersteve25.invaders.init.InvadersCapabilities;
import wintersteve25.invaders.network.InvadersNetworking;
import wintersteve25.invaders.network.UpdateClientPlayerPacket;
import wintersteve25.invaders.utils.InvadersConstants;

import java.util.concurrent.atomic.AtomicReference;

public class ArchitecturyEvents {
    public static void onClaimChunk() {
        ClaimedChunkEvent.BEFORE_CLAIM.register((commandSource, claimedChunk) -> {
            if (claimedChunk.pos.dimension.location().equals(new ResourceLocation("minecraft", "overworld"))) {
                try {
                    ServerPlayerEntity player = commandSource.getPlayerOrException();
                    AtomicReference<CompoundEventResult<ClaimResult>> returnType = new AtomicReference<>(null);
                    player.getCapability(InvadersCapabilities.PLAYER_DATA).ifPresent(cap -> {
                        BlockPos pos = cap.getHubPos();
                        if (pos == null) return;
                        TileEntity tileEntity = commandSource.getLevel().getBlockEntity(pos);
                        if (tileEntity instanceof HubBE) {
                            HubBE hub = (HubBE) tileEntity;
                            if (!hub.getClaimableChunks().contains(claimedChunk.pos.getChunkPos())) return;
                            returnType.set(CompoundEventResult.pass());
                        }
                    });

                    if (returnType.get() == null) {
                        returnType.set(CompoundEventResult.interruptFalse(new ClaimResult() {
                            @Override
                            public boolean isSuccess() {
                                return false;
                            }
                        }));
                        commandSource.sendFailure(InvadersConstants.LangKeys.FAILED_TO_CLAIM_CHUNK);
                    }

                    return returnType.get();

                } catch (CommandSyntaxException ignored) {
                    Invaders.LOGGER.warn("A chunk was just claimed by a non player entity");
                }
            }

            return CompoundEventResult.pass();
        });
    }

    public static void onPlayerJoinTeam() {
        TeamEvent.PLAYER_JOINED_PARTY.register(event -> {
            ServerPlayerEntity player = event.getPlayer();
            if (!player.getLevel().isClientSide()) {
                player.getCapability(InvadersCapabilities.PLAYER_DATA).ifPresent(cap -> {
                    InvadersWorldData worldData = InvadersWorldData.get(player.getLevel());
                    if (worldData.hasTeamHubPos(event.getPreviousTeam().actualTeam.getId())) {
                        if (player.inventory.hasAnyOf(Sets.newHashSet(InvadersBlocks.Core.HUB_BLOCK.asItem()))) {
                            player.inventory.removeItem(new ItemStack(InvadersBlocks.Core.HUB_BLOCK.asItem()));
                        }
                        if (cap.getHubPos() != null) {
                            player.getLevel().removeBlock(cap.getHubPos(), false);
                        }

                        cap.setHubPos(worldData.getTeamHubPos(event.getPreviousTeam().actualTeam.getId()));
                        InvadersNetworking.sendToClient(new UpdateClientPlayerPacket(cap.write()), player);
                    } else {
                        // this means the party is created, so sets the hub pos of the team to the hub pos of the creator
                        worldData.setTeamHubPos(event.getPreviousTeam().actualTeam.getId(), cap.getHubPos());
                    }
                    InvadersWorldData.refreshClient(player);
                });
            }
        });
    }

    public static void onPlayerLeaveTeam() {
        TeamEvent.PLAYER_LEFT_PARTY.register(event -> {
            ServerPlayerEntity player = event.getPlayer();
            if (player != null && !player.getLevel().isClientSide()) {
                player.getCapability(InvadersCapabilities.PLAYER_DATA).ifPresent(cap -> {
                    cap.setHubPos(null);
                    InvadersNetworking.sendToClient(new UpdateClientPlayerPacket(cap.write()), player);
                });

                if (event.getTeamDeleted()) {
                    InvadersWorldData worldData = InvadersWorldData.get(player.getLevel());
                    worldData.removeTeamHubPos(event.getTeam().getId());
                    InvadersWorldData.refreshClient(player);
                }
            }
        });
    }
}
