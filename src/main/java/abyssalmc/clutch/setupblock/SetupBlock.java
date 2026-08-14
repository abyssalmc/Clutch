package abyssalmc.clutch.setupblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

// BLOCK (adds block entity, some generic properties)

public class SetupBlock extends Block implements BlockEntityProvider {

    public SetupBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(FACING, Direction.UP));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public static final DirectionProperty FACING = Properties.FACING;

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SetupBlockEntity(pos, state);
    }

    // redstone power
    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);

        if (!world.isClient) {
            boolean isPowered = world.isReceivingRedstonePower(pos);
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof SetupBlockEntity storageBE) {
                storageBE.onRedstoneUpdate(isPowered);
            }
        }
    }

    // place direction
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (!world.isClient) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof SetupBlockEntity storageBE) {
                player.openHandledScreen(storageBE);
            }
        }
        return ActionResult.SUCCESS;
    }
}