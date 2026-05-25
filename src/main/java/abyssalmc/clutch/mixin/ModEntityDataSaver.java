package abyssalmc.clutch.mixin;

import abyssalmc.clutch.IEntityDataSaver;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class ModEntityDataSaver implements IEntityDataSaver {
    private NbtCompound persistentData;

    @Override
    public NbtCompound getPersistentData() {
        if (this.persistentData == null) {
            this.persistentData = new NbtCompound();
        }
        return persistentData;
    }

    @Inject(at = @At("HEAD"), method = "writeData")
    protected void writeMethod(WriteView view, CallbackInfo info) {
        if (persistentData != null){
            view.put("clutch.platform_data", NbtCompound.CODEC, persistentData);
        }
    }

    @Inject(at = @At("HEAD"), method = "readData")
    protected void readMethod(ReadView view, CallbackInfo ci) {
        persistentData = view.read("clutch.platform_data", NbtCompound.CODEC)
                .orElseGet(NbtCompound::new);
    }
}