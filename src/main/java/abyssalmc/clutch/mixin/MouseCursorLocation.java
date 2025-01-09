package abyssalmc.clutch.mixin;

import abyssalmc.clutch.*;
import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static abyssalmc.clutch.Clutch.*;

@Mixin(HandledScreen.class)
public abstract class MouseCursorLocation{
    @Shadow
    protected int x;
    @Shadow
    protected int y;

    @Shadow
    private Slot touchDragSlotStart;


    @Shadow
    @Final
    protected Set<Slot> cursorDragSlots;

    private boolean isDragging = false;



    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null && (client.currentScreen instanceof CraftingScreen || client.currentScreen instanceof InventoryScreen)) {
            guix = x;
            guiy = y;

            int index = 0;
            for (int x : cxcoords){
                //context.drawText(client.inGameHud.getTextRenderer(), "◦", x-1, cycoords.get(index)-3, 0xFF0000, true);
                //context.drawText(client.inGameHud.getTextRenderer(), "⋅", x, cycoords.get(index)-4, 0xFF0000, true);
                context.drawText(client.inGameHud.getTextRenderer(), "₊", x-1, cycoords.get(index)-5, 0xFF0000, true);


                index++;
            }
        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && client.currentScreen != null) {
            if (GlobalDataHandler.getInputLocation() > 0){
                int overslot = 0;
                for (Slot slot : ((HandledScreen<?>) client.currentScreen).getScreenHandler().slots) {
                    int slotX = guix + slot.x;
                    int slotY = guiy + slot.y;

                    if (mouseX >= slotX - 1 && mouseX <= slotX + 17 && mouseY >= slotY - 1 && mouseY <= slotY + 16) {
                        overslot++;
                    }
                }

                if (overslot == 0 || GlobalDataHandler.getInputLocation() == 2){
                    cxcoords.add((int)Math.floor(mouseX));
                    cycoords.add((int)Math.floor(mouseY));
                }
            }
        }
    }
}
