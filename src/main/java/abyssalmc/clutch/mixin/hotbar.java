package abyssalmc.clutch.mixin;

import abyssalmc.clutch.Clutch;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)

public class hotbar {

    @Inject(method = "renderHotbar", at = @At(value = "HEAD"), cancellable = true)
    private void modifyHotbarTexture(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        Identifier indicator = Identifier.of(Clutch.MOD_ID, "textures/indicator.png");
        Identifier itas = Identifier.of(Clutch.MOD_ID, "textures/itas.png");

        RenderSystem.enableBlend();

        PlayerEntity p = MinecraftClient.getInstance().player;

        if (p != null) {
            if (Clutch.isTas) {
                context.drawTexture(itas, context.getScaledWindowWidth() / 2 - 72 - 1 + p.getInventory().selectedSlot * 20, context.getScaledWindowHeight() - 2 - 1, 0, 0, 1, 1, 1, 1);
                context.drawTexture(indicator, context.getScaledWindowWidth() / 2 - 13, context.getScaledWindowHeight() - 4, 0, 0, 1, 1, 1, 1);
            }
        }
        RenderSystem.disableBlend();
    }
}