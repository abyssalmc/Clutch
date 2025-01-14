package abyssalmc.clutch.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static abyssalmc.clutch.Clutch.*;

@Mixin(MinecraftClient.class)
public class CursorOffset {
    @Inject(method = "setScreen", at = @At("HEAD"))
    private void onSetScreen(Screen screen, CallbackInfo ci) {
        if (screen instanceof CraftingScreen) {
            if (offsetEnabled) {
                unlockAndSetCursor(cursorx, cursory);
                offsetEnabled = false;
            }
        }
    }

    private void unlockAndSetCursor(double x, double y) {
        MinecraftClient client = MinecraftClient.getInstance();
        Mouse mouse = client.mouse;
        mouse.unlockCursor();

        long windowHandle = client.getWindow().getHandle();
        InputUtil.setCursorParameters(windowHandle, GLFW.GLFW_CURSOR_NORMAL, x, y);
    }
}
