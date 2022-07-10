package wintersteve25.invaders.client.entities;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import wintersteve25.invaders.contents.entities.HubEntity;

public class HubEntityRenderer extends EntityRenderer<HubEntity> {
    public HubEntityRenderer(EntityRendererManager p_i46179_1_) {
        super(p_i46179_1_);
    }

    @Override
    public ResourceLocation getTextureLocation(HubEntity pEntity) {
        return null;
    }
}
