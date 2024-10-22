package io.github.implicitsaber.forkcart.block;

import com.mojang.serialization.MapCodec;
import io.github.implicitsaber.forkcart.Forkcart;
import io.github.implicitsaber.forkcart.util.Pose;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4d;
import org.joml.Matrix3d;
import org.joml.Vector3d;

import java.util.HashMap;
import java.util.Map;

public class TrackTiesBlock extends FacingBlock implements BlockEntityProvider {
    public static final IntProperty POINTING = IntProperty.of("pointing", 0, 3);
    public static final Map<Direction, Map<Direction, Integer>> POINTING_MAP = new HashMap<>();
    public static final VoxelShape[] SHAPES = new VoxelShape[Direction.values().length];

    static {
        Map<Direction, Integer> upMap = new HashMap<>();
        upMap.put(Direction.NORTH, 0);
        upMap.put(Direction.WEST, 1);
        upMap.put(Direction.SOUTH, 2);
        upMap.put(Direction.EAST, 3);
        Map<Direction, Integer> downMap = new HashMap<>();
        downMap.put(Direction.NORTH, 2);
        downMap.put(Direction.WEST, 1);
        downMap.put(Direction.SOUTH, 0);
        downMap.put(Direction.EAST, 3);
        Map<Direction, Integer> northMap = new HashMap<>();
        northMap.put(Direction.DOWN, 0);
        northMap.put(Direction.WEST, 1);
        northMap.put(Direction.UP, 2);
        northMap.put(Direction.EAST, 3);
        Map<Direction, Integer> eastMap = new HashMap<>();
        eastMap.put(Direction.NORTH, 1);
        eastMap.put(Direction.UP, 2);
        eastMap.put(Direction.SOUTH, 3);
        eastMap.put(Direction.DOWN, 0);
        Map<Direction, Integer> southMap = new HashMap<>();
        southMap.put(Direction.UP, 2);
        southMap.put(Direction.WEST, 3);
        southMap.put(Direction.DOWN, 0);
        southMap.put(Direction.EAST, 1);
        Map<Direction, Integer> westMap = new HashMap<>();
        westMap.put(Direction.NORTH, 3);
        westMap.put(Direction.DOWN, 0);
        westMap.put(Direction.SOUTH, 1);
        westMap.put(Direction.UP, 2);
        POINTING_MAP.put(Direction.UP, upMap);
        POINTING_MAP.put(Direction.DOWN, downMap);
        POINTING_MAP.put(Direction.NORTH, northMap);
        POINTING_MAP.put(Direction.EAST, eastMap);
        POINTING_MAP.put(Direction.SOUTH, southMap);
        POINTING_MAP.put(Direction.WEST, westMap);

        for (var dir : Direction.values()) {
            int idx = dir.ordinal();
            var min = new Vector3d(-8, -8, -8);
            var max = new Vector3d(8, -6, 8);

            var rot = dir.getRotationQuaternion();
            rot.transform(min);
            rot.transform(max);

            min.add(8, 8, 8);
            max.add(8, 8, 8);

            SHAPES[idx] = createCuboidShape(
                    Math.min(min.x(), max.x()),
                    Math.min(min.y(), max.y()),
                    Math.min(min.z(), max.z()),
                    Math.max(min.x(), max.x()),
                    Math.max(min.y(), max.y()),
                    Math.max(min.z(), max.z()));
        }
    }

    public TrackTiesBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FACING, Direction.UP).with(POINTING, 0));
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return world.getBlockEntity(pos) instanceof TrackTiesBlockEntity ttBE ? (ttBE.hasCart() ? 15 : 0) : 0;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, POINTING);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction side = canBeNotUpright() ? ctx.getSide() : Direction.UP;
        Map<Direction, Integer> pMap = POINTING_MAP.getOrDefault(side, Map.of());
        Direction hLook = ctx.getHorizontalPlayerFacing();
        int pointing = pMap.containsKey(hLook) ? pMap.get(hLook) : pMap.getOrDefault(ctx.getVerticalPlayerLookDirection(), 0);
        return getDefaultState().with(FACING, side).with(POINTING, pointing);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPES[state.get(FACING).ordinal()];
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (world.getBlockEntity(pos) instanceof TrackTiesBlockEntity tie) {
            if (!newState.isOf(state.getBlock())) {
                if (!world.isClient()) tie.onDestroy();
            } else {
                tie.updatePose(pos, newState);
            }
        }

        super.onStateReplaced(state, world, pos, newState, moved);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (player.canModifyBlocks() &&
                !player.getStackInHand(Hand.MAIN_HAND).isIn(Forkcart.TRACK_TAG) &&
                world.getBlockEntity(pos) instanceof TrackTiesBlockEntity tie) {
            if (tie.prev() == null && tie.next() == null) {
                if (world.isClient()) {
                    return ActionResult.SUCCESS;
                } else {
                    var newState = state.with(POINTING, (state.get(POINTING) + 1) % 4);
                    world.setBlockState(pos, newState);
                    tie.updatePose(pos, newState);
                    tie.markDirty();
                    tie.sync();

                    return ActionResult.CONSUME;
                }
            }
        }

        return super.onUse(state, world, pos, player, hand, hit);
    }

    public Pose getPose(BlockState state, BlockPos pos) {
        if (state.contains(FACING) && state.contains(POINTING)) {
            var face = state.get(FACING);
            int point = state.get(POINTING);

            return getPose(pos, face, point);
        }

        return null;
    }

    public static Pose getPose(BlockPos block, Direction normal, int point) {
        var pos = new Vector3d();
        var basis = new Matrix3d().identity();

        var normVec = normal.getVector();
        pos.set(normVec.getX(), normVec.getY(), normVec.getZ()).mul(-0.4375).add(block.getX() + 0.5, block.getY() + 0.5, block.getZ() + 0.5);

        if (normal == Direction.UP || normal == Direction.DOWN) {
            point += 2;
        }

        var axisAngle = new AxisAngle4d(point * MathHelper.PI * 0.5, normVec.getX(), normVec.getY(), normVec.getZ());
        basis.rotate(axisAngle);
        basis.rotate(normal.getRotationQuaternion());

        return new Pose(pos, basis);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TrackTiesBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
       if(type == Forkcart.TRACK_TIES_BE) {
           return TrackTiesBlockEntity::staticTick;
       }
       return null;
    }

    public boolean canBeNotUpright() {
        return true;
    }

}
