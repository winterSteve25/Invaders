package wintersteve25.invaders.mixin;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.EntitySenses;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wintersteve25.invaders.init.InvadersCapabilities;

@Mixin(EntitySenses.class)
public class EntitySensesMixin {
    @Shadow @Final private MobEntity mob;
    
    @Inject(at = @At("HEAD"), method = "canSee(Lnet/minecraft/entity/Entity;)Z", cancellable = true)
    private void canSee(CallbackInfoReturnable<Boolean> callback) {
        mob.getCapability(InvadersCapabilities.INVASION_MOBS).ifPresent(cap -> {
            if (cap.isInvasionMob()) {
                callback.setReturnValue(true);
            }
        });
    }
}
