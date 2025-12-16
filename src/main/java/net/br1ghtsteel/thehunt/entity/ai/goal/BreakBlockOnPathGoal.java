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
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldEvents;

import java.util.EnumSet;

public class BreakBlockOnPathGoal extends Goal {
    protected MobEntity mob;
    protected BlockPos targetBlockPos = BlockPos.ORIGIN;
    protected BlockState targetBlockState;

    protected boolean targetBlockValid;
    protected int breakProgress;
    protected int prevBreakProgress = -1;
    protected int maxProgress = -1;

    public BreakBlockOnPathGoal(AbstractHunterEntity mob) {
        this.mob = mob;
        this.setControls(EnumSet.of(Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (!this.mob.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            return false;
        }

        if (!(mob.getNavigation() instanceof HunterNavigation)) {
            return false;
        } else {
            HunterNavigation hunterNavigation = (HunterNavigation)this.mob.getNavigation();
            Path path = hunterNavigation.getCurrentPath();
            if (path != null && !path.isFinished()) {
                for (int i = 0; i < Math.min(path.getCurrentNodeIndex() + 2, path.getLength()); i++) {
                    PathNode pathNode = path.getNode(i);

                    for (int o = 1; o >= 0; o--) {
                        this.targetBlockPos = new BlockPos(pathNode.x, pathNode.y + o, pathNode.z);
                        if (this.mob.squaredDistanceTo((double)this.targetBlockPos.getX(), this.mob.getY(), (double)this.targetBlockPos.getZ()) <= 2.25) {
                            this.targetBlockState = this.mob.getWorld().getBlockState(this.targetBlockPos);
                            this.targetBlockValid = shouldBreakBlock(targetBlockState);
                            if (this.targetBlockValid) {
                                return true;
                            }
                        }
                    }
                }

                this.targetBlockPos = this.mob.getBlockPos().up();
                this.targetBlockState = this.mob.getWorld().getBlockState(this.targetBlockPos);
                this.targetBlockValid = shouldBreakBlock(targetBlockState);
                return this.targetBlockValid;
            } else {
                return false;
            }
        }
    }

    protected boolean shouldBreakBlock(BlockState state) {
        return !state.isAir() && state.getHardness(mob.getWorld(), targetBlockPos) >= 0 && !state.isOf(Blocks.WATER);
    }

    @Override
    public void start() {
        super.start();
        this.breakProgress = 0;
        this.mob.getLookControl().lookAt(targetBlockPos.getX(), targetBlockPos.getY(), targetBlockPos.getZ());
        this.maxProgress = (int)(targetBlockState.getHardness(mob.getWorld(), targetBlockPos) * 8) + 5;
        TheHunt.sendChatMessage("Starting BreakBlockGoal.");
    }

    @Override
    public boolean shouldContinue() {
        return this.breakProgress <= this.maxProgress
                && this.targetBlockPos.isWithinDistance(this.mob.getPos(), 2.0);
    }

    @Override
    public void stop() {
        super.stop();
        this.mob.getWorld().setBlockBreakingInfo(this.mob.getId(), this.targetBlockPos, -1);
    }

    @Override
    public boolean shouldRunEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.mob.handSwinging) {
            this.mob.swingHand(this.mob.getActiveHand());
        }

        this.breakProgress++;
        int i = (int)((float)this.breakProgress / (float)this.maxProgress * 10.0F);
        if (i != this.prevBreakProgress) {
            this.mob.getWorld().setBlockBreakingInfo(this.mob.getId(), this.targetBlockPos, i);
            this.prevBreakProgress = i;
        }

        if (this.breakProgress == this.maxProgress) {
            this.mob.getWorld().breakBlock(this.targetBlockPos, true, this.mob);
            // this.mob.getWorld().syncWorldEvent(WorldEvents.ZOMBIE_BREAKS_WOODEN_DOOR, this.targetBlockPos, 0);
            this.mob.getWorld().syncWorldEvent(WorldEvents.BLOCK_BROKEN, this.targetBlockPos, Block.getRawIdFromState(this.mob.getWorld().getBlockState(this.targetBlockPos)));
        }
    }
}
