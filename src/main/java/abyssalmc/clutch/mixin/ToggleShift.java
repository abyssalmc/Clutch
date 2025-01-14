package abyssalmc.clutch.mixin;

import abyssalmc.clutch.GlobalDataHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static abyssalmc.clutch.Clutch.toggleshiftstate;

@Mixin(InputUtil.class)
public abstract class ToggleShift {
    @Inject(method = "isKeyPressed(JI)Z", at = @At(value = "HEAD"), cancellable = true)
    private static void toggleShiftGUI(long handle, int code, CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftClient.getInstance().player != null){
            if (code == GLFW.GLFW_KEY_LEFT_SHIFT || code == GLFW.GLFW_KEY_RIGHT_SHIFT){
                if (GlobalDataHandler.getToggleShift()){
                    if (toggleshiftstate){
                        cir.setReturnValue(true);
                    } else {
                        cir.setReturnValue(false);
                    }
                }
            }
        }

    }
}
