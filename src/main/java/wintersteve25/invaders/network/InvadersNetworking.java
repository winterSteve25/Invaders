package wintersteve25.invaders.network;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import wintersteve25.invaders.Invaders;

public class InvadersNetworking {
    private static SimpleChannel ChannelInstance;
    private static int ID = 0;

    private static int nextID() {
        return ID++;
    }

    public static void registerMessages() {
        Invaders.LOGGER.info("Registering Invaders networkings");

        ChannelInstance = NetworkRegistry.newSimpleChannel(new ResourceLocation(Invaders.MODID, "networking"),
                () -> "1.0",
                s -> true,
                s -> true);
        ChannelInstance.messageBuilder(UpdateClientTEPacket.class, nextID())
                .encoder(UpdateClientTEPacket::encode)
                .decoder(UpdateClientTEPacket::new)
                .consumer(UpdateClientTEPacket::handle)
                .add();
        ChannelInstance.messageBuilder(UpdateClientPlayerPacket.class, nextID())
                .encoder(UpdateClientPlayerPacket::encode)
                .decoder(UpdateClientPlayerPacket::new)
                .consumer(UpdateClientPlayerPacket::handle)
                .add();
        ChannelInstance.messageBuilder(UpdateClientWorldDataPacket.class, nextID())
                .encoder(UpdateClientWorldDataPacket::encode)
                .decoder(UpdateClientWorldDataPacket::new)
                .consumer(UpdateClientWorldDataPacket::handle)
                .add();
    }

    public static SimpleChannel getChannelInstance() {
        return ChannelInstance;
    }

    public static void sendToClient(Object packet, ServerPlayerEntity player) {
        ChannelInstance.sendTo(packet, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToServer(Object packet) {
        ChannelInstance.sendToServer(packet);
    }
}
