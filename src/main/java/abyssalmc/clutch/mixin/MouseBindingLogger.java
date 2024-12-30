package abyssalmc.clutch.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static abyssalmc.clutch.Clutch.et;

@Mixin(Mouse.class)
public class MouseBindingLogger {
    @Inject(method = "onMouseButton", at = @At("HEAD"))
    private void captureKey(long window, int button, int action, int mods, CallbackInfo ci) {
        if (MinecraftClient.getInstance().currentScreen != null){
            et = System.currentTimeMillis();
        }
    }
}