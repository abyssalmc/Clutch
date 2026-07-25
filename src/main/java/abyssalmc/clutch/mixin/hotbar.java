package abyssalmc.clutch.mixin;

import abyssalmc.clutch.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static abyssalmc.clutch.Clutch.toggleshiftstate;

@Mixin(InGameHud.class)

public class hotbar {


    @Inject(method = "renderHotbar", at = @At(value = "HEAD"), cancellable = true)
    private void modifyHotbarTexture(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        Identifier indicator = Identifier.of(Clutch.MOD_ID, "textures/indicator.png");
        Identifier itas = Identifier.of(Clutch.MOD_ID, "textures/itas.png");

        RenderSystem.enableBlend();
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity p = client.player;


        // TPS INDICATOR
        /*
        if (p != null) {
            if (Clutch.isTPS) {
                int slotx = context.getScaledWindowWidth() / 2 - 90 + p.getInventory().selectedSlot * 20;
                int sloty = context.getScaledWindowHeight() - 2;
                context.fill(slotx, sloty, slotx+1, sloty+1, 0xFFb7c7b3);
            }
        }*/
        RenderSystem.disableBlend();
    }
}