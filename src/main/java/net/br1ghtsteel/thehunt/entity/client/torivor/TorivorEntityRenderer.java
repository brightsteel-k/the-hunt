package net.br1ghtsteel.thehunt.entity.client.torivor;

import net.br1ghtsteel.thehunt.TheHunt;
import net.br1ghtsteel.thehunt.entity.AbstractHunterEntity;
import net.br1ghtsteel.thehunt.entity.TorivorEntity;
import net.br1ghtsteel.thehunt.entity.client.HunterEntityRenderer;
import net.br1ghtsteel.thehunt.entity.client.ModModelLayers;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.util.Identifier;

public class TorivorEntityRenderer extends HunterEntityRenderer<TorivorEntity, TorivorEntityModel<TorivorEntity>> {
    private static final Identifier TEXTURE = new Identifier(TheHunt.MOD_ID, "textures/entity/torivor.png");

    public TorivorEntityRenderer(EntityRendererFactory.Context context) {
        this(context, ModModelLayers.TORIVOR, ModModelLayers.TORIVOR_INNER_ARMOR, ModModelLayers.TORIVOR_OUTER_ARMOR);
        // this.addFeature(new TorivorOverlayFeatureRenderer<>(this, context.getModelLoader()));
    }

    public TorivorEntityRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer, EntityModelLayer legsArmorLayer, EntityModelLayer bodyArmorLayer) {
        super(
                ctx, new TorivorEntityModel<>(ctx.getPart(layer)), new TorivorEntityModel<>(ctx.getPart(legsArmorLayer)), new TorivorEntityModel<>(ctx.getPart(bodyArmorLayer))
        );
    }

    @Override
    public Identifier getTexture(AbstractHunterEntity abstractHunterEntity) {
        return TEXTURE;
    }
}
