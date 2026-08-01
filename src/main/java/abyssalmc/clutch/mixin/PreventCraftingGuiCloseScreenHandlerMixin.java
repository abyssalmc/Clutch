package abyssalmc.clutch.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static abyssalmc.clutch.ClutchClient.guitime;
import static abyssalmc.clutch.ClutchClient.timeextension;

@Mixin(CraftingScreenHandler.class)
public abstract class PreventCraftingGuiCloseScreenHandlerMixin extends ScreenHandler {

    protected PreventCraftingGuiCloseScreenHandlerMixin(ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }
    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void alwaysAllowUse(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        // singleplayer check
        if (!MinecraftClient.getInstance().isIntegratedServerRunning() || MinecraftClient.getInstance().getServer() == null) return;

        if (guitime != 0){
            if (timeextension){
                cir.setReturnValue(true);
            }
        } else {
            timeextension = false;
        }
    }
}