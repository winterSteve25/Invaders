package wintersteve25.invaders.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import wintersteve25.invaders.Invaders;
import wintersteve25.invaders.init.InvadersCapabilities;

import java.util.function.Supplier;

public class UpdateClientPlayerPacket {

    private final CompoundNBT compoundNBT;

    public UpdateClientPlayerPacket(CompoundNBT compoundNBT) {
        this.compoundNBT = compoundNBT;
    }

    public UpdateClientPlayerPacket(PacketBuffer buffer) {
        this.compoundNBT = buffer.readNbt();
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeNbt(compoundNBT);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        if (compoundNBT == null) {
            ctx.get().setPacketHandled(false);
            Invaders.LOGGER.warn("Requested client player update but nbt sent is null");
            return;
        }

        PlayerEntity player = Minecraft.getInstance().player;
        if (player == null) return;

        player.getCapability(InvadersCapabilities.PLAYER_DATA).ifPresent(cap -> {
            cap.read(compoundNBT);
        });

        ctx.get().setPacketHandled(true);
    }
}
