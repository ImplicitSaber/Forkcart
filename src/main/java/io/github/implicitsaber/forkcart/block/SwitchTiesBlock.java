package io.github.implicitsaber.forkcart.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class SwitchTiesBlock extends TrackTiesBlock {

    public static final BooleanProperty SWITCHED = BooleanProperty.of("switched");

    public static final Map<Integer, Vec3i> VELOCITY_MAP = new HashMap<>();

    static {
        VELOCITY_MAP.put(0, Direction.EAST.getVector());
        VELOCITY_MAP.put(1, Direction.NORTH.getVector());
        VELOCITY_MAP.put(2, Direction.WEST.getVector());
        VELOCITY_MAP.put(3, Direction.SOUTH.getVector());
    }

    public SwitchTiesBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FACING, Direction.UP).with(POINTING, 0).with(SWITCHED, false));
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        boolean switched = state.get(SWITCHED);
        boolean powered = world.isReceivingRedstonePower(pos);
        if(switched != powered) world.setBlockState(pos, state.with(SWITCHED, powered), Block.NOTIFY_ALL);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(SWITCHED);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        boolean switched = state.get(SWITCHED);
        boolean powered = world.isReceivingRedstonePower(pos);
        if(switched != powered) world.setBlockState(pos, state.with(SWITCHED, powered), Block.NOTIFY_ALL);
    }

    @Override
    public boolean canBeNotUpright() {
        return false;
    }

}
