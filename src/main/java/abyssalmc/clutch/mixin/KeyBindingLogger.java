package abyssalmc.clutch.mixin;

import abyssalmc.clutch.CloseGUIPayload;
import abyssalmc.clutch.GlobalDataHandler;
import abyssalmc.clutch.StateSaverAndLoader;
import abyssalmc.clutch.sound.ModSounds;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static abyssalmc.clutch.Clutch.*;
import static abyssalmc.clutch.event.keyinputhandler.*;

@Mixin(Keyboard.class)
public class KeyBindingLogger {
    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void captureKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        // CLICK INDICATOR AND SOUND
        int ishotkey = 0;
        for (KeyBinding hotkey : client.options.hotbarKeys){
            if (InputUtil.fromTranslationKey(hotkey.getBoundKeyTranslationKey()).getCode() == key){
                ishotkey++;
                break;
            }
        }
        if (InputUtil.fromTranslationKey(client.options.swapHandsKey.getBoundKeyTranslationKey()).getCode() == key){
            ishotkey++;
        }


        if (client.player != null && (client.currentScreen instanceof CraftingScreen || client.currentScreen instanceof InventoryScreen)){
            if ((action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT) && ishotkey != 0) {
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
                        double xscale = xdim/client.getWindow().getWidth(), yscale = ydim/client.getWindow().getHeight();
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
                                cxcoords.add(Math.round(mouseX));
                                cycoords.add(Math.round(mouseY));
                            }
                        }
                    }
                }
            }
        }


        // RESET KEY
        if (MinecraftClient.getInstance().player != null){
            if (key == InputUtil.fromTranslationKey(resetkey.getBoundKeyTranslationKey()).getCode() && action == GLFW.GLFW_PRESS){
                if (client.isIntegratedServerRunning() && client.getServer() != null) {
                    if ((client.currentScreen instanceof HandledScreen<?> || client.currentScreen == null) && !(client.currentScreen instanceof CreativeInventoryScreen)){
                        if (client.currentScreen != null){
                            resetclose = true;
                            ClientPlayNetworking.send(new CloseGUIPayload(new BlockPos(0,0,0)));
                        }

                        automovementcountdown = 11;
                        if (GlobalDataHandler.getAutomov()) {
                            client.options.jumpKey.setPressed(false);
                            client.options.backKey.setPressed(false);
                            client.options.forwardKey.setPressed(false);
                        }

                        StateSaverAndLoader serverState = StateSaverAndLoader.getServerState(client.getServer());
                        if (!serverState.platformcoords.equals("unset")) {
                            client.player.setVelocity(0,0,0);
                            String cmd = "tp @s " + serverState.platformcoords + GlobalDataHandler.getPitch();
                            client.getNetworkHandler().sendChatCommand(cmd);
                        }
                        else {
                            client.player.sendMessage(Text.literal("§cA platform must be set to use this! run /platform to get started."));
                        }
                    }
                }
            }
        }

        // TOGGLE SHIFT

        if ((!(client.currentScreen instanceof HandledScreen<?>) && key == InputUtil.fromTranslationKey(client.options.sneakKey.getBoundKeyTranslationKey()).getCode())
                || ((client.currentScreen instanceof HandledScreen<?> && (key == GLFW.GLFW_KEY_LEFT_SHIFT || key == GLFW.GLFW_KEY_RIGHT_SHIFT)))){
            if (GlobalDataHandler.getToggleShift()){
                if (action == GLFW.GLFW_PRESS){
                    if (GlobalDataHandler.getToggleShift()){
                        if (!client.options.getSneakToggled().getValue()){
                            toggleshiftstate = !toggleshiftstate;
                            ci.cancel();
                        } else {
                            client.player.sendMessage(Text.literal("§cToggle sneak must be disabled to use this feature. If this is a mistake, run /clutch toggleshift disable."));
                        }

                    }
                } else {
                    if (GlobalDataHandler.getToggleShift() && !client.options.getSneakToggled().getValue()){
                        ci.cancel();
                    }
                }
            }
        }
    }
}