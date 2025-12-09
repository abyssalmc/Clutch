package abyssalmc.clutch.mixin;

import abyssalmc.clutch.StateSaverAndLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static abyssalmc.clutch.Clutch.*;

@Mixin(Mouse.class)
public class CursorOffset {

    @Shadow
    private double x;

    @Shadow
    private double y;

    @Shadow
    private boolean cursorLocked;

    private boolean locked = false;


    @Inject(method = "unlockCursor", at = @At("HEAD"), cancellable = true)
    private void checkLocked(CallbackInfo ci) {
        locked = this.cursorLocked;
    }
    @Inject(method = "lockCursor", at = @At("HEAD"), cancellable = true)
    private void checkLocked2(CallbackInfo ci) {
        locked = this.cursorLocked;
    }

    @Inject(method = "unlockCursor", at = @At("TAIL"), cancellable = true)
    private void unlockPos(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.currentScreen instanceof CraftingScreen || client.currentScreen instanceof InventoryScreen){
            if (offsetEnabled){
                this.x = cursorx;
                this.y = cursory;
                InputUtil.setCursorParameters(client.getWindow().getHandle(), 212993, this.x, this.y);
                client.execute(() -> InputUtil.setCursorParameters(client.getWindow().getHandle(), 212993, this.x, this.y));
                offsetEnabled = false;
                ci.cancel();
            }
        } else {
            if (offsetEnabled){
                offsetEnabled = false;
                ci.cancel();
            }
        }
    }

    @Inject(method = "lockCursor", at = @At("TAIL"), cancellable = true)
    private void lockPos(CallbackInfo ci) {
        if (offsetEnabled){
            MinecraftClient client = MinecraftClient.getInstance();
            this.x = cursorx;
            this.y = cursory;
            InputUtil.setCursorParameters(client.getWindow().getHandle(), 212995, this.x, this.y);
            client.execute(() -> InputUtil.setCursorParameters(client.getWindow().getHandle(), 212993, this.x, this.y));
        }
    }
}
