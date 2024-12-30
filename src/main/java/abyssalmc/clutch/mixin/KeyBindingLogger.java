package abyssalmc.clutch.mixin;

import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static abyssalmc.clutch.Clutch.*;

@Mixin(Keyboard.class)
public class KeyBindingLogger {
    @Inject(method = "onKey", at = @At("HEAD"))
    private void captureKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && client.currentScreen != null){
            et = System.currentTimeMillis();
            double xdim = client.getWindow().getScaledWidth();
            double ydim = client.getWindow().getScaledHeight();
            double xscale = xdim/1920;
            double yscale = ydim/1080;
            int mouseX = (int) (xscale * client.mouse.getX());
            int mouseY = (int) (yscale * client.mouse.getY());
            ocxcoords.add(mouseX);
            ocycoords.add(mouseY);
        }
    }
}