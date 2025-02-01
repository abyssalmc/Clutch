package abyssalmc.clutch.mixin;

import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Mouse.class)
public interface ModifyMouseCoords {
    @Accessor("x")
    void setMouseX(double x);

    @Accessor("y")
    void setMouseY(double y);
}