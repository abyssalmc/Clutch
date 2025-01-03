package abyssalmc.clutch.mixin;

import abyssalmc.clutch.Clutch;
import abyssalmc.clutch.CraftItemPayload;
import com.mojang.brigadier.Command;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CraftingScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CraftRequestC2SPacket;
import net.minecraft.network.packet.c2s.play.CreativeInventoryActionC2SPacket;
import net.minecraft.network.packet.s2c.play.UpdateSelectedSlotS2CPacket;
import net.minecraft.recipe.*;
import net.minecraft.registry.Registry;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


import java.util.Optional;

import static abyssalmc.clutch.Clutch.*;

@Mixin(Keyboard.class)
public class KeyBindingLogger {
    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void captureKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && client.currentScreen != null && (client.currentScreen instanceof CraftingScreen)){
            if (action == GLFW.GLFW_PRESS || action == GLFW.GLFW_REPEAT) {
                et = System.currentTimeMillis();

                double xdim = client.getWindow().getScaledWidth(), ydim = client.getWindow().getScaledHeight();
                double xscale = xdim/1920, yscale = ydim/1080;
                int mouseX = (int) (xscale * client.mouse.getX()), mouseY = (int) (yscale * client.mouse.getY());
                ocxcoords.add(mouseX);
                ocycoords.add(mouseY);

                for (Slot slot : ((HandledScreen) client.currentScreen).getScreenHandler().slots) {
                    int slotX = guix + slot.x; // Absolute X position of the slot
                    int slotY = guiy + slot.y; // Absolute Y position of the slot

                    // Check if mouse is within the slot's bounds
                    if (mouseX >= slotX - 1 && mouseX <= slotX + 16 && mouseY >= slotY - 1 && mouseY <= slotY + 16) {
                        client.player.sendMessage(Text.literal("slot: "+ slot.getIndex()));

                        int ishotkey = 0;
                        for (KeyBinding hotkey : client.options.hotbarKeys){
                            if (InputUtil.fromTranslationKey(hotkey.getBoundKeyTranslationKey()).getCode() == key){
                                break;
                            }
                            ishotkey++;
                        }
                        if (ishotkey < 9){
                            client.player.sendMessage(Text.literal("registered hotkey " + ishotkey));

                            if (client.currentScreen instanceof CraftingScreen){

                                if ((mouseX < 240 && mouseY < 130 /*grid*/) || (mouseY >= 130 && mouseY <= 190 /*inv*/)) {
                                    ItemStack swapStack = client.player.getInventory().getStack(ishotkey);

                                    ItemStack overStack = slot.getStack();


                                    client.player.currentScreenHandler.onSlotClick(slot.getIndex()+1,ishotkey, SlotActionType.SWAP, client.player);

                                    Int2ObjectMap<ItemStack> modifiedStacks = new Int2ObjectOpenHashMap<>();
                                    modifiedStacks.put(slot.getIndex()+1,swapStack);
                                    modifiedStacks.put(ishotkey,overStack);

                                    client.player.networkHandler.sendPacket(new ClickSlotC2SPacket(
                                            client.player.currentScreenHandler.syncId,
                                            client.player.currentScreenHandler.getRevision(),
                                            slot.getIndex()+1,
                                            ishotkey,
                                            SlotActionType.SWAP,
                                            swapStack,
                                            modifiedStacks
                                    ));

                                    ci.cancel();
                                } else if (mouseX >= 240 && mouseY < 130 /*output*/) {
                                    //client.player.sendMessage(Text.literal("" + client.player.getInventory().getStack(ishotkey).getName().getString()));
                                    if (client.player.getInventory().getStack(ishotkey).getName().getString().equals("Air")){
                                        hotbarcraft = ishotkey;
                                        ClientPlayNetworking.send(new CraftItemPayload(new BlockPos(0,0,0)));
                                        ci.cancel();
                                    }
                                }

                            }

                        }



                        // left of x 240, y less than 190, y less than 130
                    }
                }
            }
        }
    }

    private void sendInventoryUpdatePacket() {
        // Send a packet to the server to update the inventory
        PacketByteBuf buf = PacketByteBufs.create();
        ClientPlayNetworking.send(new CustomPayload() {
            @Override
            public Id<? extends CustomPayload> getId() {
                return null;
            }
        });
    }
}