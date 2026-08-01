package abyssalmc.clutch.mixin;

import abyssalmc.clutch.GlobalDataHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MiningToolItem;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class Instamine {
    @Inject(method = "getBlockBreakingSpeed", at = @At("RETURN"), cancellable = true)
    private void Instamine(BlockState state, CallbackInfoReturnable<Float> cir) {
        // singleplayer check
        if (!MinecraftClient.getInstance().isIntegratedServerRunning() || MinecraftClient.getInstance().getServer() == null) return;

        PlayerEntity self = (PlayerEntity)(Object)this;

        if (GlobalDataHandler.getInstamine()) {
            ItemStack held = self.getMainHandStack();

            float speed = held.getMiningSpeedMultiplier(state); // > 1 if right tool type

            if (speed > 1f) {
                if (state.isOf(Blocks.DAYLIGHT_DETECTOR)
                        || state.isOf(Blocks.MOSS_BLOCK)
                        || state.isOf(Blocks.SNOW_BLOCK)
                        || state.isOf(Blocks.SCULK)
                        || state.isIn(BlockTags.LEAVES)
                        || state.isOf(Blocks.BROWN_MUSHROOM_BLOCK)
                        || state.isOf(Blocks.RED_MUSHROOM_BLOCK)
                        || state.isOf(Blocks.MUSHROOM_STEM)) {

                    cir.setReturnValue(1000000f);
                }
            }
        }
    }
}