package abyssalmc.clutch.mixin;

import abyssalmc.clutch.GlobalDataHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.MouseInput;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static abyssalmc.clutch.Clutch.clickedThisTick;
import static abyssalmc.clutch.Clutch.queueNextClick;

@Mixin(Mouse.class)
public abstract class InputBuffering {

    @Inject(method = "onMouseButton", at = @At("HEAD"), cancellable = true)
    private void limitClicks(long window, MouseInput input, int action, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen != null || action != GLFW.GLFW_PRESS || !GlobalDataHandler.getInputBuffering()) return;

        // Check if it is the use key
        if (client.options.useKey.matchesMouse(new Click(client.mouse.getX(), client.mouse.getY(), input))) {
            if (!clickedThisTick) {
                clickedThisTick = true;
            } else {
                queueNextClick = true;
                ci.cancel();
            }
        }
    }
}