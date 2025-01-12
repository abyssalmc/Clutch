package abyssalmc.clutch.mixin;

import abyssalmc.clutch.GlobalDataHandler;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(RecipeBookWidget.class)
public abstract class RecipeDisabler {
    @Inject(method = "isOpen", at = @At(value = "HEAD"), cancellable = true)
    private void preventRender(CallbackInfoReturnable<Boolean> cir) {
        if (GlobalDataHandler.getRecipe() > 0){
            cir.setReturnValue(false);
        }
    }
}