package abyssalmc.clutch.mixin;

import abyssalmc.clutch.*;
import abyssalmc.clutch.sound.ModSounds;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.slot.Slot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

import static abyssalmc.clutch.Clutch.*;

@Mixin(HandledScreen.class)
public abstract class MouseCursorLocation {
    @Shadow
    protected int x;
    @Shadow
    protected int y;

    @Shadow
    @Final
    protected Set<Slot> cursorDragSlots;

    private int dragSlots = 0;
    private int releaseDragSlots = 0;
    private boolean lastFirstFrame = false;


    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (GlobalDataHandler.getInputLocation() > 0) {
            if (client.player != null && (client.currentScreen instanceof CraftingScreen || client.currentScreen instanceof InventoryScreen) && (MinecraftClient.getInstance().isIntegratedServerRunning() && MinecraftClient.getInstance().getServer() != null)) {
                guix = x;
                guiy = y;

                int index = 0;
                for (int x : cxcoords) {
                    switch (GlobalDataHandler.getInputLocator()) {
                        case 0:
                            context.drawText(client.inGameHud.getTextRenderer(), "⋅", x, cycoords.get(index) - 4, 0xFF0000, true);
                            break;
                        case 1:
                            context.drawText(client.inGameHud.getTextRenderer(), "◦", x - 1, cycoords.get(index) - 3, 0xFF0000, true);
                            break;
                        case 2:
                            context.drawText(client.inGameHud.getTextRenderer(), "₊", x - 1, cycoords.get(index) - 5, 0xFF0000, true);
                            break;
                    }
                    index++;
                }
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();

        switch (GlobalDataHandler.getCustomSounds()) {
            case 0:
                break;
            case 1:
                client.player.playSoundToPlayer(ModSounds.OSU, SoundCategory.MASTER, 999, 1);
                break;
            case 2:
                client.player.playSoundToPlayer(ModSounds.BASSKICK, SoundCategory.MASTER, 999, 1);
                break;
        }

        if (GlobalDataHandler.getInputLocation() > 0) {
            if (client.player != null && client.currentScreen != null) {
                dragSlots = cursorDragSlots.size();

                int overslot = 0;
                for (Slot slot : ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots) {
                    int slotX = guix + slot.x;
                    int slotY = guiy + slot.y;

                    if (mouseX >= slotX - 1 && mouseX <= slotX + 17 && mouseY >= slotY - 1 && mouseY <= slotY + 17) {
                        overslot++;
                    }
                }

                if (overslot == 0 || GlobalDataHandler.getInputLocation() == 2) {
                    if (client.player.currentScreenHandler.getCursorStack().isEmpty()) {
                        cxcoords.add((int) Math.floor(mouseX));
                        cycoords.add((int) Math.floor(mouseY));
                    }
                    slotclick = false;
                } else {
                    slotclick = true;
                }

            }
        }
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void onKeyReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (GlobalDataHandler.getInputLocation() > 0) {
            if (client.player != null && client.currentScreen != null) {
                releaseDragSlots = cursorDragSlots.size();

                if (GlobalDataHandler.getInputLocation() > 0) {
                    int overslot = 0;
                    for (Slot slot : ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots) {
                        int slotX = guix + slot.x;
                        int slotY = guiy + slot.y;

                        if (mouseX >= slotX - 1 && mouseX <= slotX + 17 && mouseY >= slotY - 1 && mouseY <= slotY + 17) {
                            overslot++;
                        }
                    }

                    if (overslot == 0 || GlobalDataHandler.getInputLocation() == 2) {
                        if (!client.player.currentScreenHandler.getCursorStack().isEmpty()) {
                            if (dragSlots == releaseDragSlots){
                                if (!slotclick){
                                    cxcoords.add((int) Math.floor(mouseX));
                                    cycoords.add((int) Math.floor(mouseY));
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Inject(method = "close", at = @At("HEAD"), cancellable = true)
    private void closeScreen(CallbackInfo ci) {
        if (MinecraftClient.getInstance().currentScreen instanceof CraftingScreen || MinecraftClient.getInstance().currentScreen instanceof InventoryScreen) {
        }
    }
}
