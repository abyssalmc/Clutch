package abyssalmc.clutch.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static abyssalmc.clutch.Clutch.*;

@Mixin(ServerPlayerEntity.class)
public class GUICloseCancel {
    @Inject(method = "closeHandledScreen", at = @At("HEAD"), cancellable = true)
    private void cancelClose(CallbackInfo ci) {
        if (guitime != 0 && MinecraftClient.getInstance().isIntegratedServerRunning() && MinecraftClient.getInstance().getServer() != null) {
            if (MinecraftClient.getInstance().currentScreen instanceof CraftingScreen) {
                if (!closepass) {
                    if (timeextension){
                        ci.cancel();
                        ((ServerPlayerEntity) (Object) this).currentScreenHandler.syncState();
                    }
                } else {
                    closepass = false;
                    timeextension = false;
                }
            }
        }
    }
}
