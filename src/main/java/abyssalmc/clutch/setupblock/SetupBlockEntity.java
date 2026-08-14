package abyssalmc.clutch.setupblock;

import abyssalmc.clutch.Clutch;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import static abyssalmc.clutch.ClutchClient.*;
import static abyssalmc.clutch.ClutchClient.cursory;

// BLOCK ENTITY STATE (powered, items, text fields, redstone logic)
// also random methods are here since they use the data and are used when powered

public class SetupBlockEntity extends BlockEntity implements SetupBlockInventory, ExtendedScreenHandlerFactory<TextStoragePayload> {
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(51, ItemStack.EMPTY);

    public SetupBlockEntity(BlockPos pos, BlockState state) {
        super(Clutch.SETUP_BLOCK_ENTITY, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Setup Block");
    }

    private String slotText = "";
    private String offsetText = "";

    public String getSlotText() { return this.slotText; }
    public String getOffsetText() { return this.offsetText; }

    public void setFields(String slotText, String offsetText) {
        this.slotText = slotText;
        this.offsetText = offsetText;
        this.markDirty();
    }

    private boolean wasPowered = false;

    public void onRedstoneUpdate(boolean isPowered) {
        if (isPowered && !this.wasPowered) {
            overridePlayerInventory();

            // slot
            if (!getSlotText().isEmpty()) updateslot = Integer.parseInt(getSlotText());

            // cursor offset
            if (getOffsetText().split(" ").length > 1) {
                offsetEnabled = true;
                String xoff = getOffsetText().split(" ")[0];
                String yoff = getOffsetText().split(" ")[1];

                int xoffset = Integer.parseInt(xoff.substring(0, Math.min(xoff.length(), 5)));
                int yoffset = Integer.parseInt(yoff.substring(0, Math.min(yoff.length(), 5)));
                cursorx = xoffset;
                cursory = yoffset;
            }

            // trigger chain command blocks
            triggerFacingChainCommandBlock();
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
            overrideStack(32 + i, playerInv.main, i);
        }

        playerInv.markDirty();
        player.currentScreenHandler.sendContentUpdates();
    }

    public void pasteFromPlayer(PlayerEntity player) {
        if (this.world == null || this.world.isClient) return;

        PlayerInventory playerInv = player.getInventory();

        this.setStack(0, playerInv.offHand.get(0).copy());
        this.setStack(1, playerInv.armor.get(3).copy());
        this.setStack(2, playerInv.armor.get(2).copy());
        this.setStack(3, playerInv.armor.get(3).copy());
        this.setStack(4, playerInv.armor.get(0).copy());

        for (int i = 0; i < 27; i++) {
            this.setStack(5 + i, playerInv.main.get(9 + i).copy());
        }

        for (int i = 0; i < 9; i++) {
            this.setStack(32 + i, playerInv.main.get(i).copy());
        }

        this.markDirty();
        if (player.currentScreenHandler != null) {
            player.currentScreenHandler.sendContentUpdates();
        }
    }

    private void overrideStack(int blockSlot, DefaultedList<ItemStack> playerList, int playerIndex) {
        ItemStack blockStack = this.getStack(blockSlot);
        playerList.set(playerIndex, blockStack.copy());
    }

    private void triggerFacingChainCommandBlock() {
        if (this.world == null || this.world.isClient) return;

        Direction facing = this.getCachedState().get(Properties.FACING);
        BlockPos.Mutable currentPos = this.pos.offset(facing).mutableCopy();

        while (this.world.getBlockState(currentPos).isOf(Blocks.CHAIN_COMMAND_BLOCK)) {
            if (!(this.world.getBlockEntity(currentPos) instanceof CommandBlockBlockEntity chainBe)) break;

            chainBe.updateConditionMet();

            BlockState state = this.world.getBlockState(currentPos);
            boolean isPoweredOrAuto = chainBe.isAuto() || this.world.isReceivingRedstonePower(currentPos);

            if (!isPoweredOrAuto || !chainBe.isConditionMet()) break;

            chainBe.getCommandExecutor().execute(this.world);

            currentPos.move(state.get(CommandBlock.FACING));
        }
    }



    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new SetupBlockScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public TextStoragePayload getScreenOpeningData(ServerPlayerEntity player) {
        return new TextStoragePayload(this.pos, this.slotText, this.offsetText);
    }


    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        Inventories.writeNbt(nbt, inventory, registries);
        nbt.putBoolean("WasPowered", this.wasPowered);
        nbt.putString("SlotText", this.slotText);
        nbt.putString("OffsetText", this.offsetText);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        Inventories.readNbt(nbt, inventory, registries);
        this.wasPowered = nbt.getBoolean("WasPowered");
        this.slotText = nbt.getString("SlotText");
        this.offsetText = nbt.getString("OffsetText");
    }
}