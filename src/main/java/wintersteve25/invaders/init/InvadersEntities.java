package wintersteve25.invaders.init;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import wintersteve25.invaders.contents.entities.HubEntity;

public class InvadersEntities {
    public static final RegistryObject<EntityType<HubEntity>> HUB = Registration.ENTITIES.register("hub", () -> EntityType.Builder.of(HubEntity::new, EntityClassification.MISC)
            .setShouldReceiveVelocityUpdates(false)
            .sized(0.5f, 0.5f)
            .build("hub"));
    
    public static void register() {
    }
}
