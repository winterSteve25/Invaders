package wintersteve25.invaders.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MobEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wintersteve25.invaders.init.InvadersCapabilities;

@Mixin(MobEntity.class)
public abstract class MobEntityMixin extends Entity {

    public MobEntityMixin(EntityType<?> pType, World pLevel) {
        super(pType, pLevel);
    }

    @Inject(at = @At("HEAD"), method = "isSunBurnTick()Z", cancellable = true)
    private void isSunBurnTick(CallbackInfoReturnable<Boolean> callback) {
        this.getCapability(InvadersCapabilities.INVASION_MOBS).ifPresent(cap -> {
            if (cap.isInvasionMob()) {
                callback.setReturnValue(false);
            }
        });
    }
}
