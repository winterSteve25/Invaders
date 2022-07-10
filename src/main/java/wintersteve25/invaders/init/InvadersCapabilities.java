package wintersteve25.invaders.init;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import wintersteve25.invaders.Invaders;
import wintersteve25.invaders.data.capabilities.InvasionMobData;
import wintersteve25.invaders.data.capabilities.ModPlayerData;
import wintersteve25.invaders.data.capabilities.base.CapabilityStorage;

public class InvadersCapabilities {
    @CapabilityInject(ModPlayerData.class)
    public static Capability<ModPlayerData> PLAYER_DATA;

    @CapabilityInject(InvasionMobData.class)
    public static Capability<InvasionMobData> INVASION_MOBS;
    
    public static void register() {
        Invaders.LOGGER.info("Registering capabilities");
        CapabilityManager.INSTANCE.register(ModPlayerData.class, new CapabilityStorage<>(), ModPlayerData::new);
        CapabilityManager.INSTANCE.register(InvasionMobData.class, new CapabilityStorage<>(), InvasionMobData::new);
    }
}
