package abyssalmc.clutch.mixin;

import abyssalmc.clutch.GlobalDataHandler;
import abyssalmc.clutch.LineUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

import static abyssalmc.clutch.Clutch.*;

@Mixin(HandledScreen.class)
public abstract class MouseCursorLocation{
    @Shadow
    protected int x;
    @Shadow
    protected int y;

    @Shadow
    @Nullable
    private Slot touchDragSlotStart;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player != null && client.currentScreen != null) {


            // click between frames


            if (touchDragSlotStart != null) {
                client.player.sendMessage(Text.literal(touchDragSlotStart.getIndex() + ""));

                List<Integer> missedslots = new ArrayList<>();
                List<int[]> line = LineUtils.getLine(mouseX, mouseY, lastmousex, lastmousey);
                int i = 0;
                for (int[] coord : line) {
                    context.drawText(client.textRenderer, "·", coord[0], coord[1]-3,  0xFF0000, false);
                    i++;

                    for (Slot slot : ((HandledScreen) client.currentScreen).getScreenHandler().slots) {
                        int slotX = x + slot.x; // Absolute X position of the slot
                        int slotY = y + slot.y; // Absolute Y position of the slot

                        // Check if mouse is within the slot's bounds
                        if (coord[0] >= slotX - 1 && coord[0] <= slotX + 16 && coord[1] >= slotY - 1 && coord[1] <= slotY + 16) {
                            // do stuff if over slot
                            if (!missedslots.contains(slot.getIndex())){
                                missedslots.add(slot.getIndex());
                            }
                        }
                    }
                }
                missedslots = missedslots.reversed();

                String ms = "Missed slots: ";
                for (int ii : missedslots){
                    ms = ms + ii + ", ";

                    ItemStack cursorStack = ((HandledScreen) client.currentScreen).getScreenHandler().getCursorStack();
                    ItemStack distributedStack = new ItemStack(cursorStack.getItem(), 1);
                    ((HandledScreen) client.currentScreen).getScreenHandler().slots.get(ii).setStack(distributedStack);



                }
                //client.player.sendMessage(Text.literal(ms));
            }

            lastmousex = mouseX;
            lastmousey = mouseY;


            // drawing input locators
            if (GlobalDataHandler.getInputLocation() > 0){
                int index = 0;
                for (int x : cxcoords){
                    RenderSystem.disableDepthTest();
                    context.drawText(client.textRenderer, "·", x, cycoords.get(index)-3,  0xFF0000, false);
                    RenderSystem.enableDepthTest();
                    index++;

                }

                int overslot = 0;
                for (Slot slot : ((HandledScreen) client.currentScreen).getScreenHandler().slots) {
                    int slotX = x + slot.x; // Absolute X position of the slot
                    int slotY = y + slot.y; // Absolute Y position of the slot

                    // Check if mouse is within the slot's bounds
                    if (mouseX >= slotX - 1 && mouseX <= slotX + 16 && mouseY >= slotY - 1 && mouseY <= slotY + 16) {
                        // do stuff if over slot
                        overslot++;
                    }
                }

                if (overslot == 0 || GlobalDataHandler.getInputLocation() == 2){
                    if (keypressed || mousepressed) {
                        keypressed = false;
                        mousepressed = false;
                        cxcoords.add((int) Math.floor(mouseX));
                        cycoords.add((int) Math.floor(mouseY));
                    }
                } else { keypressed = false; mousepressed = false; }
            }

        }
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onKeyPressed(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && client.currentScreen != null) {
            if (GlobalDataHandler.getInputLocation() > 0){
                mousepressed = true;
            }

        }
    }
}