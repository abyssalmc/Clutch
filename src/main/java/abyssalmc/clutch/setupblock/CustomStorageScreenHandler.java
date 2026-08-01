package abyssalmc.clutch.setupblock;

import abyssalmc.clutch.Clutch;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomStorageScreenHandler extends ScreenHandler {

    private final Inventory inventory;

    public CustomStorageScreenHandler(int syncId, PlayerInventory playerInventory, BlockPos pos) {
        this(syncId, playerInventory, new SimpleInventory(51));
    }

    public CustomStorageScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(Clutch.CUSTOM_SCREEN_HANDLER, syncId);
        checkSize(inventory, 51);
        this.inventory = inventory;
        inventory.onOpen(playerInventory.player);

        int index = 0;

        // armor row
        List<Identifier> textures = new ArrayList<>(Arrays.asList(PlayerScreenHandler.EMPTY_OFFHAND_ARMOR_SLOT, PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE));
        for (int x = 0; x < 5; x++) {
            Identifier slotTexture = textures.get(x);
            this.addSlot(new Slot(inventory, index++, 8 + x * 18, 18) {
                @Override
                public @Nullable Pair<Identifier, Identifier> getBackgroundSprite() {
                    return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, slotTexture);
                }
            });
        }

        // setup inv
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(inventory, index++, 8 + x * 18, 50 + y * 18));
            }
        }

        // setup hotbar
        for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(inventory, index++, 8 + x * 18, 108));
        }

        // player inv
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 9; x++) {
                this.addSlot(new Slot(playerInventory, x + y * 9 + 9, 8 + x * 18, 140 + y * 18));
            }
        }

        // player hotbar
        for (int x = 0; x < 9; x++) {
            this.addSlot(new Slot(playerInventory, x, 8 + x * 18, 198));
        }
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack()) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();

            if (invSlot < 51) {
                if (!this.insertItem(originalStack, 51, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, 51, false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }
        return newStack;
    }
}