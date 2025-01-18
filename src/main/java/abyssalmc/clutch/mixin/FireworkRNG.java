package abyssalmc.clutch.mixin;

import abyssalmc.clutch.StateSaverAndLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.projectile.ProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static abyssalmc.clutch.Clutch.recursion;

@Mixin(Entity.class)
public abstract class FireworkRNG {
    @Inject(method = "setVelocity(DDD)V", at = @At("HEAD"), cancellable = true)
    private void removeRNG(double x, double y, double z, CallbackInfo ci) {
        if (MinecraftClient.getInstance().isIntegratedServerRunning() && MinecraftClient.getInstance().getServer() != null) {
            StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(MinecraftClient.getInstance().getServer());
            if (!serverState.projectilerng){
                Entity entity = (Entity) (Object) this;
                if (entity.getType() == EntityType.FIREWORK_ROCKET){
                    recursion = !recursion;
                    if (recursion){
                        entity.setVelocity(0,y,0);
                        ci.cancel();
                    }
                }
            }
        }

    }
}