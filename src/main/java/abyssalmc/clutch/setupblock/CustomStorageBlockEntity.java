package abyssalmc.clutch.setupblock;

import abyssalmc.clutch.Clutch;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class CustomStorageBlockEntity extends BlockEntity implements bInventory, ExtendedScreenHandlerFactory<BlockPos> {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(51, ItemStack.EMPTY);

    public CustomStorageBlockEntity(BlockPos pos, BlockState state) {
        super(Clutch.CUSTOM_BLOCK_ENTITY, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Setup Block");
    }

    private boolean wasPowered = false;

    public void onRedstoneUpdate(boolean isPowered) {
        if (isPowered && !this.wasPowered) {
            overridePlayerInventory();
        }
        this.wasPowered = isPowered;
    }

    private void overridePlayerInventory() {
        if (this.world == null || this.world.isClient) return;

        PlayerEntity player = this.world.getClosestPlayer(
                this.pos.getX() + 0.5,
                this.pos.getY() + 0.5,
                this.pos.getZ() + 0.5,
                5.0,
                false
        );

        if (player == null) return;

        PlayerInventory playerInv = player.getInventory();

        // override armor
        overrideStack(0, playerInv.offHand, 0);
        overrideStack(1, playerInv.armor, 3);
        overrideStack(2, playerInv.armor, 2);
        overrideStack(3, playerInv.armor, 1);
        overrideStack(4, playerInv.armor, 0);

        // override inv
        for (int i = 0; i < 27; i++) {
            overrideStack(5 + i, playerInv.main, 9 + i);
        }

        // override hotbar
        for (int i = 0; i < 9; i++) {
            overrideStack(33 + i, playerInv.main, i);
        }

        playerInv.markDirty();
        player.currentScreenHandler.sendContentUpdates();
    }

    private void overrideStack(int blockSlot, DefaultedList<ItemStack> playerList, int playerIndex) {
        ItemStack blockStack = this.getStack(blockSlot);
        playerList.set(playerIndex, blockStack.copy());
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new CustomStorageScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayerEntity player) {
        return this.pos;
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        Inventories.writeNbt(nbt, inventory, registries);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        Inventories.readNbt(nbt, inventory, registries);
    }
}