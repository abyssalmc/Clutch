package abyssalmc.clutch.mixin;

import abyssalmc.clutch.StateSaverAndLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.CrossbowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static abyssalmc.clutch.Clutch.recursion;

@Mixin(ProjectileEntity.class)
public abstract class ProjectileRNG {
    @Inject(method = "setVelocity(Lnet/minecraft/entity/Entity;FFFFF)V", at = @At("HEAD"), cancellable = true)
    private void removeRNG(Entity shooter, float pitch, float yaw, float roll, float speed, float divergence, CallbackInfo ci) {
        if (MinecraftClient.getInstance().isIntegratedServerRunning() && MinecraftClient.getInstance().getServer() != null) {
            StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(MinecraftClient.getInstance().getServer());
            if (!serverState.projectilerng) {
                recursion = !recursion;
                if (recursion){
                    ProjectileEntity projectile = (ProjectileEntity) (Object) this;
                    projectile.setVelocity(shooter, pitch, yaw, roll, speed, 0);

                    ci.cancel();
                }
            }
        }
    }


    @Inject(method = "setVelocity(DDDFF)V", at = @At("HEAD"), cancellable = true)
    private void removeRNG(double x, double y, double z, float power, float uncertainty, CallbackInfo ci) {
        if (MinecraftClient.getInstance().isIntegratedServerRunning() && MinecraftClient.getInstance().getServer() != null) {
            StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(MinecraftClient.getInstance().getServer());
            if (!serverState.projectilerng) {
                recursion = !recursion;
                if (recursion){
                    ProjectileEntity projectile = (ProjectileEntity) (Object) this;
                    projectile.setVelocity(x,y,z,power,0);

                    ci.cancel();
                }
            }
        }
    }
}