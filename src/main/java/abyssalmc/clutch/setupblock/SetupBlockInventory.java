package abyssalmc.clutch.setupblock;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Direction;

// BLOCK INVENTORY (basic handling)

public interface SetupBlockInventory extends SidedInventory {
    DefaultedList<ItemStack> getItems();

    @Override
    default int[] getAvailableSlots(Direction side) {
        int[] result = new int[getItems().size()];
        for (int i = 0; i < result.length; i++) result[i] = i;
        return result;
    }

    @Override default boolean canInsert(int slot, ItemStack stack, Direction side) { return true; }
    @Override default boolean canExtract(int slot, ItemStack stack, Direction side) { return true; }
    @Override default int size() { return getItems().size(); }
    @Override default boolean isEmpty() { return getItems().stream().allMatch(ItemStack::isEmpty); }
    @Override default ItemStack getStack(int slot) { return getItems().get(slot); }

    @Override
    default ItemStack removeStack(int slot, int count) {
        ItemStack result = Inventories.splitStack(getItems(), slot, count);
        if (!result.isEmpty()) markDirty();
        return result;
    }

    @Override
    default ItemStack removeStack(int slot) {
        return Inventories.removeStack(getItems(), slot);
    }

    @Override
    default void setStack(int slot, ItemStack stack) {
        getItems().set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        markDirty();
    }

    @Override default void clear() { getItems().clear(); }
    @Override default void markDirty() {}
    @Override default boolean canPlayerUse(PlayerEntity player) { return true; }
}