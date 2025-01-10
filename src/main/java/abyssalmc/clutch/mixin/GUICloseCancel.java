package abyssalmc.clutch.mixin;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static abyssalmc.clutch.Clutch.guitime;

@Mixin(HandledScreen.class)
public class GUICloseCancel {
    @Inject(method = "close", at = @At("HEAD"), cancellable = true)
    private void cancelClose(CallbackInfo ci){
        if (guitime != 0 && MinecraftClient.getInstance().isIntegratedServerRunning() && MinecraftClient.getInstance().getServer() != null){
            ci.cancel();
        }
    }

}
