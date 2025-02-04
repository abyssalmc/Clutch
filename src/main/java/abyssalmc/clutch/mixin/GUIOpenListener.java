package abyssalmc.clutch.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.InputUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

import static abyssalmc.clutch.Clutch.*;

@Mixin(MinecraftClient.class)
public class GUIOpenListener {
    @Inject(method = "setScreen", at = @At("HEAD"))
    private void onSetScreen(Screen screen, CallbackInfo ci) {
        if (screen instanceof CraftingScreen || screen instanceof InventoryScreen) {
            tempguitime = guitime;
            cxcoords = new ArrayList<>();
            cycoords = new ArrayList<>();

            //MinecraftClient.getInstance().mouse.lockCursor();
            //InputUtil.setCursorParameters(MinecraftClient.getInstance().getWindow().getHandle(), 212993, cursorx, cursory);
            //MinecraftClient.getInstance().mouse.unlockCursor();
        }
    }
}
