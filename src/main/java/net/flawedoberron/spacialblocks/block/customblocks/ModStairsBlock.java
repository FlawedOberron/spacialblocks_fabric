package net.flawedoberron.spacialblocks.block.customblocks;

import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.StairShape;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.*;
import net.minecraft.world.BlockView;

public class ModStairsBlock extends StairsBlock {
    // A bunch of bullshit used for all the more 'detailed' stairs logic...
    protected static final VoxelShape TOP_BASE;
    protected static final VoxelShape BOTTOM_BASE;

    protected static final VoxelShape TOP_NEDGESHAPE;
    protected static final VoxelShape TOP_EEDGESHAPE;
    protected static final VoxelShape TOP_SEDGESHAPE;
    protected static final VoxelShape TOP_WEDGESHAPE;
    protected static final VoxelShape BOTTOM_NEDGESHAPE;
    protected static final VoxelShape BOTTOM_EEDGESHAPE;
    protected static final VoxelShape BOTTOM_SEDGESHAPE;
    protected static final VoxelShape BOTTOM_WEDGESHAPE;

    protected static final VoxelShape TOP_NWCORNSHAPE;
    protected static final VoxelShape TOP_NECORNSHAPE;
    protected static final VoxelShape TOP_SECORNSHAPE;
    protected static final VoxelShape TOP_SWCORNSHAPE;
    protected static final VoxelShape BOTTOM_NWCORNSHAPE;
    protected static final VoxelShape BOTTOM_NECORNSHAPE;
    protected static final VoxelShape BOTTOM_SECORNSHAPE;
    protected static final VoxelShape BOTTOM_SWCORNSHAPE;
    
    public ModStairsBlock(BlockState baseBlockState, Settings settings) {
        super(baseBlockState, settings);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        boolean isTop = state.get(HALF) != BlockHalf.TOP;
        VoxelShape shape = isTop ? TOP_BASE : BOTTOM_BASE;
        var stairShape = getStairShape(state, world, pos);
        var facing = state.get(FACING);

        switch (stairShape)
        {
            case STRAIGHT -> {
                // Return the straight edge based on facing...
                switch (facing)
                {
                    case NORTH -> shape = VoxelShapes.union(shape, isTop ? TOP_SEDGESHAPE : BOTTOM_SEDGESHAPE);
                    case EAST -> shape = VoxelShapes.union(shape, isTop? TOP_WEDGESHAPE : BOTTOM_WEDGESHAPE);
                    case SOUTH -> shape = VoxelShapes.union(shape, isTop ? TOP_NEDGESHAPE : BOTTOM_NEDGESHAPE);
                    case WEST -> shape = VoxelShapes.union(shape, isTop? TOP_EEDGESHAPE : BOTTOM_EEDGESHAPE);
                }

            }

            case OUTER_LEFT -> {
                switch (facing)
                {
                    case NORTH -> shape = VoxelShapes.union(shape, isTop ? TOP_SWCORNSHAPE : BOTTOM_SWCORNSHAPE);
                    case EAST -> shape = VoxelShapes.union(shape, isTop? TOP_SECORNSHAPE : BOTTOM_SECORNSHAPE);
                    case SOUTH -> shape = VoxelShapes.union(shape, isTop ? TOP_NECORNSHAPE : BOTTOM_NECORNSHAPE);
                    case WEST -> shape = VoxelShapes.union(shape, isTop? TOP_NWCORNSHAPE : BOTTOM_NWCORNSHAPE);
                }

            }

            case INNER_LEFT -> {
                switch (facing)
                {
                    case NORTH -> shape = VoxelShapes.union(shape, isTop ? TOP_EEDGESHAPE : BOTTOM_EEDGESHAPE, isTop ? TOP_SEDGESHAPE : BOTTOM_SEDGESHAPE);
                    case EAST -> shape = VoxelShapes.union(shape, isTop ? TOP_SEDGESHAPE : BOTTOM_SEDGESHAPE, isTop ? TOP_WEDGESHAPE : BOTTOM_WEDGESHAPE);
                    case SOUTH -> shape = VoxelShapes.union(shape, isTop ? TOP_WEDGESHAPE : BOTTOM_WEDGESHAPE, isTop ? TOP_NEDGESHAPE : BOTTOM_NEDGESHAPE);
                    case WEST -> shape = VoxelShapes.union(shape, isTop ? TOP_NEDGESHAPE : BOTTOM_NEDGESHAPE, isTop ? TOP_EEDGESHAPE : BOTTOM_EEDGESHAPE);
                }
            }

            case OUTER_RIGHT -> {
                switch (facing)
                {
                    case NORTH -> shape = VoxelShapes.union(shape, isTop ? TOP_SECORNSHAPE : BOTTOM_SECORNSHAPE);
                    case EAST -> shape = VoxelShapes.union(shape, isTop? TOP_NECORNSHAPE : BOTTOM_NECORNSHAPE);
                    case SOUTH -> shape = VoxelShapes.union(shape, isTop ? TOP_NWCORNSHAPE : BOTTOM_NWCORNSHAPE);
                    case WEST -> shape = VoxelShapes.union(shape, isTop? TOP_SWCORNSHAPE : BOTTOM_SWCORNSHAPE);
                }

            }

            case INNER_RIGHT -> {
                switch (facing)
                {
                    case NORTH -> shape = VoxelShapes.union(shape, isTop ? TOP_WEDGESHAPE : BOTTOM_WEDGESHAPE, isTop ? TOP_SEDGESHAPE : BOTTOM_SEDGESHAPE);
                    case EAST -> shape = VoxelShapes.union(shape, isTop ? TOP_NEDGESHAPE : BOTTOM_NEDGESHAPE, isTop ? TOP_WEDGESHAPE : BOTTOM_WEDGESHAPE);
                    case SOUTH -> shape = VoxelShapes.union(shape, isTop ? TOP_EEDGESHAPE : BOTTOM_EEDGESHAPE, isTop ? TOP_NEDGESHAPE : BOTTOM_NEDGESHAPE);
                    case WEST -> shape = VoxelShapes.union(shape, isTop ? TOP_SEDGESHAPE : BOTTOM_SEDGESHAPE, isTop ? TOP_EEDGESHAPE : BOTTOM_EEDGESHAPE);
                }
            }
        }

        return shape;
    }

