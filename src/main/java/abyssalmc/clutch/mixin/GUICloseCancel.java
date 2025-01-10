package abyssalmc.clutch.mixin;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static abyssalmc.clutch.Clutch.closepass;
import static abyssalmc.clutch.Clutch.guitime;

@Mixin(ServerPlayerEntity.class)
public class GUICloseCancel {
    @Inject(method = "closeHandledScreen", at = @At("HEAD"), cancellable = true)
    private void cancelClose(CallbackInfo ci){
        if (guitime != 0 && MinecraftClient.getInstance().isIntegratedServerRunning() && MinecraftClient.getInstance().getServer() != null){
            if (MinecraftClient.getInstance().currentScreen instanceof CraftingScreen){
                if (!closepass){
                    ci.cancel();
                } else {
                    closepass = false;
                }

            }
        }
    }

}
