package abyssalmc.clutch.mixin;

import abyssalmc.clutch.GlobalDataHandler;
import abyssalmc.clutch.sound.ModSounds;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static abyssalmc.clutch.Clutch.*;

@Mixin(Keyboard.class)
public class KeyBindingLogger {
    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void captureKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        int ishotkey = 0;
        for (KeyBinding hotkey : client.options.hotbarKeys){
            if (InputUtil.fromTranslationKey(hotkey.getBoundKeyTranslationKey()).getCode() == key){
                ishotkey++;
                break;
            }
        }


        if (client.player != null && (client.currentScreen instanceof CraftingScreen || client.currentScreen instanceof InventoryScreen)){
            if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT && ishotkey > 0) {
                switch (GlobalDataHandler.getCustomSounds()){
                    case 0:
                        break;
                    case 1:
                        client.player.playSoundToPlayer(ModSounds.OSU, SoundCategory.MASTER, 999, 1);
                        break;
                    case 2:
                        client.player.playSoundToPlayer(ModSounds.BASSKICK, SoundCategory.MASTER, 999, 1);
                        break;
                }
            }

            if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT) {

                if (ishotkey != 0){
                    if (GlobalDataHandler.getInputLocation() > 0){
                        double xdim = client.getWindow().getScaledWidth(), ydim = client.getWindow().getScaledHeight();
                        double xscale = xdim/1920, yscale = ydim/1080;
                        int mouseX = (int) (xscale * client.mouse.getX()), mouseY = (int) (yscale * client.mouse.getY());

                        int overslot = 0;
                        for (Slot slot : ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots) {
                            int slotX = guix + slot.x;
                            int slotY = guiy + slot.y;

                            if (mouseX >= slotX - 1 && mouseX <= slotX + 16 && mouseY >= slotY - 1 && mouseY <= slotY + 16) {
                                overslot++;
                            }
                        }

                        if (overslot == 0 || GlobalDataHandler.getInputLocation() == 2){
                            if (ishotkey > 0){
                                cxcoords.add((int)Math.round(mouseX));
                                cycoords.add((int)Math.round(mouseY));
                            }
                        }
                    }
                }
            }
        }
    }
}