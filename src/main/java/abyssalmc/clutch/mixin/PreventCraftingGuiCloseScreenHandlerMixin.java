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

import static abyssalmc.clutch.Clutch.guitime;
import static abyssalmc.clutch.Clutch.timeextension;

@Mixin(CraftingScreenHandler.class)
public abstract class PreventCraftingGuiCloseScreenHandlerMixin extends ScreenHandler {

    protected PreventCraftingGuiCloseScreenHandlerMixin(ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }
    @Inject(method = "canUse", at = @At("HEAD"), cancellable = true)
    private void alwaysAllowUse(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (guitime != 0 && MinecraftClient.getInstance().isIntegratedServerRunning() && MinecraftClient.getInstance().getServer() != null){
            if (timeextension){
                cir.setReturnValue(true);
            }
        } else {
            timeextension = false;
        }
    }
}