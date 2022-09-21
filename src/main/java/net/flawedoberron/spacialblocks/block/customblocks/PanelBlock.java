package net.flawedoberron.spacialblocks.block.customblocks;

import net.minecraft.block.*;
import net.minecraft.block.enums.BlockHalf;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.feature.util.CaveSurface;

import java.util.Map;

public class PanelBlock extends HorizontalConnectingBlock {
    public static final BooleanProperty UP;
    public static final BooleanProperty DOWN;
    private final VoxelShape[] cullingShapes;

    static {
        UP = ConnectingBlock.UP;
        DOWN = ConnectingBlock.DOWN;
    }

    public PanelBlock(AbstractBlock.Settings settings) {
        super(2.0F, 2.0F, 16.0F, 16.0F, 16.0F, settings);
        this.setDefaultState((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.stateManager.getDefaultState()).with(NORTH, false)).with(EAST, false)).with(SOUTH, false)).with(WEST, false)).with(WATERLOGGED, false));
        this.cullingShapes = this.createShapes(2.0F, 1.0F, 16.0F, 6.0F, 15.0F);
    }

    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return this.cullingShapes[this.getShapeIndex(state)];
    }

    public VoxelShape getCameraCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return this.getOutlineShape(state, world, pos, context);
    }

    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return true;
    }

    public boolean canConnect(BlockState state, boolean neighborIsFullSquare, Direction dir) {
        Block block = state.getBlock();

        if (neighborIsFullSquare)
        {
            neighborIsFullSquare = !(block instanceof CraftingTableBlock || block instanceof FurnaceBlock || block instanceof BlastFurnaceBlock);
        }

        boolean bl = this.canConnectToFence(state);
        boolean bl2 = this.canConnectToWallOrPane(state);
        boolean bl3 = block instanceof FenceGateBlock && FenceGateBlock.canWallConnect(state, dir) && !(block instanceof CraftingTableBlock || block instanceof FurnaceBlock || block instanceof  BlastFurnaceBlock);
        return !cannotConnect(state) && neighborIsFullSquare || bl || bl2 || bl3;
    }

    private boolean canConnectToFence(BlockState state) {
        return state.isIn(BlockTags.FENCES) && state.isIn(BlockTags.WOODEN_FENCES) == this.getDefaultState().isIn(BlockTags.WOODEN_FENCES);
    }

    private  boolean canConnectToWallOrPane(BlockState state) {
        return  state.isIn(BlockTags.WALLS) || state.getBlock() instanceof PaneBlock;
    }

    private boolean canVExtendTo(BlockState state, Boolean up)
    {
        Block block = state.getBlock();
        Boolean validBType = false;
        Boolean topHalf = false;

        if (block instanceof  ModStairsBlock)
        {
            ModStairsBlock blockAsStairs = (ModStairsBlock)block;
            validBType = true;
            topHalf = state.get(StairsBlock.HALF) == (BlockHalf.TOP);
        }

        if (block instanceof SlabBlock)
        {
            SlabBlock blockAsSlab = (SlabBlock)block;
            validBType = true;
            topHalf = state.get(SlabBlock.TYPE) == SlabType.TOP ||
                    (state.get(SlabBlock.TYPE) == SlabType.DOUBLE && up);
        }

        System.out.println("Checked Block: " + block + ", Is Valid: " + validBType + ", Matches Half: " + (up == topHalf));

        return  validBType && up == topHalf;
    }

    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockView blockView = ctx.getWorld();
        FluidState fluidState = ctx.getWorld().getFluidState(ctx.getBlockPos());

        BlockPos blockPos = ctx.getBlockPos();
        BlockPos blockPos2 = blockPos.north();
        BlockPos blockPos3 = blockPos.east();
        BlockPos blockPos4 = blockPos.south();
        BlockPos blockPos5 = blockPos.west();

        BlockState blockState = blockView.getBlockState(blockPos2);
        BlockState blockState2 = blockView.getBlockState(blockPos3);
        BlockState blockState3 = blockView.getBlockState(blockPos4);
        BlockState blockState4 = blockView.getBlockState(blockPos5);

        BlockPos blockAbove = blockPos.up();
        BlockPos blockBelow = blockPos.down();

        return (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)super.getPlacementState(ctx)
                .with(NORTH, this.canConnect(blockState, blockState.isSideSolidFullSquare(blockView, blockPos2, Direction.SOUTH), Direction.SOUTH)))
                .with(EAST, this.canConnect(blockState2, blockState2.isSideSolidFullSquare(blockView, blockPos3, Direction.WEST), Direction.WEST)))
                .with(SOUTH, this.canConnect(blockState3, blockState3.isSideSolidFullSquare(blockView, blockPos4, Direction.NORTH), Direction.NORTH)))
                .with(WEST, this.canConnect(blockState4, blockState4.isSideSolidFullSquare(blockView, blockPos5, Direction.EAST), Direction.EAST)))
                .with(UP, this.canVExtendTo(blockView.getBlockState(blockAbove), true))
                .with(DOWN, this.canVExtendTo(blockView.getBlockState(blockBelow), false))
                .with(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if ((Boolean)state.get(WATERLOGGED)) {
            world.getFluidTickScheduler().schedule(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
        }

        return direction.getAxis().getType() == Direction.Type.HORIZONTAL ?
                (BlockState)state.with((Property)FACING_PROPERTIES.get(direction), this.canConnect(neighborState, neighborState.isSideSolidFullSquare(world, neighborPos, direction.getOpposite()), direction.getOpposite()))
                : direction.getAxis().getType() ==    Direction.Type.VERTICAL ?
                (BlockState)state.with(direction.getDirection() == Direction.AxisDirection.POSITIVE ? UP : DOWN, this.canVExtendTo(neighborState, direction.getDirection() == Direction.AxisDirection.POSITIVE))
                : super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(new Property[]{NORTH, EAST, WEST, SOUTH, WATERLOGGED, UP, DOWN});
    }
}
