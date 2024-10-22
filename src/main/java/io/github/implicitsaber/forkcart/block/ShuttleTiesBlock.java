package io.github.implicitsaber.forkcart.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ShuttleTiesBlock extends TrackTiesBlock {

    public static final BooleanProperty RUNNING = BooleanProperty.of("running");

    public ShuttleTiesBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FACING, Direction.UP).with(POINTING, 0).with(RUNNING, true));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        boolean running = state.get(RUNNING);
        boolean powered = world.isReceivingRedstonePower(pos);
        if(running == powered) world.setBlockState(pos, state.with(RUNNING, !powered), Block.NOTIFY_ALL);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(RUNNING);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        boolean running = state.get(RUNNING);
        boolean powered = world.isReceivingRedstonePower(pos);
        if(running == powered) world.setBlockState(pos, state.with(RUNNING, !powered), Block.NOTIFY_ALL);
    }

}
