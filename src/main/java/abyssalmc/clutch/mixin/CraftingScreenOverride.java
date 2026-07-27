package abyssalmc.clutch.mixin;

import abyssalmc.clutch.Clutch;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingScreen.class)
public abstract class CraftingScreenOverride extends HandledScreen<CraftingScreenHandler> {
    @Unique
    private static final Identifier SECRET_TEXTURE = Identifier.of(Clutch.MOD_ID, "textures/crafting_table.png");

    protected CraftingScreenOverride(CraftingScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Inject(method = "drawBackground", at = @At("HEAD"), cancellable = true)
    private void drawCustomBackground(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        if (Clutch.isTPS) {
            //context.drawTexture(SECRET_TEXTURE, this.x, this.y, 0, 0, this.backgroundWidth, this.backgroundHeight);
            context.drawTexture(RenderPipelines.GUI_TEXTURED, SECRET_TEXTURE, this.x, this.y, 0, 0, 256, 256, 256, 256);

            ci.cancel();
        }
    }
}