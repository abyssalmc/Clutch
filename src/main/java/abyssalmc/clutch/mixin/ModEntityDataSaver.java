package abyssalmc.clutch.mixin;

import abyssalmc.clutch.IEntityDataSaver;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
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

    @Inject(at = @At("HEAD"), method = "writeNbt")
    protected void writeMethod(NbtCompound nbt, CallbackInfoReturnable info) {
        if (persistentData != null){
            nbt.put("clutch.platform_data", persistentData);
        }
    }

    @Inject(at = @At("HEAD"), method = "readNbt")
    protected void readMethod(NbtCompound nbt, CallbackInfo info) {
        if (nbt.contains("clutch.platform_data",10)){
            persistentData = nbt.getCompound("clutch.platform_data");
        }
    }


}