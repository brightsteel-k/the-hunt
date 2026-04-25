package net.br1ghtsteel.thehunt.entity.ai.goal;

import net.br1ghtsteel.thehunt.entity.AbstractHunterEntity;
import net.br1ghtsteel.thehunt.entity.ai.pathing.HunterNavigation;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNode;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldEvents;

import java.util.EnumSet;

public class BreakBlockOnPathGoal extends Goal {
    protected AbstractHunterEntity hunter;
    protected BlockPos targetBlockPos = BlockPos.ORIGIN;
    protected BlockState targetBlockState;

    protected boolean targetBlockValid;
    protected int breakProgress;
    protected int prevBreakProgress = -1;
    protected int maxProgress = -1;

    public BreakBlockOnPathGoal(AbstractHunterEntity hunter) {
        this.hunter = hunter;
        this.setControls(EnumSet.of(Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (!this.hunter.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            return false;
        }

        Path path = this.hunter.getNavigation().getCurrentPath();
        if (path != null && !path.isFinished()) {
            for (int i = 0; i < Math.min(path.getCurrentNodeIndex() + 2, path.getLength()); i++) {
                PathNode pathNode = path.getNode(i);

                for (int o = 1; o >= 0; o--) {
                    this.targetBlockPos = new BlockPos(pathNode.x, pathNode.y + o, pathNode.z);
                    if (this.hunter.squaredDistanceTo((double)this.targetBlockPos.getX(), this.hunter.getY(), (double)this.targetBlockPos.getZ()) <= 2.25) {
                        this.targetBlockState = this.hunter.getWorld().getBlockState(this.targetBlockPos);
                        this.targetBlockValid = shouldBreakBlock(targetBlockState);
                        if (this.targetBlockValid) {
                            return true;
                        }
                    }
                }
            }

            this.targetBlockPos = this.hunter.getBlockPos().up();
            this.targetBlockState = this.hunter.getWorld().getBlockState(this.targetBlockPos);
            this.targetBlockValid = shouldBreakBlock(targetBlockState);
            return this.targetBlockValid;
        } else {
            return false;
        }
    }

    protected boolean shouldBreakBlock(BlockState state) {
        return !state.isAir() && state.getHardness(hunter.getWorld(), targetBlockPos) >= 0 && !state.isOf(Blocks.WATER);
    }

    @Override
    public void start() {
        super.start();
        this.breakProgress = 0;
        float f = (targetBlockState.getHardness(hunter.getWorld(), targetBlockPos) * 8) + 5;
        this.maxProgress = (int)(f / this.hunter.getMiningSpeed());
    }

    @Override
    public boolean shouldContinue() {
        return this.breakProgress <= this.maxProgress
                && this.targetBlockPos.isWithinDistance(this.hunter.getPos(), 2.0);
    }

    @Override
    public void stop() {
        super.stop();
        this.hunter.getWorld().setBlockBreakingInfo(this.hunter.getId(), this.targetBlockPos, -1);
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.hunter.handSwinging) {
            this.hunter.swingHand(this.hunter.getActiveHand());
        }
        this.hunter.getLookControl().lookAt(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ(), 60.0F, 60.0F);

        this.breakProgress++;
        int i = (int)((float)this.breakProgress / (float)this.maxProgress * 10.0F);
        if (i != this.prevBreakProgress) {
            this.hunter.getWorld().setBlockBreakingInfo(this.hunter.getId(), this.targetBlockPos, i);
            this.prevBreakProgress = i;
        }

        if (this.breakProgress == this.maxProgress) {
            this.hunter.getWorld().breakBlock(this.targetBlockPos, true, this.hunter);
            // this.mob.getWorld().syncWorldEvent(WorldEvents.ZOMBIE_BREAKS_WOODEN_DOOR, this.targetBlockPos, 0);
            this.hunter.getWorld().syncWorldEvent(WorldEvents.BLOCK_BROKEN, this.targetBlockPos, Block.getRawIdFromState(this.hunter.getWorld().getBlockState(this.targetBlockPos)));
        }
    }
}
