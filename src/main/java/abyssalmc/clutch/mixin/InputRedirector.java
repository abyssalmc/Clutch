package abyssalmc.clutch.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


import static abyssalmc.clutch.Clutch.*;

@Mixin(HandledScreen.class)
public class InputRedirector {
    private static boolean recursion = false;
    private static long time = 0;

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onClick(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {

        recursion = (time == System.currentTimeMillis()) ? true : false;
        time = System.currentTimeMillis();

        if (!recursion){

            ocxcoords.add((int) mouseX);
            ocycoords.add((int) mouseY);

            MinecraftClient.getInstance().player.sendMessage(Text.literal("x: " + mouseX + ", y: " + mouseY));


            System.out.println("(" + mouseX + "," + mouseY + ")");
            cir.setReturnValue(MinecraftClient.getInstance().currentScreen.mouseClicked(mouseX,mouseY,button));
            cir.cancel();
        }


    }
}