    private static StairShape getStairShape(BlockState state, BlockView world, BlockPos pos) {
        Direction direction = state.get(FACING);
        BlockState blockState = world.getBlockState(pos.offset(direction));
        if (isStairs(blockState) && state.get(HALF) == blockState.get(HALF)) {
            Direction direction2 = blockState.get(FACING);
            if (direction2.getAxis() != state.get(FACING).getAxis() && isDifferentOrientation(state, world, pos, direction2.getOpposite())) {
                if (direction2 == direction.rotateYCounterclockwise()) {
                    return StairShape.OUTER_LEFT;
                }

                return StairShape.OUTER_RIGHT;
            }
        }

        BlockState direction2 = world.getBlockState(pos.offset(direction.getOpposite()));
        if (isStairs(direction2) && state.get(HALF) == direction2.get(HALF)) {
            Direction direction3 = direction2.get(FACING);
            if (direction3.getAxis() != state.get(FACING).getAxis() && isDifferentOrientation(state, world, pos, direction3)) {
                if (direction3 == direction.rotateYCounterclockwise()) {
                    return StairShape.INNER_LEFT;
                }

                return StairShape.INNER_RIGHT;
            }
        }

        return StairShape.STRAIGHT;
    }

    private static boolean isDifferentOrientation(BlockState state, BlockView world, BlockPos pos, Direction dir) {
        BlockState blockState = world.getBlockState(pos.offset(dir));
        return !isStairs(blockState) || blockState.get(FACING) != state.get(FACING) || blockState.get(HALF) != state.get(HALF);
    }

