package abyssalmc.clutch.setupblock;

import abyssalmc.clutch.Clutch;
import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// SCREEN HANDLER (slot locations, special slots and actions)

public class SetupBlockScreenHandler extends ScreenHandler {

    private final Inventory inventory;
    private final Inventory deleteInventory = new SimpleInventory(1);

    private BlockPos pos;
    private String slotText = "";
    private String offsetText = "";

    public String getSlotText() { return this.slotText; }
    public String getOffsetText() { return this.offsetText; }

    public static class TrashSlot extends Slot {
        public TrashSlot(Inventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return false;
        }
    }

    public SetupBlockScreenHandler(int syncId, PlayerInventory playerInventory, TextStoragePayload data) {
        this(syncId, playerInventory, new SimpleInventory(51));
        this.pos = data.pos();
        this.slotText = data.slotText();
        this.offsetText = data.offsetText();
    }


    public SetupBlockScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(Clutch.SETUP_BLOCK_SCREEN_HANDLER, syncId);
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

        // delete slot
        this.addSlot(new TrashSlot(this.deleteInventory, 0, 152, 18));
    }


    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public BlockPos getPos() {
        return this.pos;
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

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        if (slotIndex >= 0 && slotIndex < this.slots.size()) {
            Slot slot = this.slots.get(slotIndex);

            if (slot instanceof TrashSlot) {

                if (actionType == SlotActionType.QUICK_MOVE) {
                    for (int i = 0; i < this.inventory.size(); i++) {
                        this.inventory.setStack(i, ItemStack.EMPTY);
                    }
                    this.inventory.markDirty();
                    this.sendContentUpdates();
                    return;
                }

                if (actionType == SlotActionType.PICKUP || actionType == SlotActionType.PICKUP_ALL) {
                    this.setCursorStack(ItemStack.EMPTY);
                    return;
                }
            }
        }

        super.onSlotClick(slotIndex, button, actionType, player);
    }
}