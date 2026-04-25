package net.br1ghtsteel.thehunt.entity.ai.goal;

import net.br1ghtsteel.thehunt.TheHunt;
import net.br1ghtsteel.thehunt.entity.AbstractHunterEntity;
import net.br1ghtsteel.thehunt.entity.ai.pathing.HunterNavigation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumSet;

public class JumpGapOnPathGoal extends Goal implements HunterNavGoal {
    private final AbstractHunterEntity hunter;
    private final int jumpRange;
    private final int clutchRange;
    private final World world;
    private final HunterNavigation navigation;
    private final Block clutchBlock;
    private BlockPos targetBlockPos;
    private int targetNodeIndex;
    private boolean jumpFlag;
    private boolean clutchFlag;
    private BlockPos clutchPos;


    public JumpGapOnPathGoal(AbstractHunterEntity hunter, int jumpRange, int clutchRange, Block clutchBlock) {
        this.hunter = hunter;
        this.world = hunter.getWorld();
        this.navigation = (HunterNavigation) hunter.getNavigation();
        this.jumpRange = jumpRange;
        this.clutchRange = clutchRange;
        this.clutchBlock = clutchBlock;
        this.jumpFlag = false;
        this.navigation.registerGoal(this);
        this.setControls(EnumSet.of(Goal.Control.JUMP));
    }

    @Override
    public boolean canStart() {
        Path path = this.navigation.getCurrentPath();
        if (path != null && !path.isFinished()) {
            if (path.getCurrentNode().type == PathNodeType.LEAVES) {
                this.jumpFlag = true;
            }

            if (!this.jumpFlag) {
                return false;
            }

            // Check we are close enough to try jumping
            if (this.hunter.getY() <= this.world.getBottomY() || !this.world.getBlockState(this.hunter.getBlockPos().down()).isAir()) {
                return false;
            }

            // Try to find a target block to land on
            int j = path.getCurrentNodeIndex();
            for (int i = j; i < Math.min(j + this.jumpRange, path.getLength()); i++) {
                PathNode pathNode = path.getNode(i);
                if (canJumpToNode(pathNode)) {
                    this.clutchFlag = false;
                    this.targetBlockPos = pathNode.getBlockPos();
                    this.targetNodeIndex = i;
                    return true;
                }
            }

            // Try to find a position we can aim at to place a block and clutch
            for (int i = Math.min(j + this.jumpRange, path.getLength()) - 1; i > j; i--) {
                PathNode pathNode = path.getNode(i);
                if (canClutchOnNode(pathNode)) {
                    clutchFlag = true;
                    this.targetBlockPos = pathNode.getBlockPos();
                    this.targetNodeIndex = i;
                    this.clutchPos = pathNode.getBlockPos().down();
                    TheHunt.sendChatMessage("Clutching");
                    return true;
                }
            }

            this.jumpFlag = false;
            this.navigation.stop();
        }

        return false;
    }

    protected boolean canJumpToNode(PathNode node) {
        return node.type != PathNodeType.BLOCKED
                && node.type != PathNodeType.BREACH
                && node.type != PathNodeType.STICKY_HONEY
                && node.type != PathNodeType.LEAVES
                && canJumpToBlock(this.world.getBlockState(node.getBlockPos().down()));
    }

    protected boolean canClutchOnNode(PathNode node) {
        return node.getPos().y > this.world.getBottomY() && findClutchSource(node.getBlockPos().down()) != null;
    }

    protected BlockPos findClutchSource(BlockPos blockPos) {
        for (Direction direction : Direction.values()) {
            if (direction.getAxis() == Direction.Axis.Y) {
                continue;
            }
            BlockPos sourcePos = blockPos.offset(direction);
            if (canClutchOffBlock(this.world.getBlockState(sourcePos))) {
                return sourcePos;
            }
        }
        return null;
    }

    protected boolean canJumpToBlock(BlockState blockState) {
        return !blockState.isAir() && !blockState.isOf(Blocks.WATER) && !blockState.isOf(Blocks.LAVA);
    }

    protected boolean canClutchOffBlock(BlockState blockState) {
        return !blockState.isAir() && !blockState.isOf(Blocks.WATER) && !blockState.isOf(Blocks.LAVA);
    }

    @Override
    public boolean shouldContinue() {
        return !this.hunter.isOnGround();
    }

    @Override
    public void stop() {
        super.stop();
        this.jumpFlag = false;
    }

    @Override
    public void start() {
        TheHunt.sendChatMessage("Jumping to: " + targetBlockPos);
        hunter.jumpToPos(targetBlockPos.toCenterPos());
        Path path = this.hunter.getNavigation().getCurrentPath();
        if (path != null) {
            int i = Math.min(this.targetNodeIndex + 2, path.getLength() - 1);
            path.setCurrentNodeIndex(i);
        }
    }

    @Override
    public void tick() {
        if (clutchFlag && this.hunter.squaredDistanceTo(clutchPos.toCenterPos()) <= this.hunter.getReachDistance() * this.hunter.getReachDistance()) {
            tryClutch(clutchPos);
        }
    }

    protected void tryClutch(BlockPos pos) {
        BlockPos sourcePos = findClutchSource(pos);
        if (sourcePos == null) {
            return;
        }
        placeClutchBlock(pos, sourcePos);
    }

    protected void placeClutchBlock(BlockPos pos, BlockPos sourcePos) {
        if (!this.hunter.handSwinging) {
            this.hunter.swingHand(this.hunter.getActiveHand());
        }
        this.hunter.getLookControl().lookAt(sourcePos.getX(), sourcePos.getY(), sourcePos.getZ(), 60.0F, 60.0F);
        this.world.setBlockState(pos, clutchBlock.getDefaultState());

        // this.mob.getWorld().syncWorldEvent(WorldEvents.ZOMBIE_BREAKS_WOODEN_DOOR, this.targetBlockPos, 0);
    }

    @Override
    public void onPathNodeCompleted(PathNode node) {

    }
}
