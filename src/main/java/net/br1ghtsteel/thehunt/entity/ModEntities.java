package net.br1ghtsteel.thehunt.entity;

import net.br1ghtsteel.thehunt.TheHunt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModEntities {

    public static final EntityType<TorivorEntity> TORIVOR_ENTITY = registerEntity("torivor",
            EntityType.Builder.create(TorivorEntity::new, SpawnGroup.MONSTER)
                    .setDimensions(0.6F, 1.95F)
                    .maxTrackingRange(8)
    );

    public static <T extends Entity> EntityType<T> registerEntity(String name, EntityType.Builder<T> entityTypeBuilder) {
        EntityType<T> entityType = entityTypeBuilder.build(name);
        return Registry.register(Registries.ENTITY_TYPE, new Identifier(TheHunt.MOD_ID, name), entityType);
    }
}