    static {
        TOP_BASE = Block.createCuboidShape(0,0,0,16,4,16);
        BOTTOM_BASE = Block.createCuboidShape(0, 12,0,16,16,16);

        TOP_NEDGESHAPE = VoxelShapes.union(Block.createCuboidShape(0, 4, 4, 16, 8, 16),
                Block.createCuboidShape(0, 8, 8, 16, 12, 16),
                Block.createCuboidShape(0, 12, 12, 16, 16, 16));
        TOP_SEDGESHAPE = VoxelShapes.union(Block.createCuboidShape(0, 4, 0, 16, 8, 12),
                Block.createCuboidShape(0, 8, 0, 16, 12, 8),
                Block.createCuboidShape(0, 12, 0, 16, 16, 4));
        TOP_EEDGESHAPE = VoxelShapes.union(Block.createCuboidShape(0, 4, 0, 12, 8, 16),
                Block.createCuboidShape(0, 8, 0, 8, 12, 16),
                Block.createCuboidShape(0, 12, 0, 4, 16, 16));
        TOP_WEDGESHAPE = VoxelShapes.union(Block.createCuboidShape(4, 4, 0, 16, 8, 16),
                Block.createCuboidShape(8, 8, 0, 16, 12, 16),
                Block.createCuboidShape(12, 12, 0, 16, 16, 16));

        BOTTOM_NEDGESHAPE = VoxelShapes.union(Block.createCuboidShape(0, 8, 4, 16, 12, 16),
                Block.createCuboidShape(0, 4, 8, 16, 8, 16),
                Block.createCuboidShape(0, 0, 12, 16, 4, 16));
        BOTTOM_SEDGESHAPE = VoxelShapes.union(Block.createCuboidShape(0, 8, 0, 16, 12, 12),
                Block.createCuboidShape(0, 4, 0, 16, 8, 8),
                Block.createCuboidShape(0, 0, 0, 16, 4, 4));
        BOTTOM_EEDGESHAPE = VoxelShapes.union(Block.createCuboidShape(0, 8, 0, 12, 12, 16),
                Block.createCuboidShape(0, 4, 0, 8, 8, 16),
                Block.createCuboidShape(0, 0, 0, 4, 4, 16));
        BOTTOM_WEDGESHAPE = VoxelShapes.union(Block.createCuboidShape(4, 8, 0, 16, 12, 16),
                Block.createCuboidShape(8, 4, 0, 16, 8, 16),
                Block.createCuboidShape(12, 0, 0, 16, 4, 16));

        TOP_NWCORNSHAPE = VoxelShapes.union(Block.createCuboidShape(0, 4, 4, 12, 8, 16),
                Block.createCuboidShape(0, 8, 8, 8, 12, 16),
                Block.createCuboidShape(0, 12, 12, 4, 16, 16));
        TOP_NECORNSHAPE = VoxelShapes.union(Block.createCuboidShape(4, 4, 4, 16, 8, 16),
                Block.createCuboidShape(8, 8, 8, 16, 12, 16),
                Block.createCuboidShape(12, 12, 12, 16, 16, 16));
        TOP_SWCORNSHAPE = VoxelShapes.union(Block.createCuboidShape(0, 4, 0, 12, 8, 12),
                Block.createCuboidShape(0, 8, 0, 8, 12, 8),
                Block.createCuboidShape(0, 12, 0, 4, 16, 4));
        TOP_SECORNSHAPE = VoxelShapes.union(Block.createCuboidShape(4, 4, 0, 16, 8, 12),
                Block.createCuboidShape(8, 8, 0, 16, 12, 8),
                Block.createCuboidShape(12, 12, 0, 16, 16, 4));

        BOTTOM_NWCORNSHAPE = VoxelShapes.union(Block.createCuboidShape(0, 8, 4, 12, 12, 16),
                Block.createCuboidShape(0, 4, 8, 8, 8, 16),
                Block.createCuboidShape(0, 0, 12, 4, 4, 16));

        BOTTOM_NECORNSHAPE = VoxelShapes.union(Block.createCuboidShape(4, 8, 4, 16, 12, 16),
                Block.createCuboidShape(8, 4, 8, 16, 8, 16),
                Block.createCuboidShape(12, 0, 12, 16, 4, 16));
        BOTTOM_SWCORNSHAPE = VoxelShapes.union(Block.createCuboidShape(0, 8, 0, 12, 12, 12),
                Block.createCuboidShape(0, 4, 0, 8, 8, 8),
                Block.createCuboidShape(0, 0, 0, 4, 4, 4));
        BOTTOM_SECORNSHAPE = VoxelShapes.union(Block.createCuboidShape(4, 8, 0, 16, 12, 12),
                Block.createCuboidShape(8, 4, 0, 16, 8, 8),
                Block.createCuboidShape(12, 0, 0, 16, 4, 4));

    }
}
