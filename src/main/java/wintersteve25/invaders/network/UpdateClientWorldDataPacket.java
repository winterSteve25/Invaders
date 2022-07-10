package wintersteve25.invaders.network;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import wintersteve25.invaders.data.worlddata.InvadersWorldData;
import wintersteve25.invaders.data.worlddata.InvadersWorldDataClient;

import java.util.HashMap;
import java.util.function.Supplier;

public class UpdateClientWorldDataPacket {
    private final CompoundNBT compoundNBT;

    public UpdateClientWorldDataPacket(CompoundNBT compoundNBT) {
        this.compoundNBT = compoundNBT;
    }

    public UpdateClientWorldDataPacket(PacketBuffer buffer) {
        this.compoundNBT = buffer.readNbt();
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeNbt(compoundNBT);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> InvadersWorldDataClient.data = InvadersWorldData.loadData(compoundNBT));
        ctx.get().setPacketHandled(true);
    }
}
