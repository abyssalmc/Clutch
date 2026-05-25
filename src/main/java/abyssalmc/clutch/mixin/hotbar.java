package abyssalmc.clutch.mixin;

import abyssalmc.clutch.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(InGameHud.class)

public class hotbar {


    @Inject(method = "renderHotbar", at = @At(value = "TAIL"), cancellable = true)
    private void modifyHotbarTexture(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {

        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity p = client.player;

        if (p != null) {
            if (Clutch.isTas) {
                int slotx = context.getScaledWindowWidth() / 2 - 90 + p.getInventory().getSelectedSlot() * 20;
                int sloty = context.getScaledWindowHeight() - 2;
                context.fill(slotx, sloty, slotx+1, sloty+1, 0xFFafc0ab);
            }
        }
    }
}