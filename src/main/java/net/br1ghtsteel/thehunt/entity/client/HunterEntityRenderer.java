package net.br1ghtsteel.thehunt.entity.client;

import net.br1ghtsteel.thehunt.TheHunt;
import net.br1ghtsteel.thehunt.entity.AbstractHunterEntity;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.ZombieEntityModel;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class HunterEntityRenderer<T extends AbstractHunterEntity, M extends HunterEntityModel<T>> extends BipedEntityRenderer<T, M> {

    protected HunterEntityRenderer(EntityRendererFactory.Context ctx, M bodyModel, M legsArmorModel, M bodyArmorModel) {
        super(ctx, bodyModel, 0.5F);
        this.addFeature(new ArmorFeatureRenderer<>(this, legsArmorModel, bodyArmorModel, ctx.getModelManager()));
    }

    public Identifier getTexture(AbstractHunterEntity abstractHunterEntity) {
        return null;
    }
}
