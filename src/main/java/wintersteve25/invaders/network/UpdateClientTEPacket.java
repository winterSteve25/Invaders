package wintersteve25.invaders.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import wintersteve25.invaders.Invaders;
import wintersteve25.invaders.utils.InvadersConstants;

import java.util.function.Supplier;

public class UpdateClientTEPacket {

    private final BlockPos pos;
    private final byte packetType;
    private final CompoundNBT compoundNBT;

    public UpdateClientTEPacket(TileEntity teIn, byte packetType) {
        this.pos = teIn.getBlockPos();
        this.packetType = packetType;
        this.compoundNBT = null;
    }

    public UpdateClientTEPacket(TileEntity teIn, byte packetType, CompoundNBT compoundNBT) {
        this.pos = teIn.getBlockPos();
        this.packetType = packetType;
        this.compoundNBT = compoundNBT;
    }

    public UpdateClientTEPacket(PacketBuffer buffer) {
        this.pos = buffer.readBlockPos();
        this.packetType = buffer.readByte();
        this.compoundNBT = buffer.readNbt();
    }

    public void encode(PacketBuffer buffer) {
        buffer.writeBlockPos(pos);
        buffer.writeByte(packetType);
        buffer.writeNbt(compoundNBT);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().setPacketHandled(true);
        if (pos == null) {
            Invaders.LOGGER.warn("Requested update but position is null");
            return;
        }

        TileEntity te = Minecraft.getInstance().level.getBlockEntity(pos);

        if (te == null) {
            Invaders.LOGGER.warn("Requested update at " + pos + " but no te is found");
            return;
        }

        if (packetType == InvadersConstants.PacketType.SYNC_DATA) {
            if (compoundNBT == null) return;
            te.handleUpdateTag(te.getBlockState(), compoundNBT);
        }
    }
}
