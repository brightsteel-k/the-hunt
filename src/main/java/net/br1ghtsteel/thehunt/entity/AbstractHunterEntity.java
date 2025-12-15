package net.br1ghtsteel.thehunt.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.world.World;

public class AbstractHunterEntity extends HostileEntity {

    public AbstractHunterEntity(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }


}
